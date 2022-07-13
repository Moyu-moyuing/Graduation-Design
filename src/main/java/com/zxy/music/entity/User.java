package com.zxy.music.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
//包括所有实体类方法
@TableName(value = "user")
//user表与User实体对应
public class User {
    @TableId(type = IdType.AUTO)//告诉主键
    private Integer id;
    private String name;

    private String password;
//    @TableField(value = "time")取别名
    private Timestamp time;


}
