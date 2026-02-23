package com.controller;

import com.annotation.IgnoreAuth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 页面跳转控制
 * 统一入口：/login -> 前台登录页
 */
@Controller
public class PageController {

    @IgnoreAuth
    @GetMapping({"/", "/login"})
    public void loginPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 重定向到登录页，保证浏览器地址为 /front/... 这样页面内相对路径(../../layui 等)才能正确加载，Vue/身份选择/注册链接才能正常显示
        response.sendRedirect("/front/pages/login/login.html");
    }
}

