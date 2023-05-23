package com.example.rhythmicmusic.model;

import lombok.Data;

/**
 * @author sgj
 * @create 2023-05-23 19:19
 */
@Data
public class Music {
    private int id;
    private String title;
    private String singer;
    private String time;
    private String url;
    private int userId;

}
