package com.kason.spring.custom.aop;

import java.util.List;
import java.util.regex.Pattern;

public class HAdviseSupport {


    private Object instance;
    private Class<?> clazz;
    private List<HAopConfig> hAopConfigs;

    private Pattern pointCutPattern;
    public HAdviseSupport(Object instance, Class<?> clazz, List<HAopConfig> hAopConfigs) {
        this.instance = instance;
        this.clazz = clazz;
        this.hAopConfigs = hAopConfigs;
    }

    public boolean pointCutMatchClass(){
        return true;
    }


}
