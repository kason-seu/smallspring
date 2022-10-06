package com.kason.spring.custom.beans;

public interface HBeanFactory {

    public Object getBean(String name);
    public Object getBean(Class<?> clz);
}
