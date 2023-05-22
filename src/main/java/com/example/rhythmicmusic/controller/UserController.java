package com.example.rhythmicmusic.controller;

import com.example.rhythmicmusic.mapper.UserMapper;
import com.example.rhythmicmusic.model.User;
import com.example.rhythmicmusic.tools.Constant;
import com.example.rhythmicmusic.tools.ResponseBodyMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author sgj
 * @create 2023-05-21 20:16
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
   private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @RequestMapping("/login")
    public ResponseBodyMessage login(@RequestParam String username, @RequestParam String password, HttpServletRequest request) {
        User userLogin = new User();
        userLogin.setUsername(username);
        userLogin.setPassword(password);


        User user = userMapper.selectLoginByName(username);
        boolean res = bCryptPasswordEncoder.matches(password,user.getPassword());
        // 规定
        // 状态码 >=0: 登录成功
        // 状态码 <0: 登录失败
        if (null != user  && res) {
            System.out.println("登录成功");
            request.getSession().setAttribute(Constant.USERINFO_SESSION_KEY, user);
            return new ResponseBodyMessage<>(0, "登录成功", userLogin);
        } else {
            System.out.println("登录失败");
            return new ResponseBodyMessage<>(-1, "登录失败", userLogin);

        }
    }
}
