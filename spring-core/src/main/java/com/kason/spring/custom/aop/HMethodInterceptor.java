package com.kason.spring.custom.aop;

public interface HMethodInterceptor {

    // hMethodInvocation, 它已经存储好了目标函数以及目标参数
    Object invoke(HMethodInvocation hMethodInvocation) throws Throwable;
}
