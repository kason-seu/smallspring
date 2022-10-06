package com.kason.spring.custom.beans;

public class HBeanDefinition {

    private String beanName;
    private Class<?> beanClass;

    private String beanClassName; // 如果是接口，则指的是实现的名字

    public boolean isLazyInit() {
        return false;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }
}
