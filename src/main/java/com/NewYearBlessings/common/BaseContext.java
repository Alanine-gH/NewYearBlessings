package com.NewYearBlessings.common;

/**
 * 基于ThreadLocal封装工具类，用于保存和获取当前登录用户id
 *
 * @author Alanine
 */
import lombok.extern.slf4j.Slf4j;

/**
 * 基于ThreadLocal封装工具类，用于保存和获取当前登录用户id
 *
 * @author Alanine
 */
@Slf4j
public class BaseContext {
    private static ThreadLocal<Long> userId = new ThreadLocal<>();
    private static ThreadLocal<String> realName = new ThreadLocal<>();
    private static ThreadLocal<String> username = new ThreadLocal<>();

    public static void setUserId(Long id){
        userId.set(id);
    }
    public static Long getUserId(){
        return userId.get();
    }
    public static void removeUserId(){
        userId.remove();
    }

    public static void setRealName(String name){
        realName.set(name);
    }
    public static String getRealName(){
        return realName.get();
    }
    public static void removeRealName(){
        realName.remove();
    }

    public static void setUsername(String name){
        username.set(name);
    }
    public static String getUsername(){
        return username.get();
    }
    public static void removeUsername(){
        username.remove();
    }

}
