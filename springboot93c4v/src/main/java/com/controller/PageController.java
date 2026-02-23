package com.controller;

import com.annotation.IgnoreAuth;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面跳转控制
 * 统一入口：/login -> 前台登录页
 */
@Controller
public class PageController {

    @IgnoreAuth
    @GetMapping({"/", "/login"})
    public String loginPage() {
        // 转发到前台登录页面
        return "forward:/front/pages/login/login.html";
    }
}

