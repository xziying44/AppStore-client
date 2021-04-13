package com.xziying.appstore.web;

import com.xziying.appstore.api.Request;
import com.xziying.appstore.control.APIRequest;
import com.xziying.appstore.plugIn.PluginPool;
import com.xziying.appstore.plugIn.domain.EventInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 主动请求
 *
 * @author : xziying
 * @create : 2021-01-22 22:55
 */

@RestController
@RequestMapping("/proactive")
public class ProactiveRequest {
    @Resource
    Request request;

    @Resource
    PluginPool pluginPool;

    @ResponseBody
    @RequestMapping("/get")
    public String get(){
        try {
            return request.obtainAPI();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "{\"code\" : 1, \"msg\" : \"error\"}";
    }

    @ResponseBody
    @RequestMapping("/set")
    public String set(
            String replyQQ,
            String apiName,
            String a,
            String b,
            String c
    ){
        try {
            String[] list = new String[3];
            int num = 0;
            if (a != null){
                list[num++] = a;
            }
            if (b != null){
                list[num++] = b;
            }
            if (c != null){
                list[num++] = c;
            }

            request.requestAPI(request.packageAPI(replyQQ, apiName, list));
            return request.getReply();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "{\"code\" : 1, \"msg\" : \"error\"}";
    }

    @ResponseBody
    @RequestMapping("/reply")
    public String reply(
            String json
    ){
        try {
            request.completeAPI(json);
            return "{\"code\" : 0, \"msg\" : \"yes\"}";
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "{\"code\" : 1, \"msg\" : \"error\"}";
    }

    @ResponseBody
    @RequestMapping("/config/**")
    public byte[] html(
            HttpServletResponse response,
            HttpServletRequest request
    ) throws IOException {
        String requestURI = request.getRequestURI();
        String[] split = requestURI.split("/");
        int status = 0; // 记录状态 1为开始记录应用名 2为开始记录url
        String appName = "";
        StringBuilder url = new StringBuilder();
        for (String u : split){
            switch (status){
                case 0:{
                    if (u.equals("config")){
                        status = 1;
                    }
                    break;
                }
                case 1 :{
                    appName = u;
                    status = 2;
                    break;
                }
                case 2:{
                    if (u.equals("api")){
                        status = 4; // 跳转apiHttp请求
                        break;
                    }
                    url.append(u);
                    status = 3;
                    break;
                }
                case 3:{
                    url.append("/").append(u);
                    break;
                }
                case 4:{
                    Map<String, String[]> parameterMap = request.getParameterMap();
                    return pluginPool.getApiHttpRequest(appName, u, parameterMap);
                }
            }
        }
        return pluginPool.getHTMLByPluginName(appName, url.toString());
    }

    /**
     * 处理事件
     * @param robotVersion 来着框架版本 1 ER框架
     * @param fromQQ 来源机器人QQ
     * @param messageType   接收到消息类型，该类型可在常量表中查询具体定义，此处仅列举： -1 未定义事件 0,在线状态临时会话 1,好友信息 2,群信息 3,讨论组信息 4,群临时会话 5,讨论组临时会话 6,财付通转账 7,好友验证回复会话
     * @param messageSubtype 此参数在不同ER_下，有不同的定义，暂定：接收财付通转账时 1待确认收款 0为已收款    有人请求入群时，不良成员这里为1
     * @param source    此消息的来源，如：群号、讨论组ID、临时会话QQ、好友QQ等
     * @param triggerActive 主动发送这条消息的QQ，踢人时为踢人管理员QQ
     * @param triggerPassive    被动触发的QQ，如某人被踢出群，则此参数为被踢出人QQ
     * @param message   此参数有多重含义，常见为：对方发送的消息内容，但当ER_消息类型为 某人申请入群，则为入群申请理由,当消息类型为财付通转账时为 原始json
     */
    @ResponseBody
    @RequestMapping("/event")
    public void event(
            int robotVersion,
            String fromQQ,
            int messageType,
            int messageSubtype,
            String source,
            String triggerActive,
            String triggerPassive,
            String message
    ){
        EventInfo eventInfo = new EventInfo(
                robotVersion,
                fromQQ,
                messageType,
                messageSubtype,
                source,
                triggerActive,
                triggerPassive,
                message
        );
        pluginPool.eventHandling(eventInfo);
    }

    @ResponseBody
    @RequestMapping("/eventJson")
    public void eventJson(
            String json
    ){
        EventInfo eventInfo = new EventInfo(json);
        pluginPool.eventHandling(eventInfo);
    }

/*    @ResponseBody
    @RequestMapping("/start")
    public String test2() throws Exception {
        pluginPool.startPlugIn("com.xziying.collectionAndForwarding");
        return "";
    }*/


}
