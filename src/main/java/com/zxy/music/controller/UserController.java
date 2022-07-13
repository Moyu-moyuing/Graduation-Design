package com.zxy.music.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxy.music.controller.dto.UserDto;
import com.zxy.music.entity.R;
import com.zxy.music.entity.User;
import com.zxy.music.mapper.UserMapper;
import com.zxy.music.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
//    @Autowired(required = false)
//    private UserMapper userMapper;
    @Autowired
    private UserService userService;
    @PostMapping
    public R save(@RequestBody User user){
        //@RequestBody 把json数据映射为对象
        //新增或更新

        if(userService.getIdByName(user.getName())>0){
            return R.error().data("用户名已存在");
        }
        else{
            if(userService.saveUser(user)){
                return  R.ok().data("成功");
            }else{
                return R.error().data("失败");
            }
        }
    }
    //查询所有
    @GetMapping
    public List<User> findAll(){
       return userService.list();
    }
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id){
        return userService.removeById(id);
    }

    @PostMapping("/login")
    public R login(@RequestBody UserDto userDto){
        //@RequestBody 把json数据映射为对象
        //登录验证
        String username=userDto.getUsername();
        String password=userDto.getPassword();
        if(StrUtil.isBlank(username) || StrUtil.isBlank(password)){
            return R.error().data("用户名或密码不能为空");
        }
        return userService.login(userDto);
    }

    @GetMapping("/info")
    public R info(HttpServletRequest request){
        return userService.getInfo(request);
    }

    @GetMapping("/getid")
    public Map<String,Object> getIdByName(@RequestParam String name){
        Map<String,Object> res=new HashMap<>();
        res.put("id",userService.getIdByName(name));
        return res;

    }

    //分页查询
    //@RequestParam 接收 映射到路由
    // /user/page?pageNum=1&pageSize=10
//    @GetMapping("/page")
//    public Map<String,Object> findPage(@RequestParam Integer pageNum,@RequestParam Integer pageSize){
//        pageNum=(pageNum -1)*pageSize;
//        List<User> data=userMapper.selectPage(pageNum,pageSize);
//        Integer total=userMapper.selectTotal();
//        Map<String,Object> res=new HashMap<>();
//        res.put("data",data);
//        res.put("total",total);
//
//        return res;
//    }
    @GetMapping("/page")
    public IPage<User>  findPage(@RequestParam Integer pageNum,@RequestParam Integer pageSize){
        IPage<User> page=new Page<>(pageNum,pageSize);
      QueryWrapper<User> queryWrapper=new QueryWrapper<>();
//        queryWrapper.eq("userid",userid);
        IPage<User> userPage=userService.page(page,queryWrapper);


        return userPage;
    }
}
