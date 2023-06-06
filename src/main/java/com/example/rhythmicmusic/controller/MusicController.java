package com.example.rhythmicmusic.controller;

import com.example.rhythmicmusic.mapper.LoveMusicMapper;
import com.example.rhythmicmusic.mapper.MusicMapper;
import com.example.rhythmicmusic.model.Music;
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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

    @Autowired
    private LoveMusicMapper loveMusicMapper;

    @RequestMapping("/upload")
    public ResponseBodyMessage<Boolean> insertMusic(@RequestParam String singer,
                                                    @RequestParam("filename") MultipartFile file,
                                                    HttpServletRequest request,
                                                    HttpServletResponse resp) {
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
                // 跳转到音乐列表页面
                resp.sendRedirect("/list.html");
                return new ResponseBodyMessage<>(0, "数据库上传成功", true);
            } else {
                return new ResponseBodyMessage<>(-1, "数据库上传失败", false);
            }
        } catch (BindingException b) {
            dest.delete();
            return new ResponseBodyMessage<>(-1, "数据库上传失败", false);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
    public ResponseBodyMessage<Boolean> deleteMusicById(@RequestParam String id) {
        // 1. 先检查这个音乐是否存在
        int iid = Integer.parseInt(id);
        Music music = musicMapper.findMusicById(iid);
        if (music == null) {
            return new ResponseBodyMessage<>(-1, "没有你要删除的音乐", false);
        } else {
            // 2. 如果存在要进行删除
            // 2.1 删除数据库
            int ret = musicMapper.deleteMusicById(iid);
            if (ret == 1) {
                // 2.2 删除服务器上的数据
                String nameOfMusic = music.getTitle();
                File file = new File(SAVE_PATH + "/" + nameOfMusic + ".mp3");
                System.out.println(file.getPath());
                if (file.delete()) {
                    // 2.3 删除收藏表中的音乐
                    loveMusicMapper.deleteLoveMusicByMusicId(iid);
                    return new ResponseBodyMessage<>(0, "数据库、服务器和收藏表当中的音乐删除成功", true);
                } else {
                    return new ResponseBodyMessage<>(-1, "数据库当中的音乐删除成功，服务器当中的音乐没有删除成功", false);

                }

            } else {
                return new ResponseBodyMessage<>(-1, "数据库当中的音乐没有删除成功", false);
            }
        }

    }

    // 批量进行删除
    @RequestMapping("/deleteSel")
    public ResponseBodyMessage<Boolean> deleteSelMusic(@RequestParam("id[]") List<Integer> id) {
        for (int i = 0; i < id.size(); i++) {
            ResponseBodyMessage<Boolean> res = deleteMusicById(Integer.toString(id.get(i)));
            if (res.getStatus() < 0) {
                return new ResponseBodyMessage<>(-1, "音乐删除失败", false);
            }
        }
       return new ResponseBodyMessage<>(0, "音乐删除成功", true);

    }

    // 查询音乐
    // 支持模糊查询
    // 支持空参查询, 即查询所有的音乐
    @RequestMapping("/findmusic")
    public ResponseBodyMessage<List<Music>> findMusic(@RequestParam(required = false) String musicName) { // required = false: 默认是true, 这里设置为false, 表明可以传空
        List<Music> musicList = null;
        if (musicName == null) {
            musicList = musicMapper.findMusic();
        } else {
            musicList = musicMapper.findMusicByName(musicName);
        }

        return new ResponseBodyMessage<>(0, "查询到了相关音乐", musicList);
    }


}
