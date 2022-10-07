package com.kason.spring.custom.aop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public abstract class HAbstractAspectJAdvice implements HAdvice {

    private Object aspect; // 从属哪个切面
    private Method adviceMethod; // 是哪个通知方法

    public HAbstractAspectJAdvice(Object aspect, Method adviceMethod) {
        this.aspect = aspect;
        this.adviceMethod = adviceMethod;
    }

    public Object invokeAdviceMethod(HJointPoint jointPoint, Object returnValue, Throwable ex) throws Throwable {

        Class<?>[] parameterTypes = this.adviceMethod.getParameterTypes();
        if (null == parameterTypes || parameterTypes.length == 0) {

            return this.adviceMethod.invoke(aspect);

        } else {
            Object[] args = new Object[parameterTypes.length];
            for (int i = 0; i < args.length; i++) {

                if (parameterTypes[i] == HJointPoint.class) {
                    args[i] = jointPoint;
                } else if (parameterTypes[i] == Object.class) {
                    args[i] = returnValue;
                } else if (parameterTypes[i] == Throwable.class) {
                    args[i] = ex;
                }

            }

            return this.adviceMethod.invoke(aspect, args);

        }

    }
}
