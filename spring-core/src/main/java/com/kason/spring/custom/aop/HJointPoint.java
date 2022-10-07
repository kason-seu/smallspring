package com.kason.spring.custom.aop;

import java.lang.reflect.Method;

public interface HJointPoint {

    Object getTarget(); // 获取目标实例
    Method getMethod(); // 获取目标方法
    Object[] getArguments();// 获取目标方法参数

    Object proceed() throws Throwable;
}
