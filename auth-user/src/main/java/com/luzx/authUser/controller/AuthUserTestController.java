package com.luzx.authUser.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthUserTestController {


    @GetMapping("/authUser")
    public String authUser()
    {
        return "Hello AuthUser!";
    }

}
