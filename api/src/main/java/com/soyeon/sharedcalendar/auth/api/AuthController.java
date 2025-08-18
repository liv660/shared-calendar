package com.soyeon.sharedcalendar.auth.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    @PostMapping("/auth/login")
    public void login() {}

    @RequestMapping("/auth/callback/kakao")
    public void callback() {}
}
