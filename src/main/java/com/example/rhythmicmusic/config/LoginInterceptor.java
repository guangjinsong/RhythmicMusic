package com.example.rhythmicmusic.config;

import com.example.rhythmicmusic.tools.Constant;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author sgj
 * @create 2023-06-10 15:27
 */
public class LoginInterceptor implements HandlerInterceptor {

//    // 自定义拦截器
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        HttpSession httpSession = request.getSession(false);
//        if (httpSession == null || httpSession.getAttribute(Constant.USERINFO_SESSION_KEY) == null) {
//            System.out.println("没有登录!");
//            return false;
//        }
//        return true;
//    }

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                                 Object handler) throws Exception {

            HttpSession httpSession = request.getSession(false);
            if(httpSession != null && httpSession.getAttribute(Constant.USERINFO_SESSION_KEY) != null) {
                //System.out.println("没有登录！");
                return true;
            }
            return false;
        }

}
