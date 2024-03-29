# Graduation-Design
## 毕业设计

本人毕业设计是音乐可视化图形引擎的研发，项目预期是实现基于Web端，交互性强可编辑的音乐可视化图形设计软件。

主要功能实现：

- 用户信息管理
- 文件管理
- 音乐可视化动画系统
- 三维建模编辑器

## 技术栈

前端使用vue-admin-template框架和Three.js开源3D图形库进行二次开发，使用Web Adudio API 处理音频数据；后台使用SpringBoot+MyBatis框架快速搭建，使用MongoDB数据库存储3D图形数据。

## 环境依赖
![](https://img.shields.io/badge/npm-16.15.0-green)     ![](https://img.shields.io/badge/JDK-1.8-yellow)     ![](https://img.shields.io/badge/MongoDB-5.0.8-brightgreen)  ![](https://img.shields.io/badge/MySQL-8.0.22-blue)

## 项目启动
### 搭建后台
建议使用IntelliJ IDEA开发软件，在安装好对应版本的MongoDB和MySQL数据库后，使用Maven仓库进行配置，选择SpringBoot和MyBatis框架初始化，其余配置使用pom.xml进行安装即可，一切完毕后运行后台。
### 进入前端项目目录
这里由于使用的是vue-admin-template二次开发，因此未改文件夹直接进入即可
```bash
cd vue-admin-template-master
```
### 安装依赖
```bash
npm install
```
### 启动前端服务
```bash
npm run dev
```
