package com.example.rhythmicmusic.mapper;

import com.example.rhythmicmusic.model.Music;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author sgj
 * @create 2023-06-02 19:22
 */
// 收藏音乐
@Mapper
public interface LoveMusicMapper {
    // 查询某个用户收藏的某首音乐
    Music findLoveMusicByUserIdAndMusicId(int userId, int musicId);


    // 收藏音乐
    boolean insertLoveMusic(int userId, int musicId);

    // 查询某个用户收藏的所有音乐
    List<Music> findLoveMusicByUserId(int userId);

    // 查询当前用户，指定为musicName的音乐相关信息，支持模糊查询
    List<Music> findLoveMusicByKeyAndUID(String musicName, int userId);

    // 取消收藏
    int deleteLoveMusic(int userId, int musicId);

    // 根据musicId取消收藏
    // 返回值是指取消收藏音乐的条数
    int deleteLoveMusicByMusicId(int musicId);
}
