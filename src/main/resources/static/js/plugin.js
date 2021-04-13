const PluginData = {
    plugins : []
}
const LocalPlugin = {
    plugins : [],
    error : []
}

$(function () {
    // 启动初始化
    plugin_local.queryList()
})
/*导航栏*/
const navigationBar = new Vue({
    el: "#navigationBar",
    data: function () {
        return {
            index : {
                val : 0 // 0首页 1插件商城 2本地列表 3日志信息
            }
        }
    },
    methods : {
        jump : function (index) {
            this.index.val = index
            console.log(navigationBar.index)
        }
    }
})
/*首页信息*/
const home = new Vue({
    el: "#home",
    data: function () {
        return {
            show_index: navigationBar.index
        }
    },
})
/*在线插件*/
const plugin_list = new Vue({
    el : "#plugin-list",
    data : function () {
        return {
            plugins : PluginData.plugins,
            status_info : ["已安装", "未安装", "正在下载", "正在加载", "加载异常", "下载失败"], // 分别为0 1 2 3 4 5
            show_index : navigationBar.index
        }
    },
    methods : {
        queryList : function () {
            $.get("pluginServlet/getList", function (data) {
                const json = $.parseJSON(data)
                if (json.code !== 0){
                    location.href = "/"
                } else {
                    PluginData.plugins.splice(0, PluginData.plugins.length)
                    for (let i = 0; i < json.list.length; i++) {
                        let whetherToInstall = false // 是否安装
                        for (let j = 0; j < LocalPlugin.plugins.length; j++) {
                            if (LocalPlugin.plugins[j].clazz === json.list[i].clazz){
                                whetherToInstall = true
                                break
                            }
                        }
                        if (whetherToInstall){
                            json.list[i].status = 0 // 已安装
                        } else {
                            json.list[i].status = 1 // 未安装
                        }

                        PluginData.plugins.push(json.list[i])
                        Vue.nextTick(function(){
                            layui.rate.render({
                                elem: "#score-online-" + json.list[i].pid
                                ,value: (json.list[i].score / 10.0)
                                ,half: true
                                ,readonly: true
                            });
                        })
                    }


                }
            })

        },
        download : function (pid) {
            const list = plugin_list.$data.plugins
            for (let i = 0; i < list.length; i++) {
                if (list[i].pid === pid){
                    list[i].status = 2  // 正在下载
                    $.post("pluginServlet/download", {
                        clazz : list[i].clazz,
                        url : list[i].link
                    }, function (data) {
                        const json = $.parseJSON(data)
                        if (json.code === 0){
                            list[i].status = 3 // 正在加载
                            $.post("pluginServlet/load", {
                                clazz : list[i].clazz
                            }, function (data) {
                                const json = $.parseJSON(data)
                                if (json.code === 0) {
                                    list[i].status = 0 // 已安装
                                } else {
                                    list[i].status = 4 // 加载异常
                                }
                                plugin_local.queryList()
                            })
                        } else {
                            list[i].status = 5 // 下载失败
                        }
                    })
                    break
                }
            }
        },
    }
})
/*本地插件*/
const plugin_local = new Vue({
    el : "#plugin-local-list",
    data : function () {
        return {
            plugins : LocalPlugin.plugins,
            error : LocalPlugin.error,
            show_index : navigationBar.index
        }
    },
    methods : {
        /*开关插件*/
        switchPlugIn : function(plugin, state){
            let behavior = 0
            if (state) behavior = 1
            $.post("pluginServlet/switchPlugIn", {
                clazz : plugin.clazz,
                behavior : behavior
            }, function (data) {
                const json = $.parseJSON(data)
                if (json.code !== 0){
                    layui.layer.alert(json.msg)
                    plugin_local.queryList()
                } else {
                    plugin_local.queryList()
                }
            })
        },
        queryList : function () {
            $.get("pluginServlet/getLocalList", function (data) {
                const json = $.parseJSON(data)
                LocalPlugin.plugins.splice(0, LocalPlugin.plugins.length)
                for (let i = 0; i < json.list.length; i++) {
                    json.list[i].stateBoolean = (json.list[i].state === 1)
                    LocalPlugin.plugins.push(json.list[i])
                }

                LocalPlugin.error.splice(0, LocalPlugin.error.length)
                for (let i = 0; i < json.error.length; i++) {
                    LocalPlugin.error.push(json.error[i])
                }


                // 加载网络插件列表
                plugin_list.queryList()
            })
        },
        jumpSetting : function (plugin) {
            console.log(plugin)
            window.open("/proactive/config/" + plugin.clazz + "/set.html")
        },
        managementAuthorization : function(plugin){
            $.get("pluginServlet/getVerification", function (data) {
                const json = $.parseJSON(data)
                let body = ""
                for (let i = 0; i < json.verifications.length; i++) {
                    if (json.verifications[i].clazz === plugin.clazz){
                        body += '<tr>' +
                            '<td>' + json.verifications[i].fromQQ +'</td>' +
                            '<td>' + json.verifications[i].formatStart +'</td>' +
                            '<td>' + json.verifications[i].formatEnd +'</td>' +
                            '<td><button class="layui-btn layui-btn-normal">续费</button></td>' +
                            '</tr>'
                    }
                }
                let html = '<table  class="layui-table" lay-even="" lay-skin="nob" >' +
                    '<thead>' +
                    '<tr><th>授权对象</th><th>授权时间</th><th>到期时间</th><th>操作</th></tr>' +
                    '</thead>' +
                    '<tbody>' + body + '</tbody>' +
                    '</table>'
                layui.layer.open({
                    type: 1 //Page层类型
                    ,area: ['500px', '300px']
                    ,title: '授权管理'
                    ,shade: 0.6 //遮罩透明度
                    ,anim: 0 //0-6的动画形式，-1不开启
                    ,content: html
                });
            })



        },
        swapWeight : function(plugin, behavior){
            $.post("pluginServlet/adjustPriority", {
                clazz : plugin.clazz,
                behavior : behavior
            }, function (data) {
                const json = $.parseJSON(data)
                if (json.code !== 0){
                    layui.layer.alert(json.msg)
                } else {
                    plugin_local.queryList()
                }
            })
        },
        /*重新加载本地插件*/
        refresh : function () {
            $.get("/pluginServlet/refreshThePlugin", function () {
                plugin_local.queryList()
            })
        }
    }
})