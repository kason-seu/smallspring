package com.kason.spring.custom.beans;

import com.kason.spring.custom.annotation.*;
import com.kason.spring.custom.aop.HAopConfig;

import java.beans.Introspector;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HBeanDefinitionReader {

    public HBeanDefinitionReader(String... locationBeanConfig) {
        for (String basePackage : locationBeanConfig) {
            try {
                doIOC(basePackage);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    List<HAopConfig> hAopConfigs = new ArrayList<>();
    List<String> classFiles = new ArrayList<>();
    List<HBeanDefinition> beanDefinitions = new ArrayList<>();

    public List<HBeanDefinition> getBeanDefinitions() {
        return beanDefinitions;
    }

    public List<HAopConfig> gethAopConfigs() {
        return hAopConfigs;
    }

    private void doIOC(String basePackage) throws ClassNotFoundException {
        URL resource = this.getClass().getClassLoader().getResource(basePackage.replace(".", "/"));
        assert resource != null;
        File file = new File(resource.getFile());
        recursiveScan(file, basePackage);
        // 通过反射创建需要被接管的Bean
        for (String classFile : classFiles) {
            Class<?> beanClass = Class.forName(classFile.replace(".class", ""));
            if (beanClass.isAnnotationPresent(HController.class)) {

                HController hControllerAnnotation = beanClass.getDeclaredAnnotation(HController.class);
                createBeanDefiniton(beanClass, hControllerAnnotation.value());
            } else if (beanClass.isAnnotationPresent(HService.class)) {
                HService hServiceAnnotation = beanClass.getDeclaredAnnotation(HService.class);
                String beanName = createBeanDefiniton(beanClass, hServiceAnnotation.value());
                Type[] genericInterfaces = beanClass.getGenericInterfaces();
                // 获取该类继承的所有接口，因为可能会继承多个接口，所以返回的是数组
                for (Type genericInterface : genericInterfaces) {
                    HBeanDefinition beanDefinition = new HBeanDefinition();
                    beanDefinition.setBeanName(Introspector.decapitalize(((Class<?>) genericInterface).getSimpleName()));
                    beanDefinition.setBeanClassName(beanName);
                    beanDefinition.setBeanClass(beanClass);
                    beanDefinitions.add(beanDefinition);
                }
            } else if (beanClass.isAnnotationPresent(HAspect.class)) {


                Method[] declaredMethods = beanClass.getDeclaredMethods();
                HAopConfig hAopConfig = new HAopConfig();
                hAopConfig.setAspectClass(beanClass.getName());
                for (Method declaredMethod : declaredMethods) {

                    if (declaredMethod.isAnnotationPresent(HBefore.class)) {
                        HBefore declaredAnnotation = declaredMethod.getDeclaredAnnotation(HBefore.class);
                        hAopConfig.setAspectBefore(declaredMethod.getName());
                        hAopConfig.setBeforePointCut(declaredAnnotation.value());

                    } else if (declaredMethod.isAnnotationPresent(HAfterReturning.class)) {
                        HAfterReturning declaredAnnotation = declaredMethod.getDeclaredAnnotation(HAfterReturning.class);
                        hAopConfig.setAspectAfterReturning(declaredMethod.getName());
                        hAopConfig.setAfterReturningPointCut(declaredAnnotation.value());


                    } else if (declaredMethod.isAnnotationPresent(HAfterThrowing.class)) {
                        HAfterThrowing declaredAnnotation = declaredMethod.getDeclaredAnnotation(HAfterThrowing.class);
                        hAopConfig.setAspectAfterThrowing(declaredMethod.getName());
                        hAopConfig.setAfterThrowingPointCut(declaredAnnotation.value());

                    }

                }
                hAopConfigs.add(hAopConfig);
            }

        }
    }

    private String createBeanDefiniton(Class<?> beanClass, String beanName) {
        //String beanName = value;
        if (beanName == null || beanName.equals("")) {
            beanName = Introspector.decapitalize(beanClass.getSimpleName());
        }
        HBeanDefinition hBeanDefinition = new HBeanDefinition();
        hBeanDefinition.setBeanName(beanName);
        hBeanDefinition.setBeanClassName(Introspector.decapitalize(beanClass.getSimpleName()));
        hBeanDefinition.setBeanClass(beanClass);
        beanDefinitions.add(hBeanDefinition);
        return beanName;
    }

    private void recursiveScan(File file, String basePackage) {
        if (file.isDirectory()) {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                recursiveScan(f, basePackage + "." + f.getName());
            }
        } else {
            if (!file.getName().endsWith(".class")) {
                return;
            }
            classFiles.add(basePackage);
        }

    }
}
