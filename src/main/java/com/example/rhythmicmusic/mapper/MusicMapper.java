package com.example.rhythmicmusic.mapper;

import com.example.rhythmicmusic.model.Music;
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

    // 通过歌曲名和歌手查询音乐相关信息
    public User selectByTitleAndSinger(String title, String singer);

    // 通过id查询音乐
    Music findMusicById(int id);

    // 删除音乐
    int deleteMusicById(int id);
}
