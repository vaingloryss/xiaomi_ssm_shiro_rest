package com.vainglory.controller;

import cn.dsna.util.images.ValidateCode;
import com.vainglory.pojo.User;
import com.vainglory.service.IUserService;
import com.vainglory.util.MD5Util;
import com.vainglory.util.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("/userController/")
@SessionAttributes(value = {"user"})
public class UserController {
    @Autowired
    private IUserService userService;
    @GetMapping("toLogin")
    public String toLogin(){
        return "login";
    }

    @GetMapping("toRegister")
    public String toRegister(){
        return "register";
    }

    @GetMapping("toHome")
    public String toHome(){
        return "home";
    }

    @PostMapping("register")//Post：添加
    public String register(User user, @RequestParam("repassword") String repassword, Model model){
        if (StringUtils.isEmpty(user.getUsername())){
            model.addAttribute("registerMSG","用户名不能为空");
            System.out.println("用户名不能为空");
            return "register";
        }
        if (StringUtils.isEmpty(user.getPassword())){
            model.addAttribute("registerMSG","密码不能为空");
            System.out.println("密码不能为空");
            return "register";
        }
        if (!user.getPassword().equals(repassword)){
            model.addAttribute("registerMSG","两次密码不一致");
            System.out.println("两次密码不一致");
            return "register";
        }
        if (StringUtils.isEmpty(user.getEmail())){
            model.addAttribute("registerMSG","邮箱不能为空");
            System.out.println("邮箱不能为空");
            return "register";
        }
        userService.register(user);
        return "registerSuccess";
    }

    @ResponseBody
    @PostMapping("checkUsername")//是否应该是Get请求
    public String checkUsername(String username){
        if(username==null||username.trim().length()==0){
            System.out.println("null");
            return null;
        }
        User user = userService.checkUserName(username);
        if(user!=null){
            return "1";
        }
        return "0";
    }

    @GetMapping("validateCode")
    public void validateCode(HttpServletRequest request,HttpServletResponse response){
        ValidateCode validateCode = new ValidateCode(100,40,4,10);
        request.getSession().setAttribute("code",validateCode.getCode());
        try {
            validateCode.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ResponseBody
    @PostMapping("checkValidateCode")//是否应该是Get请求
    public String checkValidateCode(String code,HttpServletRequest request){
        String result = "1";
        if (code.equalsIgnoreCase(request.getSession().getAttribute("code").toString())){
            result = "0";
        }
        System.out.println(result);
        return result;
    }

    @PostMapping("login")//登录是一个特殊的查询，必须使用Post请求，否则用户名密码会显示在URL中
    public String login(String username,String password,String RememberMe,Model model){
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username,password);
        if (RememberMe!=null){
            token.setRememberMe(true);
        }
        subject.login(token);
        model.addAttribute("user");
        User user = userService.queryUserByUsername(username);
        model.addAttribute("user",user);
        return "home";
    }

    @GetMapping("logOut")
    @RequiresAuthentication
    public String logOut(HttpServletRequest request,HttpServletResponse response){
        System.out.println("注销...");
        HttpSession session = request.getSession();
        session.removeAttribute("user");
        session.invalidate();
        Cookie cookie = new Cookie("autoUser","");
        cookie.setPath("/");
        response.addCookie(cookie);
        return "home";
    }
}
