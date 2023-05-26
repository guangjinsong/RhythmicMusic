package com.example.rhythmicmusic.controller;

import com.example.rhythmicmusic.mapper.MusicMapper;
import com.example.rhythmicmusic.model.User;
import com.example.rhythmicmusic.tools.Constant;
import com.example.rhythmicmusic.tools.ResponseBodyMessage;
import org.apache.ibatis.binding.BindingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author sgj
 * @create 2023-05-23 19:23
 */

@RestController
@RequestMapping("/music")
public class MusicController {

    @Value("${music.local.path}")
    private String SAVE_PATH;

    @Autowired
    private MusicMapper musicMapper;

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

        // 2. 得到上传音乐的相关信息
        String fileName = file.getOriginalFilename();  // 文件名
        int dotOfIndex = fileName.lastIndexOf(".");
        String title = fileName.substring(0, dotOfIndex); // 音乐名
        User user = (User)httpSession.getAttribute(Constant.USERINFO_SESSION_KEY);
        int userid = user.getId();  // userid
        String url = "/music/get?path="+title;  // url 存进去的时候为了节省空间不需要加“.mp3”后缀
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");  // time
        String time = simpleDateFormat.format(new Date());

        // 3. 先查询数据库当中,是否有当前音乐, 通过歌曲名 + 歌手来判断
        //    如果歌曲名和歌手都一样, 就不可以上传
        User res = musicMapper.selectByTitleAndSinger(title, singer);
        if (null != res) {
            return new ResponseBodyMessage<>(-1, "数据库已有该歌曲, 服务器上传失败", false);
        }


        // 4. 上传到服务器
        String path = SAVE_PATH + fileName;
        File dest = new File(path);
        if (!dest.exists()) {
            dest.mkdir();

        }
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseBodyMessage<>(-1, "服务器上传失败", false);
        }

        // 5. 上传到music数据库
        try {
            int ret = musicMapper.insert(title, singer, time, url, userid);
            if (ret == 1) {
                // 这里应该跳转到音乐列表页面
                return new ResponseBodyMessage<>(0, "数据库上传成功", true);
            } else {
                return new ResponseBodyMessage<>(-1, "数据库上传失败", false);
            }
        } catch (BindingException b) {
            dest.delete();
            return new ResponseBodyMessage<>(-1, "数据库上传失败", false);
        }
    }

    // 获取音乐
    @RequestMapping("/get")
    public ResponseEntity<byte[]> get(String path) {
        File file = new File(SAVE_PATH + "/" + path);
        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(file.toPath());
            if (bytes == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(bytes);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }

    // 删除音乐
    // 1. 查询该音乐是否存在
    // 2. 如果存在，删除
    @RequestMapping("/delete")
    public ResponseBodyMessage<Boolean> delete() {

    }



}
