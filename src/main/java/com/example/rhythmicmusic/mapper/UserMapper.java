package com.example.rhythmicmusic.mapper;

import com.example.rhythmicmusic.model.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author sgj
 * @create 2023-05-21 20:06
 */

@Mapper
public interface UserMapper {
    public User login(User user);
    public User selectLoginByName(String username);
}
