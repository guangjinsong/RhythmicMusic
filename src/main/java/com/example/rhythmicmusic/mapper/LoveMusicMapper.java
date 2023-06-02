package com.example.rhythmicmusic.mapper;

import com.example.rhythmicmusic.model.Music;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author sgj
 * @create 2023-06-02 19:22
 */
// 收藏音乐
@Mapper
public interface LoveMusicMapper {
    // 收藏音乐
    Music findLoveMusic(int userId, int musicId);
    // 查询收藏音乐
    boolean insertLoveMusic(int userId, int musicId);
}
