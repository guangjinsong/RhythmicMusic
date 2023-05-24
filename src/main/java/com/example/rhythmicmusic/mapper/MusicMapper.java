package com.example.rhythmicmusic.mapper;

import com.example.rhythmicmusic.model.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author sgj
 * @create 2023-05-23 20:32
 */

@Mapper
public interface MusicMapper {

    // 插入音乐
    public int insert(String title, String singer, String time, String url, Integer userid);
    public User selectByTitleAndSinger(String title, String singer);
}
