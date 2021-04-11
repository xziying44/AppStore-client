package com.xziying.appstore.abnormal;

/**
 * PlugInDoesNotExist
 *
 * @author : xziying
 * @create : 2021-04-05 13:43
 */
public class PlugInDoesNotExistException extends Exception{
    public PlugInDoesNotExistException() {
        super("插件类名不存在!");
    }
}
