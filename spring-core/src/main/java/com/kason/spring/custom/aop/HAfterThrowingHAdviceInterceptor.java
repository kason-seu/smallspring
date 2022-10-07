package com.kason.spring.custom.aop;

import java.lang.reflect.Method;

public class HAfterThrowingHAdviceInterceptor extends HAbstractAspectJAdvice implements HMethodInterceptor {

    public HAfterThrowingHAdviceInterceptor(Object aspect, Method adviceMethod) {
        super(aspect, adviceMethod);
    }

    @Override
    public Object invoke(HMethodInvocation mi) throws Throwable {
        try {
            Object returnValue = mi.proceed();
            return returnValue;
        }catch (Throwable ex) {
            invokeAdviceMethod(mi, null, ex);
            throw ex;
        }
    }
}
