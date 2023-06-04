package com.example.rhythmicmusic.controller;

import com.example.rhythmicmusic.mapper.LoveMusicMapper;
import com.example.rhythmicmusic.model.Music;
import com.example.rhythmicmusic.model.User;
import com.example.rhythmicmusic.tools.Constant;
import com.example.rhythmicmusic.tools.ResponseBodyMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author sgj
 * @create 2023-06-03 14:45
 */
@RestController
@RequestMapping("/lovemusic")
public class LoveMusicController {
    @Autowired
    private LoveMusicMapper loveMusicMapper;

    @RequestMapping("/likemusic")
    public ResponseBodyMessage<Boolean> likeMusic(@RequestParam String id, HttpServletRequest request) {
        int musicId = Integer.parseInt(id);
        // 1. 检查是否登录
        HttpSession httpSession = request.getSession(false); // false的意思是,如果没有session就不创建
        if (null == httpSession || null == httpSession.getAttribute(Constant.USERINFO_SESSION_KEY)) {
            System.out.println("没有登录");
            return new ResponseBodyMessage<>(-1, "请登录后上传", false);
        }

        User user = (User)httpSession.getAttribute(Constant.USERINFO_SESSION_KEY);
        int userId = user.getId();
        if (loveMusicMapper.findLoveMusicByUserIdAndMusicId(userId, musicId) != null) {
            // 优化：取消收藏该音乐
            return new ResponseBodyMessage<>(-1, "您之前已收藏过该音乐", false);
        } else {
            if (loveMusicMapper.insertLoveMusic(userId, musicId)) {
                return new ResponseBodyMessage<>(1, "收藏成功", true);
            } else {
                return new ResponseBodyMessage<>(-1, "收藏失败", true);
            }
        }
    }

    @RequestMapping("/findlovemusic")
    public ResponseBodyMessage<List<Music>> findLoveMusic(@RequestParam(required = false) String musicName, HttpServletRequest request) {
        // 检查是否登录
        HttpSession httpSession = request.getSession(false); // false的意思是,如果没有session就不创建
        if (null == httpSession || null == httpSession.getAttribute(Constant.USERINFO_SESSION_KEY)) {
            System.out.println("没有登录");
            return new ResponseBodyMessage<>(-1, "请登录后上传", null);
        }

        User user = (User)httpSession.getAttribute(Constant.USERINFO_SESSION_KEY);
        int userId = user.getId();

        List<Music> ans = null;
        if (musicName == null) {
            System.out.println("1");
            ans  = loveMusicMapper.findLoveMusicByUserId(userId); // 查询该用户所有的音乐
        } else {
            System.out.println("2");
            ans = loveMusicMapper.findLoveMusicByKeyAndUID(musicName, userId);
            System.out.println(ans);
        }

        return new ResponseBodyMessage<>(1, "查询成功", ans);
    }

    @RequestMapping("/deletelovemusic")
    public ResponseBodyMessage<Boolean> deleteLoveMusic(@RequestParam String id, HttpServletRequest request) {
        int musicId = Integer.parseInt(id);
        // 检查是否登录
        HttpSession httpSession = request.getSession(false); // false的意思是,如果没有session就不创建
        if (null == httpSession || null == httpSession.getAttribute(Constant.USERINFO_SESSION_KEY)) {
            System.out.println("没有登录");
            return new ResponseBodyMessage<>(-1, "请登录后上传", null);
        }

        User user = (User)httpSession.getAttribute(Constant.USERINFO_SESSION_KEY);
        int userId = user.getId();

        int ret = loveMusicMapper.deleteLoveMusic(userId, musicId);
        if (ret == 1) {
            return new ResponseBodyMessage<>(0, "取消收藏成功", true);
        } else {
            return new ResponseBodyMessage<>(-1, "取消收藏失败", false);
        }
    }
}
