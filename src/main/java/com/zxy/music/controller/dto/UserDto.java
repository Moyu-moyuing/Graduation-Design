package com.zxy.music.controller.dto;

import lombok.Data;

/*用来接收前端的用户登录数据*/
@Data
public class UserDto {
    private String username;
    private String password;
}
