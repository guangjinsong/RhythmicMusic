package com.example.rhythmicmusic.controller;

import com.example.rhythmicmusic.tools.Constant;
import com.example.rhythmicmusic.tools.ResponseBodyMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;

/**
 * @author sgj
 * @create 2023-05-23 19:23
 */

@RestController
@RequestMapping("/music")
public class MusicController {

    @Value("${music.local.path}")
    private String SAVE_PATH;

    @RequestMapping("/upload")
    public ResponseBodyMessage<Boolean> insertMusic(@RequestParam String singer,
                                                    @RequestParam("filename") MultipartFile file,
                                                    HttpServletRequest request) {
        // 1. 检查是否登录
        HttpSession httpSession = request.getSession(false); // false的意思是,如果没有session就不创建
        if (null == httpSession || null == httpSession.getAttribute(Constant.USERINFO_SESSION_KEY)) {
            System.out.println("没有登录");
            return new ResponseBodyMessage<>(-1, "请登录后上传", false);
        }

        // 2. 上传到服务器
        String fileName = file.getOriginalFilename();
        String path = SAVE_PATH + fileName;
        File dest = new File(path);
        if (!dest.exists()) {
            dest.mkdir();
        }
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseBodyMessage<>(-1, "上传失败", false);
        }

        // 3. 上传到数据库


    }
}
