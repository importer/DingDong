## 功能特点

+ 基于[小米推送](https://dev.mi.com/console/doc/detail?pId=863)，系统级推送，低功耗
+ [腾讯云函数](https://cloud.tencent.com/document/product/583)服务搭建[转发推送服务脚本] (https://github.com/importer/DingDong/blob/main/pushserver.py)
+ 便签功能基于[发呆便签](https://github.com/ifadai/SuperNote)是一款开源的Android便签，实现了主流便签的基本功能，并不断完善中。 特点： Material Design设计、MVP设计模式
+ 配合[ChromeBarkExtension](https://chrome.google.com/webstore/detail/bark/pmlkbdbpglkgbgopghdcmohdcmladeii?utm_source=chrome-ntp-icon)使用能帮你方便地把网页上的文本或者网址推送到小米手机端
## 如何使用叮咚便签

+ [下载](https://github.com/importer/DingDong/releases/download/1.0/app-release.apk)


+ 须配合[ChromeBarkExtension](https://chrome.google.com/webstore/detail/bark/pmlkbdbpglkgbgopghdcmohdcmladeii?utm_source=chrome-ntp-icon)使用

+ 打开叮咚便签，点击含有“推送网址，点此复制网址！”的标题，进去内容详情，将网址复制到剪贴板

+ 进入chrome插件配置页面，类型选择iPhone，然后填入刚才复制的网址：https://service-mm0wgphf-1258509752.cd.apigw.tencentcs.com/release/APIService-mipushServer/dd/sendmsg?id=自己的设备id&title=标题(可选值)&msg=
+ 修改为：https://service-mm0wgphf-1258509752.cd.apigw.tencentcs.com/release/APIService-mipushServer/dd/sendmsg?id=自己的设备id&title=%E5%89%AA%E5%88%87%E6%9D%BF%E6%8E%A8%E9%80%81&msg= 点击Add，即可完成

+ 点击插件图标，默认配置会将剪贴板内容push移动端

+ 右键单击图片可以push图片链接

+ 选中文本然后右键可以push选中内容


## get请求发送消息
+ [下载](https://github.com/importer/DingDong/releases/download/1.0/app-release.apk)
+ 打开叮咚便签，点击含有“推送网址，点此复制网址！”的标题，进去内容详情，将网址复制到剪贴板 如：https://service-mm0wgphf-1258509752.cd.apigw.tencentcs.com/release/APIService-mipushServer/dd/sendmsg?id=设备唯一id&title=标题(可选值)&msg=发送的内容
+ 通过get请求上面网址，可发送内容，请求返回内容为成功，则发送成功，消息将在状态栏提示；

#### 注意事项
+ 只有点击了才会保存推送内容
+ 多条内容同时到达只保存最后一条