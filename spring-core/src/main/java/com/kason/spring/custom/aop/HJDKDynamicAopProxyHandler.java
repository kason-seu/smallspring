package com.kason.spring.custom.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

public class HJDKDynamicAopProxyHandler implements InvocationHandler {

    private Object targetBean;
    private HAdviseSupport hAdviseSupport;

    private Class<?> clazz;

    public HJDKDynamicAopProxyHandler(Object targetBean, HAdviseSupport hAdviseSupport, Class<?> clazz) {
        this.targetBean = targetBean;
        this.hAdviseSupport = hAdviseSupport;
        this.clazz = clazz;

    }

    //@Override
//    public Object getProxy() {
//        Class<?>[] interfaces = targetBean.getClass().getInterfaces();
//        System.out.println(interfaces.length);
//        System.out.println(interfaces[0].getName());

//        Class<?>[] interfaces1 = null;
//        try {
//            Class<?> aClass = Class.forName("com.kason.app.service.impl.UserServiceImpl");
//            interfaces1 = aClass.getInterfaces();
//            System.out.println(interfaces1.length);
//            System.out.println(interfaces1[0].getName());
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }

        //return Proxy.newProxyInstance(targetBean.getClass().getClassLoader(), interfaces, this);
    //}

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //

        //method.invoke(targetBean, args);

        // 得到该Method目标方法需要执行的几个切面方法 //todo 这地方一个是实现类的方法，一个是接口的方法
        Map<Method, List<Object>> methodAndAdviceMap = hAdviseSupport.getMethodAndAdviceMap();
        List<Object> chain = methodAndAdviceMap.get(method);

        if (chain == null) {
            Method method1 = clazz.getMethod(method.getName(), method.getParameterTypes());
            chain = methodAndAdviceMap.get(method1);
            methodAndAdviceMap.put(method1, chain);
        }
        HMethodInvocation hMethodInvocation = new HMethodInvocation(targetBean, method, args, chain);

        return hMethodInvocation.proceed();
    }
}
