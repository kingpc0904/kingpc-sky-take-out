package com.sky.context;

public class BaseContext {

    //ThreadLocal为每个线程提供单独一份的存储空间，具有线程隔离的效果，只有在线程内才能获取到对应的值，线程外获取不到。
    //用户每次发送一个请求都是一个单独的线程，所以可以在解析jwt的时候顺便将用户id存储在ThreadLocal提供的存储空间内
    //等运行到后面创建用户的时候，再将当前用户的id赋值给新建用户的createUser和updateUser.

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
