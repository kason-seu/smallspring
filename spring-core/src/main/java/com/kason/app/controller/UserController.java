package com.kason.app.controller;

import com.kason.spring.custom.annotation.HAutowired;
import com.kason.spring.custom.annotation.HController;
import com.kason.spring.custom.annotation.HRequestMapping;
import com.kason.spring.custom.annotation.HRequestParam;
import com.kason.app.service.interfaces.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@HController
@HRequestMapping("/user")
public class UserController {


    @HAutowired
    UserService userService;
    @HRequestMapping("/hello")
    public void hello(@HRequestParam("user") String user, HttpServletRequest request, HttpServletResponse resp) {
        //return ;
        try {
            resp.getWriter().write("Hello"  + userService.hello());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @HRequestMapping("/test")
    public void test(@HRequestParam("user") String user, HttpServletRequest request, HttpServletResponse resp) {
        try {
            String hello = userService.hello();
            System.out.println("===" + hello);
            resp.getWriter().write("test");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
