package com.kason.spring.custom.controller;

import com.kason.spring.custom.annotation.HAutowired;
import com.kason.spring.custom.annotation.HController;
import com.kason.spring.custom.annotation.HRequestMapping;
import com.kason.spring.custom.annotation.HRequestParam;
import com.kason.spring.custom.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@HController
@HRequestMapping("/user")
public class UserController {


    @HAutowired
    UserService userService;
    @HRequestMapping("/hello")
    public String hello(@HRequestParam("user") String name) {
        return "Hello" + name;

    }


    @HRequestMapping("/test")
    public void test(@HRequestParam("user") String user, HttpServletRequest request, HttpServletResponse resp) {
        try {
            resp.getWriter().write("test");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
