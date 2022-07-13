package com.zxy.music.entity;

import com.zxy.music.common.ResultCode;
import lombok.Data;

@Data
public class R<T> {


    private Boolean success;

    private Integer code;

    private String message;

    private T data;

    public void setCode(Integer code) {
        this.code = code;
    }

    //把构造方法私有
    private R() {}

    //成功静态方法
    public static R ok() {
        R r = new R();
        r.setSuccess(true);
        r.setCode(ResultCode.SUCCESS);
        r.setMessage("成功");
        return r;
    }

    //失败静态方法
    public static R error() {
        R r = new R();
        r.setSuccess(false);
        r.setCode(ResultCode.ERROR);
        r.setMessage("失败");
        return r;
    }

    public R success(Boolean success){
        this.setSuccess(success);
        return this;
    }

    public R message(String message){
        this.setMessage(message);
        return this;
    }

    public R code(Integer code){
        this.setCode(code);
        return this;
    }

    public R data(T t){
        this.setData(t);
        return this;
    }
}
