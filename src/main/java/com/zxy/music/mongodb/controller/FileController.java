package com.zxy.music.mongodb.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.zxy.music.mongodb.domain.FileDocument;
import com.zxy.music.mongodb.domain.ResponseModel;
import com.zxy.music.mongodb.service.FileService;

import com.zxy.music.mongodb.util.MD5Util;
import com.zxy.music.mongodb.util.MultipartFileResource;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Controller

public class FileController {
    @Autowired
    private FileService fileService;
    @Autowired
    private GridFSBucket gridFSBucket;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private MongoTemplate mongoTemplate;

    private String serverAddress="127.0.0.1";

    @Value("${server.port}")
    private String serverPort;

    @RequestMapping(value = "/")
    public String index(Model model) {
        // 展示最新二十条数据
        model.addAttribute("files", fileService.listFilesByPage(0, 20));
        return "index";
    }

    /**
     * 分页查询文件
     */
    @GetMapping("files/{pageIndex}/{pageSize}")
    @ResponseBody
    public List<FileDocument> listFilesByPage(@PathVariable int pageIndex, @PathVariable int pageSize) {
        return fileService.listFilesByPage(pageIndex, pageSize);
    }

    /**
     * 获取文件片信息
     */
    @GetMapping("files/{id}")
    @ResponseBody
    public ResponseEntity<Object> serveFile(@PathVariable String id) throws UnsupportedEncodingException {

        Optional<FileDocument> file = fileService.getFileById(id);

        if (file.isPresent()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=" + new String(file.get().getName().getBytes("utf-8"),"ISO-8859-1"))
                    .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                    .header(HttpHeaders.CONTENT_LENGTH, file.get().getSize() + "").header("Connection", "close")
                    .body(file.get().getContent());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File was not fount");
        }

    }

    /**
     * 在线显示文件
     */
    @GetMapping("/view")
    @ResponseBody
    public ResponseEntity<Object> serveFileOnline(@RequestParam("id") String id) {
        Optional<FileDocument> file = fileService.getFileById(id);
        if (file.isPresent()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "fileName=" + file.get().getName())
                    .header(HttpHeaders.CONTENT_TYPE, file.get().getContentType())
                    .header(HttpHeaders.CONTENT_LENGTH, file.get().getSize() + "").header("Connection", "close")
                    .header(HttpHeaders.CONTENT_LENGTH , file.get().getSize() + "")
                    .body(file.get().getContent());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File was not found");
        }


    }

    /**
     * 上传
     */
    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        ByteArrayOutputStream bos = null;
        InputStream ins = null;
        try {
            FileDocument fileDocument = new FileDocument();
            fileDocument.setName(file.getOriginalFilename());
            fileDocument.setSize(file.getSize());
            fileDocument.setContentType(file.getContentType());
            fileDocument.setUploadDate(new Date());
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            fileDocument.setSuffix(suffix);
            ins=file.getInputStream();
            //fileDocument.setMd5(MD5Util.getMD5(file.getInputStream()));
            int len = 0;
            byte[] buf = new byte[256];
            while ((len = ins.read(buf, 0, 256)) > -1) {
                bos.write(buf, 0, len);
            }

            fileDocument.setContent(bos.toByteArray());

            //将文件存入gridFs
            String gridfsId = fileService.uploadFileToGridFS(file.getInputStream() , file.getContentType());
            fileDocument.setGridfsId(gridfsId);
            fileDocument = fileService.saveFile(fileDocument);
            System.out.println(fileDocument);
        } catch (IOException  ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Your " + file.getOriginalFilename() + " is wrong!");
            return "redirect:/";
        }

        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    /**
     * 上传接口
     */

    @CrossOrigin("*")
    @PostMapping("/upload")
    @ResponseBody

    public Map<String,Object> handleFileUpload(@RequestParam("file") MultipartFile file) {
        FileDocument returnFile = null;
        Map<String,Object> res=new HashMap<>();
        try {
            FileDocument fileDocument = new FileDocument();
            fileDocument.setName(file.getOriginalFilename());
            fileDocument.setSize(file.getSize());
            fileDocument.setContentType(file.getContentType());
            fileDocument.setUploadDate(new Date());
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            fileDocument.setSuffix(suffix);
            fileDocument.setMd5(MD5Util.getMD5(file.getInputStream()));
            //将文件存入gridFs
            String gridfsId = fileService.uploadFileToGridFS(file.getInputStream() , file.getContentType());
            fileDocument.setGridfsId(gridfsId);
            returnFile = fileService.saveFile(fileDocument);
            String path = "//" + serverAddress + ":" + serverPort + "/view/" + returnFile.getId();
            System.out.println("上传成功"+path);
            res.put("fileid",returnFile.getId());

            return res;
        } catch (IOException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            res.put("error",ex);
            return res;
        }
    }

    /**
     * 删除文件
     */
    @GetMapping("/deletebyid")
    @ResponseBody
    public ResponseModel deleteFile(@RequestParam("id") String id) {
        ResponseModel model = ResponseModel.getInstance();
        if(!StrUtil.isEmpty(id)){
            fileService.removeFile(id);
            model.setCode(ResponseModel.Success);
            model.setMessage("删除成功");
        }else {
            model.setMessage("请传入文件id");
        }
        return model;
    }

    /**
     * 通过单个文件id获取文件详细信息
     */
    @GetMapping("/getFileinfoByid")
    @ResponseBody
    public ResponseEntity<byte[]> getFileinfoByid(@RequestParam("id") String id) throws IOException {
        ByteArrayOutputStream bos = null;
        InputStream ins = null;
        Map<String, Object> res=new HashMap<>();
        try {
            Optional<FileDocument> file = fileService.getFileById(id);
            bos = new ByteArrayOutputStream();
            FileDocument fileDocument = mongoTemplate.findById(id , FileDocument.class );
            Query gridQuery = new Query().addCriteria(Criteria.where("filename").is(fileDocument.getGridfsId()));
            GridFSFile fsFile = gridFsTemplate.findOne(gridQuery);
            ins = gridFSBucket.openDownloadStream(fsFile.getObjectId());;
            // 取流中的数据
            int len = 0;
            byte[] buf = new byte[256];
            while ((len = ins.read(buf, 0, 256)) > -1) {
                bos.write(buf, 0, len);
            }
            String fileName = URLEncoder.encode(file.get().getName(), "utf-8");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment;filename=" + fileName);
//            headers.add("Content-Type", "application/json");
            headers.add("Content-Type", "*/*");
            HttpStatus statusCode = HttpStatus.OK;
            ResponseEntity<byte[]>  response = new ResponseEntity<byte[]>(bos.toByteArray(), headers, statusCode);
            return response;
            // 防止中文乱码
            //String fileName = URLEncoder.encode(file.get().getName(), "utf-8");
            // 设置响应头
//            String message=new String(bos.toByteArray());
//            String substring = message.substring(1, message.length() - 1).replace("\\\"","'");
////转化为json对象



        } catch (Exception e) {
            System.out.println("下载失败1"+e);
            return  null;
        } finally {
            try {
                if (ins != null) {
                    ins.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
                System.out.println("下载失败2");
            }
        }
    }


    /**
     * 通过单个文件id获取图片
     */
   @RequestMapping(value = "/img",method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImginfoByid(@RequestParam("id") String id,HttpServletResponse response) throws IOException {
        ByteArrayOutputStream bos = null;
        InputStream ins = null;
        Map<String, Object> res=new HashMap<>();
        try {
            Optional<FileDocument> file = fileService.getFileById(id);
            bos = new ByteArrayOutputStream();
            FileDocument fileDocument = mongoTemplate.findById(id , FileDocument.class );
            Query gridQuery = new Query().addCriteria(Criteria.where("filename").is(fileDocument.getGridfsId()));
            GridFSFile fsFile = gridFsTemplate.findOne(gridQuery);
            ins = gridFSBucket.openDownloadStream(fsFile.getObjectId());;
            // 取流中的数据
            int len = 0;
            byte[] buf = new byte[256];
            while ((len = ins.read(buf, 0, 256)) > -1) {
                bos.write(buf, 0, len);
            }
            String fileName = URLEncoder.encode(file.get().getName(), "utf-8");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment;filename=" + fileName);
//            headers.add("Content-Type", "application/json");
            headers.add("Content-Type", "image/*");
            HttpStatus statusCode = HttpStatus.OK;
            ResponseEntity<byte[]>  responseEntity = new ResponseEntity<byte[]>(bos.toByteArray(), headers, statusCode);
            return responseEntity;
            // 防止中文乱码
            //String fileName = URLEncoder.encode(file.get().getName(), "utf-8");
            // 设置响应头
//            String message=new String(bos.toByteArray());
//            String substring = message.substring(1, message.length() - 1).replace("\\\"","'");
////转化为json对象



        } catch (Exception e) {
            System.out.println("下载失败1"+e);
            return  null;
        } finally {
            try {
                if (ins != null) {
                    ins.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
                System.out.println("下载失败2");
            }
        }
    }

}

