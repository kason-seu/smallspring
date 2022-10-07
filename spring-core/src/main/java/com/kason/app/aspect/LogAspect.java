package com.kason.app.aspect;

import com.kason.spring.custom.annotation.HAfterReturning;
import com.kason.spring.custom.annotation.HAfterThrowing;
import com.kason.spring.custom.annotation.HAspect;
import com.kason.spring.custom.annotation.HBefore;

@HAspect
public class LogAspect {


    @HBefore(value = "com.kason.app.service.impl")
    public void before() {
        System.out.println("执行切面Before逻辑");
    }
    @HAfterReturning(value = "com.kason.app.service.impl")
    public void afterReturn() {
        System.out.println("执行切面HAfterReturning逻辑");
    }

    @HAfterThrowing(value = "com.kason.app.service.impl")
    public void afterThrow() {
        System.out.println("执行切面HAfterThrowing逻辑");
    }
}
