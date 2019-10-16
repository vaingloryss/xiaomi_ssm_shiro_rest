package com.vainglory.interceptor;

import com.vainglory.pojo.User;
import com.vainglory.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author vaingloryss
 * @date 2019/10/13 0013 下午 12:08
 */
@Component
public class AutoLoginInterceptor implements HandlerInterceptor {

    @Autowired
    IUserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //HttpServletRequest request = (HttpServletRequest) req;
        //HttpServletResponse response = (HttpServletResponse) resp;

       /*
        非管理员页面
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String p = requestURI.substring(contextPath.length() + 1);


        if ("userController".equals(p) || !p.contains("admin")){}

        */
        User loginUser = (User) request.getSession().getAttribute("user");
        if (loginUser==null){
            //用户还没有进行登录
            String username = "";
            String password = "";
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                if ("autoUser".equals(cookie.getName())){
                    String loginInfo = cookie.getValue();
                    String[] infos = loginInfo.split(":");
                    if (infos.length>1){

                        username = infos[0];
                        password = infos[1];
                    }
                }
            }
            //IUserService userService = new UserServiceImpl();
            User user = userService.checkUserName(username);

            if (user!=null){
                //用户已经进行了登录
                System.out.println(user.toString());
                if (user.getPassword().equals(password)){
                    request.getSession().setAttribute("user",user);
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
