package com.zxy.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zxy.music.entity.SceneFile;
import com.zxy.music.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

//@Mapper
public interface SceneFileMapper extends BaseMapper<SceneFile> {
//    @Select("SELECT * from scenefile")
//    List<SceneFile> findAll();
//    @Insert("INSERT into scenefile(userid,username,filename,file,fileimg) VALUES (#{userid},#{username},#{filename},#{file},#{fileimg})")
//    int insert(SceneFile sceneFile);
////    @Update("update user set name = #{name} , password = #{password} where id = #{id}")
//
//    int update(SceneFile sceneFile);
//
//    @Delete("delete from scenefile where id = #{id}")
//    Integer deleteById(@Param("id") Integer id);
//
//    @Select("select * from scenefile where userid=#{userid} limit #{pageNum},#{pageSize}")
//    List<SceneFile> selectPage(Integer pageNum, Integer pageSize,Integer userid);
//
//    @Select("select count(*) from scenefile")
//    Integer selectTotal();
//
//    @Select("select * from scenefile where userid=#{userid}")
//    List<SceneFile> findAllByUser(Integer userid);
}
