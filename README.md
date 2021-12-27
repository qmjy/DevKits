[中文](./README.md) | [English](./README_en.md)

## DevKits

一款为软件工程师、网络工程师和办公室工作人员提高工作效率的常用工具包。努力成为一名自由程序员。

[![LICENSE](https://img.shields.io/badge/license-Anti%20996-blue.svg)](https://github.com/996icu/996.ICU/blob/master/LICENSE)
[![issues](https://img.shields.io/github/issues/qmjy/DevKits)](https://github.com/qmjy/DevKits/issues)
[![forks](https://img.shields.io/github/forks/qmjy/DevKits)](https://github.com/qmjy/DevKits)
[![stars](https://img.shields.io/github/stars/qmjy/DevKits)](https://github.com/qmjy/DevKits)
[![996.icu](https://img.shields.io/badge/link-996.icu-red.svg)](https://996.icu)
[![downloads](https://img.shields.io/github/downloads/qmjy/Devkits/total)](https://github.com/qmjy/DevKits)


## 特性

- 服务器端口检测
- 重复大文件检测
- QQ截屏
- Windows 7登录壁纸修改
- 多文件管理器
- 二维码识别（本地图片，摄像头，网页）
- 快速打开Hosts文件
- XML & JSON 格式化
- 计算机信息展示
- 待办事务提醒（系统消息、邮件）
- 摄像头
- 托盘支持cpu使用率动画展示


## 依赖环境

项目运行依赖Oracle JDK 1.8及以上版本。


## Release版本
1. [下载Release版本包](https://github.com/qmjy/DevKits/releases/download/v1.0.0/devkits-1.0.0-bin.zip)到本地并解压；
2. 确认本地JAVA环境安装完成，并设置了环境变量，JDK需要Oracle JDK 1.8及以上版本，否则无法运行程序；
3. 进入到`bin`目录运行`start.bat`脚本即可；
4. 从托盘菜单开始；


## 源码构建

```
git clone git@github.com:qmjy/DevKits.git
cd DevKits
mvn clean package assembly:single
```

## 联系方式

如果你有任何问题, [联系我]((mailto:admin@devkits.cn)) 或者到 [issues](https://github.com/qmjy/DevKits/issues)发帖。


## 开源协议

Devkits 采用 [MIT](https://choosealicense.com/licenses/mit/) 和 [Anti-996 License](https://github.com/996icu/996.ICU/blob/master/LICENSE_CN)开源协议。

## 感谢支持

本项目采用Jetbrains IDEA开发，感谢Jetbrains对开源项目的免费支持，谢谢。

![https://jb.gg/OpenSource](./logo/jetbrains.svg)
