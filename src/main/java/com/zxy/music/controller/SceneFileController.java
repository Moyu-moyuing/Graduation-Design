package com.zxy.music.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxy.music.entity.SceneFile;
import com.zxy.music.entity.User;
import com.zxy.music.mapper.SceneFileMapper;

import com.zxy.music.service.SceneFileService;
import com.zxy.music.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/scenefile")
public class SceneFileController {
//    @Autowired(required = false)
//    private SceneFileMapper sceneFileMapper;
    @Autowired
    private SceneFileService sceneFileService;
    @PostMapping
    public boolean save(@RequestBody SceneFile sceneFile){
        //@RequestBody 把json数据映射为对象
        //新增或更新
        return sceneFileService.saveFile(sceneFile);
    }
    //查询所有
//    @GetMapping
//    public List<SceneFile> findAll(){
//        List<SceneFile> all=sceneFileMapper.findAll();
//        return all;
//    }
    //查询所有用户的文件
    @GetMapping
    public List<SceneFile> findAllByUser(@RequestBody Integer userid){
        QueryWrapper<SceneFile> sceneFileQueryWrapper=new QueryWrapper<>();
        sceneFileQueryWrapper.eq("userid",userid);
        List<SceneFile> sceneFileList=sceneFileService.list(sceneFileQueryWrapper);
        return sceneFileList;
    }
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id){
        return sceneFileService.removeById(id);
    }
    @GetMapping("/getbyid")
    public  Map<String,Object> findById(@RequestParam Integer id){
        Map<String,Object> res=new HashMap<>();
        res.put("file",sceneFileService.getById(id));
        return res;
    }

    //分页查询
    //@RequestParam 接收 映射到路由
    // /user/page?pageNum=1&pageSize=10
    @GetMapping("/page")
    public IPage<SceneFile> findPage(@RequestParam Integer pageNum,
                                     @RequestParam Integer pageSize,
                                     @RequestParam Integer userid,
                                     @RequestParam(defaultValue = "") String filename
                                     ){
        IPage<SceneFile> page=new Page<>(pageNum,pageSize);
        QueryWrapper<SceneFile> queryWrapper=new QueryWrapper<>();
        if(!"".equals(filename)){
            queryWrapper.like("filename",filename);
        }
//       if(!"".equals(time)) {
//           queryWrapper.like("time", time);
//       }
        queryWrapper.eq("userid",userid);
        IPage<SceneFile> sceneFilePage=sceneFileService.page(page,queryWrapper);


        return sceneFilePage;

    }
}
