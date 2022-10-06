package com.kason.spring.custom.context;

import com.kason.spring.custom.annotation.HAutowired;
import com.kason.spring.custom.beans.HBeanDefinition;
import com.kason.spring.custom.beans.HBeanDefinitionReader;
import com.kason.spring.custom.beans.HBeanFactory;

import java.beans.Introspector;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class HApplicationContext implements HBeanFactory {
    private String configLocation;
    private List<HBeanDefinition> beanDefinitions;
    public HApplicationContext(String configLocation) {
        this.configLocation = configLocation;
        System.out.println("config location " + configLocation);

        String scanPackage = doParseConfig(configLocation);
        HBeanDefinitionReader beanDefinitionReader = new HBeanDefinitionReader(scanPackage);
        beanDefinitions = beanDefinitionReader.getBeanDefinitions();
        createBean(beanDefinitions);
        populateBean();
    }

    private void populateBean() {
        beanOriginMap.forEach((beanName, beanInstance) -> {

            for (Field beanField : beanInstance.getClass().getDeclaredFields()) {

                if (beanField.isAnnotationPresent(HAutowired.class)) {
                    HAutowired fieldAnnotation = beanField.getDeclaredAnnotation(HAutowired.class);
                    String fieldName = fieldAnnotation.value();
                    if (fieldName == null || "".equals(fieldName)) {
                        String beanFieldName = beanField.getName();
                        fieldName = Introspector.decapitalize(beanFieldName);
                    }
                    Object fieldInstance = beanOriginMap.get(fieldName);
                    if (fieldInstance == null) {
                        throw new NullPointerException("注入的bean为空");
                    }
                    beanField.setAccessible(true);
                    try {
                        beanField.set(beanInstance, fieldInstance);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        });
    }

    private String doParseConfig(String configLocation) {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(configLocation);

        Properties p = new Properties();
        String basePackage = "";
        try {
            p.load(resourceAsStream);
            System.out.println("scan base package " + basePackage);
            basePackage = (String)p.get("basePackage");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return basePackage;
    }
    ConcurrentMap<String, Object> beanOriginMap = new ConcurrentHashMap<>();
    ConcurrentMap<String, Object> beanWrappedMap = new ConcurrentHashMap<>();
    private void createBean(List<HBeanDefinition> beanDefinitions) {
        beanDefinitions.forEach(beanDefinition -> {
            Object beanInstance = beanOriginMap.get(beanDefinition.getBeanName());
            if (beanInstance == null) {
                Object bean = beanOriginMap.get(beanDefinition.getBeanClassName());
                if (bean != null) {
                    beanOriginMap.put(beanDefinition.getBeanName(), bean);
                } else {
                    try {
                        beanOriginMap.put(beanDefinition.getBeanName(), beanDefinition.getBeanClass().getDeclaredConstructor().newInstance());
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        });
    }

    @Override
    public Object getBean(String beanName) {
        return beanOriginMap.get(beanName);
    }

    @Override
    public Object getBean(Class<?> clz) {
        return beanOriginMap.get(Introspector.decapitalize(clz.getSimpleName()));
    }


    public List<Class<?>> getBeanClass() {
        return beanDefinitions.stream().map(HBeanDefinition::getBeanClass).collect(Collectors.toList());
    }
}
