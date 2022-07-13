package com.zxy.music.mongodb.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.result.DeleteResult;

import com.zxy.music.mongodb.domain.FileDocument;
import com.zxy.music.mongodb.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * @Author 三分恶
 * @Date 2020/1/11
 * @Description
 */
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;


    /**
     * 保存文件
     * @param file
     * @return
     */
    @Override
    public FileDocument saveFile(FileDocument file) {
        file = mongoTemplate.save(file);
        return file;
    }

    /**
     * 上传文件到Mongodb的GridFs中
     * @param in
     * @param contentType
     * @return
     */
    @Override
    public String uploadFileToGridFS(InputStream in , String contentType){
        String gridfsId = IdUtil.simpleUUID();
        //将文件存储进GridFS中
        gridFsTemplate.store(in, gridfsId , contentType);
        return gridfsId;
    }


    /**
     * 删除单个文件
     * @param id
     */
    @Override
    public void removeFile(String id) {
        //根据id查询文件
        FileDocument fileDocument = mongoTemplate.findById(id , FileDocument.class );
        if(fileDocument!=null){
            //根据文件ID删除fs.files和fs.chunks中的记录
            Query deleteFileQuery = new Query().addCriteria(Criteria.where("filename").is(fileDocument.getGridfsId()));
            gridFsTemplate.delete(deleteFileQuery);
            //删除集合fileDocment中的数据
            Query deleteQuery=new Query(Criteria.where("id").is(id));
            mongoTemplate.remove(deleteQuery,FileDocument.class);
        }
    }

    /**
     * 删除多个文件，id为带;的字符串(在数据库中的表)
     * @param ids
     */
    @Override
    public void removeFileByIds(String ids) {
        if(ids==null || ids.equals("")){
            return;
        }else{
            String[] split = ids.split(";");
            for(String item:split){
                this.removeFile(item);
            }
        }
    }

    /**
     * 根据id查看文件
     * @param id
     * @return
     */
    @Override
    public Optional<FileDocument> getFileById(String id){
        FileDocument fileDocument = mongoTemplate.findById(id , FileDocument.class );
        if(fileDocument != null){
            Query gridQuery = new Query().addCriteria(Criteria.where("filename").is(fileDocument.getGridfsId()));
            try {
                //根据id查询文件
                GridFSFile fsFile = gridFsTemplate.findOne(gridQuery);
                //打开流下载对象
                GridFSDownloadStream in = gridFSBucket.openDownloadStream(fsFile.getObjectId());
                if(in.getGridFSFile().getLength() > 0){
                    //获取流对象
                    GridFsResource resource = new GridFsResource(fsFile, in);
                    //获取数据
                    fileDocument.setContent(IoUtil.readBytes(resource.getInputStream()));
                    return Optional.of(fileDocument);
                }else {
                    fileDocument = null;
                    return Optional.empty();
                }
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
        return Optional.empty();
    }

    /**
     * 根据id查询文件简要信息
     * @param id
     * @return
     */
    @Override
    public Optional<FileDocument> getFileInfoById(String id){
        FileDocument fileDocument = mongoTemplate.findById(id , FileDocument.class );

        if(fileDocument != null){
            return Optional.of(fileDocument);
        }else{
            return Optional.empty();
        }


    }
    /**
     * 分页列出文件
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @Override
    public List<FileDocument> listFilesByPage(int pageIndex, int pageSize) {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "uploadDate"));
        long skip = (pageIndex -1) * pageSize;
        query.skip(skip);
        query.limit(pageSize);
        Field field = query.fields();
        field.exclude("content");
        List<FileDocument> files = mongoTemplate.find(query , FileDocument.class );
        return files;
    }


    /**
     * 给定两个上传文件名，删除不在newUploadFileName中而存在oldUploadFileName中的文件
     * @param oldUploadFileName 应该被删除的文件
     * @param newUploadFileName 被保留的文件
     * @return
     */
    @Override
    public void removeFileByOldItem(String oldUploadFileName, String newUploadFileName) {
        if(oldUploadFileName != null){
            if(!oldUploadFileName.equals(newUploadFileName)){	// 如果修改了uploadfilename,则删除原来的file文件
                String[] split = oldUploadFileName.split(";");
                for(String temp:split){
                    if(!newUploadFileName.contains(temp)){
                        this.removeFileByIds(temp);
                    }
                }
            }
        }
    }
}
