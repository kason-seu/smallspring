package com.kason.spring.custom.aop;

import java.lang.reflect.Method;

public class HBeforeHAdviceInterceptor extends HAbstractAspectJAdvice implements HMethodInterceptor {

    public HBeforeHAdviceInterceptor(Object aspect, Method adviceMethod) {
        super(aspect, adviceMethod);
    }

    @Override
    public Object invoke(HMethodInvocation mi) throws Throwable {
        this.before(mi);
        return mi.proceed();
    }

    private void before(HMethodInvocation mi) throws Throwable {
        invokeAdviceMethod(mi,null,null);
    }
}
