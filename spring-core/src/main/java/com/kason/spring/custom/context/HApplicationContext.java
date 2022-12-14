package com.kason.spring.custom.context;

import com.kason.app.service.interfaces.UserService;
import com.kason.spring.custom.annotation.HAutowired;
import com.kason.spring.custom.aop.HAdviseSupport;
import com.kason.spring.custom.aop.HAopConfig;
import com.kason.spring.custom.aop.HJDKDynamicAopProxy;
import com.kason.spring.custom.aop.HJDKDynamicAopProxyHandler;
import com.kason.spring.custom.beans.HBeanDefinition;
import com.kason.spring.custom.beans.HBeanDefinitionReader;
import com.kason.spring.custom.beans.HBeanFactory;

import java.beans.Introspector;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class HApplicationContext implements HBeanFactory {
    private String configLocation;
    private List<HBeanDefinition> beanDefinitions;
    private HBeanDefinitionReader beanDefinitionReader;
    public HApplicationContext(String configLocation) {
        this.configLocation = configLocation;
        System.out.println("config location " + configLocation);

        String scanPackage = doParseConfig(configLocation);
        beanDefinitionReader = new HBeanDefinitionReader(scanPackage);
        beanDefinitions = beanDefinitionReader.getBeanDefinitions();
        createBean(beanDefinitions);



        populateBean();
        System.out.println("=====");
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
                        throw new NullPointerException("?????????bean??????");
                    }
                    beanField.setAccessible(true);
                    try {
                        beanField.set(beanInstance, (UserService)fieldInstance);
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
               // Object bean = beanOriginMap.get(beanDefinition.getBeanClassName());
//                if (bean != null) {
//                    beanOriginMap.put(beanDefinition.getBeanName(), bean);
//                } else {
                    try {
                        Object beanObj = beanDefinition.getBeanClass().getDeclaredConstructor().newInstance();
                        List<HAopConfig> hAopConfigs = beanDefinitionReader.gethAopConfigs();
                        HAdviseSupport hAdviseSupport = new HAdviseSupport(beanObj, beanDefinition.getBeanClass(), hAopConfigs);
                        if (hAdviseSupport.pointCutMatchClass() && !beanDefinition.getBeanName().endsWith("Impl")) {
                            System.out.println("???????????????????????????????????????????????????");
                            hAdviseSupport.methodAndAdviceMapping();
                            // ?????????bean????????????
                            if (beanObj.getClass().getInterfaces().length > 0) {
                                        Class<?>[] interfaces1 = null;
        try {
            Class<?> aClass = Class.forName("com.kason.app.service.impl.UserServiceImpl");
            interfaces1 = aClass.getInterfaces();
            System.out.println(interfaces1.length);
            System.out.println(interfaces1[0].getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
                                //HJDKDynamicAopProxy hjdkDynamicAopProxy = new HJDKDynamicAopProxy(beanObj.getClass(), hAdviseSupport, interfaces1);
                                //UserService proxy =  (UserService) hjdkDynamicAopProxy.getProxy();
                                //beanOriginMap.put(beanDefinition.getBeanName(), proxy);
                                Class<?>[] interfaces = beanObj.getClass().getInterfaces();
                                beanOriginMap.put(beanDefinition.getBeanName(),Proxy.newProxyInstance(beanObj.getClass().getClassLoader(), interfaces,
                                        new HJDKDynamicAopProxyHandler(beanObj, hAdviseSupport, beanObj.getClass())));
                            }
                        } else {
                            beanOriginMap.put(beanDefinition.getBeanName(), beanObj);
                        }
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
               // }

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
