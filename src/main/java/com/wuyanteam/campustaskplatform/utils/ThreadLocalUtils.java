package com.wuyanteam.campustaskplatform.utils;

/**
 * ThreadLocal 工具类  提供线程的局部变量，用来存储数据： set()/get() 使用threadlocal存储的数据，线程安全
 */
@SuppressWarnings("all")
public class ThreadLocalUtils {
    //提供ThreadLocal对象,
    private static final ThreadLocal THREAD_LOCAL = new ThreadLocal();

    //根据键获取值
    public static <T> T get(){
        return (T) THREAD_LOCAL.get();
    }

    //存储键值对
    public static void set(Object value){
        THREAD_LOCAL.set(value);
    }

    //清除ThreadLocal 防止内存泄漏
    public static void remove(){
        THREAD_LOCAL.remove();
    }
}

