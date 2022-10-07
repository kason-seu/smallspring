package com.kason.app.service.impl;

import com.kason.spring.custom.annotation.HService;
import com.kason.app.service.interfaces.UserService;

@HService
public class UserServiceImpl implements UserService {
    @Override
    public String hello() {
        System.out.println("切面之间执行业务逻辑");
        return "hello";
    }
}
