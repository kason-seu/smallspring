package com.kason.app.service.impl;

import com.kason.spring.custom.annotation.HService;
import com.kason.app.service.UserService;

@HService
public class UserServiceImpl implements UserService {
    @Override
    public String hello() {
        return "hello";
    }
}
