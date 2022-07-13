package com.zxy.music.mongodb.service;

import com.zxy.music.mongodb.domain.FileDocument;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public interface FileService {
    /**
     * 保存文件
     */
    FileDocument saveFile(FileDocument file);

    /**
     * 删除文件
     */
    void removeFile(String id);

    /**
     * 删除文件，id为带;的字符串(在数据库中的表)
     * @param ids
     */
    void removeFileByIds(String ids);

    /**
     * 根据id获取文件
     */
    Optional<FileDocument> getFileById(String id);

    /**
     * 根据id查询文件简要信息
     * @param id
     * @return
     */
    Optional<FileDocument> getFileInfoById(String id);

    /**
     * 分页查询，按上传时间降序
     * @return
     */
    List<FileDocument> listFilesByPage(int pageIndex, int pageSize);

    public String uploadFileToGridFS(InputStream in , String contentType);

    /**
     * 给定两个上传文件名，删除不在newUploadFileName中而存在oldUploadFileName中的文件
     * @param oldUploadFileName 应该被删除的文件
     * @param newUploadFileName 被保留的文件
     * @return
     */
    void removeFileByOldItem(String oldUploadFileName, String newUploadFileName);
}
