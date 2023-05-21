package com.example.rhythmicmusic.tools;

import lombok.Data;

/**
 * @author sgj
 * @create 2023-05-21 20:41
 */

// 通用响应
@Data
public class ResponseBodyMessage <T>{
    private int status; // 状态码
    private String message; // 返回的信息
    private T data; // 返回前端的数据

    public ResponseBodyMessage(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
