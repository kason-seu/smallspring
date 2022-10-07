package com.kason.spring.custom.aop;

import com.kason.spring.custom.annotation.HAfterReturning;
import com.kason.spring.custom.annotation.HAfterThrowing;
import com.kason.spring.custom.annotation.HBefore;
import jdk.swing.interop.SwingInterOpUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class HAdviseSupport {


    private Object targetInstance;
    private Class<?> targetClazz;
    private List<HAopConfig> hAopConfigs;

    private Pattern pointCutPattern;
    public HAdviseSupport(Object targetInstance, Class<?> targetClazz, List<HAopConfig> hAopConfigs) {
        this.targetInstance = targetInstance;
        this.targetClazz = targetClazz;
        this.hAopConfigs = hAopConfigs;
    }

    public boolean pointCutMatchClass(){

        for (HAopConfig hAopConfig : hAopConfigs) {

            String targetClazzName = targetClazz.getName();
            String beforePointCut = hAopConfig.getBeforePointCut();
            String afterReturningPointCut = hAopConfig.getAfterReturningPointCut();
            String afterThrowingPointCut = hAopConfig.getAfterThrowingPointCut();

            // 目标类是否符合切面表达式规则
            if (targetClazzName.contains(beforePointCut) || targetClazzName.contains(afterThrowingPointCut) || targetClazzName.contains(afterReturningPointCut)) {
                return true;
            }

        }
        return false;
    }


    // 存储Method与切面通知Before应关系
    Map<Method, List<Object>> methodAndAdviceMap = new HashMap<>();
    public void methodAndAdviceMapping() {
        // 获取该target对象的方法， 就是UserServiceImpl里的hello
        Method[] targetDeclaredMethods = targetClazz.getDeclaredMethods();
        for (Method targetDeclaredMethod : targetDeclaredMethods) {

            Class<?>[] interfaces = targetDeclaredMethod.getDeclaringClass().getInterfaces();
            String methodName = targetDeclaredMethod.getDeclaringClass().getName();
            List<Object> hAdvices = new ArrayList<>(); // 存储方法对应的所有切面通知
            for (HAopConfig hAopConfig : hAopConfigs) {
                String beforePointCut = hAopConfig.getBeforePointCut();
                String afterReturningPointCut = hAopConfig.getAfterReturningPointCut();
                String afterThrowingPointCut = hAopConfig.getAfterThrowingPointCut();

                String aspectClassName = hAopConfig.getAspectClass();
                Class<?> aspectClz = null;
                Object aspectInstance = null;
                try {
                    aspectClz = Class.forName(aspectClassName);
                    aspectInstance = aspectClz.getDeclaredConstructor().newInstance();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
                Method[] declaredMethods = aspectClz.getDeclaredMethods();
                // 目标类是否符合切面表达式规则
                if (methodName.contains(beforePointCut) || methodName.contains(afterThrowingPointCut) || methodName.contains(afterReturningPointCut)) {
                        for (Method declaredMethod : declaredMethods) {
                            if (declaredMethod.isAnnotationPresent(HBefore.class)) {
                                hAdvices.add(new HBeforeHAdviceInterceptor(aspectInstance, declaredMethod));

                            }
                            if (declaredMethod.isAnnotationPresent(HAfterReturning.class)) {
                                hAdvices.add(new HAfterReturningHAdviceInterceptor(aspectInstance, declaredMethod));

                            }
                            if (declaredMethod.isAnnotationPresent(HAfterThrowing.class)) {
                                hAdvices.add(new HAfterThrowingHAdviceInterceptor(aspectInstance, declaredMethod));

                            }
                        }
                }
            }
            methodAndAdviceMap.put(targetDeclaredMethod, hAdvices);
        }
        System.out.println(methodAndAdviceMap);
    }



    public Map<Method, List<Object>> getMethodAndAdviceMap() {
        return methodAndAdviceMap;
    }
}
