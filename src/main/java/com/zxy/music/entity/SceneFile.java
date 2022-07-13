package com.zxy.music.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.Data;

import java.sql.Timestamp;
@Data
@TableName(value = "scenefile")
public class SceneFile {
    @TableId//指定主键
    private Integer id;
    private Integer userid;
    private String username;
    private String filename;
//插入json格式字符串
    //@TableField("file")
    private String file;
    //entity.setAttr(JSONObject.toJSONString(dto));
    //
    ////手动解析
    //ClusterQStorageDTO dto = JSONObject.parseObject(entity.getAttr(), ClusterQStorageDTO.class);

    private String fileimg;
    private Timestamp time;

}
