package com.kason.spring.custom.aop;

import java.lang.reflect.Method;
import java.util.List;

public class HMethodInvocation implements HJointPoint{

    private Object target;
    private Method method;
    private Object[] arguments;

    private List<Object> chain;
    public HMethodInvocation(Object targetBean, Method method, Object[] args, List<Object> chain) {

        this.target = targetBean;
        this.method = method;
        this.arguments = args;
        this.chain = chain;
    }

    @Override
    public Object getTarget() {
        return target;
    }

    private int currentIndexInterceptor = -1;
    @Override
    public Object proceed() throws Throwable{
        if (currentIndexInterceptor == chain.size() - 1) {
            return method.invoke(target, arguments);
        }
        Object hAdvice = this.chain.get(++currentIndexInterceptor);
        if (hAdvice instanceof HMethodInterceptor hMethodInterceptor) {
            return hMethodInterceptor.invoke(this);
        } else {
            return proceed();
        }
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }



    public List<Object> getChain() {
        return chain;
    }
}
