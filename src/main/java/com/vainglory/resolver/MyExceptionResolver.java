package com.vainglory.resolver;

import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyExceptionResolver implements HandlerExceptionResolver{
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        System.out.println(ex.getClass());
        String loginMSG = "";
        ex.printStackTrace();//开发时必需
        ModelAndView mv = new ModelAndView();
        if(ex instanceof IncorrectCredentialsException || ex instanceof UnknownAccountException){
            //跳转登录页面，重新登录
            loginMSG = "用户名或密码不正确...";
            mv.addObject("loginMSG",loginMSG);
            mv.setViewName("login");
        }else if (ex instanceof UnauthenticatedException){
            mv.setViewName("login");
        }
        return mv;
    }
}
