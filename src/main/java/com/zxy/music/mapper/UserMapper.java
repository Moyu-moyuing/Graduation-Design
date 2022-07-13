package com.zxy.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zxy.music.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

//@Mapper
public interface UserMapper extends BaseMapper<User> {

//    @Select("SELECT * from user")
//    List<User> findAll();
//    @Insert("INSERT into user(name,password) VALUES (#{name},#{password})")
//    int insert(User user);
////    @Update("update user set name = #{name} , password = #{password} where id = #{id}")
//
//    int update(User user);
//
//    @Delete("delete from user where id = #{id}")
//    Integer deleteById(@Param("id") Integer id);
//
//    @Select("select * from user limit #{pageNum},#{pageSize}")
//    List<User> selectPage(Integer pageNum, Integer pageSize);
//
//    @Select("select count(*) from user")
//    Integer selectTotal();
}
