package com.kason.spring.custom.beans;

/**
 * 装饰着模式，装饰器
 */
public class HBeanWrapper {

    private Object wrapperBeanInstance;
    private Class<?> clazz;

    public HBeanWrapper(Object beanInstance) {
        this.wrapperBeanInstance = beanInstance;
    }
}
