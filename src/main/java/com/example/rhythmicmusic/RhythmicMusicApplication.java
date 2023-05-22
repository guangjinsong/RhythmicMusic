package com.example.rhythmicmusic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


// 因为我们只是用到security这个依赖下的一个类（仅此而已， 不是用到这个框架！！！），所以再注解中得加exclude参数
// 否则，security就会保护所有的接口，间而再不做处理的情况下就会登录失败
@SpringBootApplication(exclude =
        {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
public class RhythmicMusicApplication {

    public static void main(String[] args) {
        SpringApplication.run(RhythmicMusicApplication.class, args);
    }

}
