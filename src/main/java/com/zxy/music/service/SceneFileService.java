package com.zxy.music.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxy.music.entity.SceneFile;
import com.zxy.music.entity.User;
import com.zxy.music.mapper.SceneFileMapper;
import com.zxy.music.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SceneFileService extends ServiceImpl<SceneFileMapper,SceneFile> {
    public boolean saveFile(SceneFile sceneFile){
        return saveOrUpdate(sceneFile);
    }
//    @Autowired(required = false)
//    private SceneFileMapper sceneFileMapper;
//    public int save(SceneFile sceneFile){
//        if(sceneFile.getId()==null){
//            //如果user的id不存在则新增
//            return sceneFileMapper.insert(sceneFile);
//        }
//        else{
//            //如果存在则更新
//            return sceneFileMapper.update(sceneFile);
//
//        }
//
//    }
}
