package com.kason.spring.custom.aop;

import java.lang.reflect.Method;

public class HAfterReturningHAdviceInterceptor extends HAbstractAspectJAdvice implements HMethodInterceptor {

    public HAfterReturningHAdviceInterceptor(Object aspect, Method adviceMethod) {
        super(aspect, adviceMethod);
    }

    @Override
    public Object invoke(HMethodInvocation mi) throws Throwable {

        Object returnValue = mi.proceed();

        this.afterReturning(mi, returnValue);
        return returnValue;
    }

    private void afterReturning(HMethodInvocation mi, Object returnValue) throws Throwable {
        invokeAdviceMethod(mi, returnValue,  null);
    }
}
