package com.zxy.music.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxy.music.controller.dto.UserDto;
import com.zxy.music.entity.R;
import com.zxy.music.entity.User;
import com.zxy.music.mapper.UserMapper;
import com.zxy.music.mongodb.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService extends ServiceImpl<UserMapper,User> {
    public boolean saveUser(User user){
//        if(user.getId()==null){
//            return save(user);
//            //mybatis-plus提供
//        }else{
//            return updateById(user);
//        }
//        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
//        queryWrapper.eq("name",user.getName());
//        User u=getOne(queryWrapper);

        if(user.getId()==0){
            user.setId(Math.toIntExact(count() + 1));
        }
        return saveOrUpdate(user);

    }

    public R login(UserDto userDto) {
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("name",userDto.getUsername());
        User u=getOne(queryWrapper);

        if(u==null){
            return R.error().data("用户不存在");
        }
        if(u.getName().equals(userDto.getUsername())){
            if(u.getPassword().equals(userDto.getPassword())){
                Map map=new HashMap<>();
                map.put("token", JwtUtils.getJwtToken(String.valueOf(u.getId()),u.getName()));
                return R.ok().data(map);
            }
            else return R.error().data("密码错误");
        }
        else return R.error().data("用户名输入错误");

    }

    public R getInfo(HttpServletRequest request) {
        // 获取jwt解析的信息（用户的id）
        String memberIdByJwtToken = JwtUtils.getMemberIdByJwtToken(request);
        // 根据id，查询用户的信息，并将他放入data数据中
        Integer id= Integer.valueOf(memberIdByJwtToken);
        User user=getById(id);
        // 存储用户信息到响应体
        Map map = new HashMap<>();
        map.put("name",user.getName());
        map.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        return R.ok().data(map);

    }

    public Integer getIdByName(String name) {
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("name",name);
        User user=getOne(queryWrapper);
        return user.getId();
    }


//    @Autowired(required = false)
//    private UserMapper userMapper;
//    public int save(User user){
//        if(user.getId()==null){
//            //如果user的id不存在则新增
//            return userMapper.insert(user);
//        }
//        else{
//            //如果存在则更新
//            return userMapper.update(user);
//
//        }
//
//    }
}
