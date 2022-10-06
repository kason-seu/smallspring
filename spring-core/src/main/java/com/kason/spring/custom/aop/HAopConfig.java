package com.kason.spring.custom.aop;
// 存储切面元数据
public class HAopConfig {
    private String aspectClass; // 切面类的全路径 com.kason.aspect.LogAspect
    private String beforePointCut; // 切面表达式，正则表达式，用于表示对哪些类进行切面
    private String afterReturningPointCut; // 切面表达式，正则表达式，用于表示对哪些类进行切面
    private String afterThrowingPointCut; // 切面表达式，正则表达式，用于表示对哪些类进行切面
    private String aspectBefore; //前置通知
    private String aspectAfterReturning;//后置
    private String aspectAfterThrowing;

    public String getAspectClass() {
        return aspectClass;
    }

    public void setAspectClass(String aspectClass) {
        this.aspectClass = aspectClass;
    }

    public String getBeforePointCut() {
        return beforePointCut;
    }

    public void setBeforePointCut(String beforePointCut) {
        this.beforePointCut = beforePointCut;
    }

    public String getAfterReturningPointCut() {
        return afterReturningPointCut;
    }

    public void setAfterReturningPointCut(String afterReturningPointCut) {
        this.afterReturningPointCut = afterReturningPointCut;
    }

    public String getAfterThrowingPointCut() {
        return afterThrowingPointCut;
    }

    public void setAfterThrowingPointCut(String afterThrowingPointCut) {
        this.afterThrowingPointCut = afterThrowingPointCut;
    }

    public String getAspectBefore() {
        return aspectBefore;
    }

    public void setAspectBefore(String aspectBefore) {
        this.aspectBefore = aspectBefore;
    }

    public String getAspectAfterReturning() {
        return aspectAfterReturning;
    }

    public void setAspectAfterReturning(String aspectAfterReturning) {
        this.aspectAfterReturning = aspectAfterReturning;
    }

    public String getAspectAfterThrowing() {
        return aspectAfterThrowing;
    }

    public void setAspectAfterThrowing(String aspectAfterThrowing) {
        this.aspectAfterThrowing = aspectAfterThrowing;
    }
}
