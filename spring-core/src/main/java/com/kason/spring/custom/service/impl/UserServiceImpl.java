package com.kason.spring.custom.service.impl;

import com.kason.spring.custom.annotation.HService;
import com.kason.spring.custom.service.UserService;

@HService
public class UserServiceImpl implements UserService {
    @Override
    public String hello() {
        return "hello";
    }
}
