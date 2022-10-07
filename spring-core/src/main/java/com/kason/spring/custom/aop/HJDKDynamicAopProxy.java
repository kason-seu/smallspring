package com.kason.spring.custom.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

public class HJDKDynamicAopProxy implements HAopProxy, InvocationHandler {

    private Object targetBean;
    private HAdviseSupport hAdviseSupport;

    private Class<?>[] interfaces;
    public HJDKDynamicAopProxy(Object targetBean, HAdviseSupport hAdviseSupport, Class<?>[] interfaces) {
        this.targetBean = targetBean;
        this.hAdviseSupport = hAdviseSupport;
        this.interfaces = interfaces;
    }

    @Override
    public Object getProxy() {
        Class<?>[] interfaces = targetBean.getClass().getInterfaces();
        System.out.println(interfaces.length);
        System.out.println(interfaces[0].getName());

//        Class<?>[] interfaces1 = null;
//        try {
//            Class<?> aClass = Class.forName("com.kason.app.service.impl.UserServiceImpl");
//            interfaces1 = aClass.getInterfaces();
//            System.out.println(interfaces1.length);
//            System.out.println(interfaces1[0].getName());
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }

        return Proxy.newProxyInstance(targetBean.getClass().getClassLoader(), interfaces, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //

        //method.invoke(targetBean, args);

        // 得到该Method目标方法需要执行的几个切面方法
        Map<Method, List<Object>> methodAndAdviceMap = hAdviseSupport.getMethodAndAdviceMap();
        List<Object> chain = methodAndAdviceMap.get(method);

        HMethodInvocation hMethodInvocation = new HMethodInvocation(targetBean, method, args, chain);

        return hMethodInvocation.proceed();
    }
}
