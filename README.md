# 阳光沙滩App

#### 阳光沙滩社区：[<font color="#FA7299">A Lonely Cat</font>](https://www.sunofbeach.net/u/1204736502274318336)

<img src="https://gitee.com/anjiemo/figure-bed/raw/master/img/20210624130828.png" alt="阳光沙滩社区个人主页" style="zoom:50%;" />

#### 项目体验

* [蒲公英下载地址](https://www.pgyer.com/sob-app)
  ![蒲公英渠道扫码下载](https://www.pgyer.com/app/qrcode/sob-app)

* [蓝奏云下载地址](https://wwa.lanzoui.com/b02zz8dva)（密码：5qlt）

#### [优化、BUG或建议请点这里](https://support.qq.com/product/333302)

#### 关于我们

阳光沙滩创立于2014年11月22日，那是一个阳光明媚的下午。

韩寒的电影《后会无期》里面有一句台词：sun of beach （阳光沙滩），于是阳光沙滩创立。阳光沙滩是一个学习编程的社区网站。

你可以在这里学习，写博客，写笔记，分享经验，提问题，分享链接。你可以遇到志同道合的人，收获知识、经验与同性朋友。

#### 我们的使命

让学习编程变得更加简单。

#### 我们的愿景

让每一个热爱编程的年轻人成为优秀的程序员。

#### 项目简介

该项目使用Kotlin、Java语言进行开发，采用MVVM架构 +
Google [Jetpack](https://developer.android.google.cn/jetpack)组件搭建项目，主要使用阳光沙滩社区开放Api实现相关功能，正在持续更新中...

#### 技术栈

Kotlin、Glide、EasyHttp、Retrofit、OkHttp、BRVAH、XXPermissions、AndroidUtilCode、Room、UmengSDK、MiPush等...

#### 快速开始

- clone 本项目源码并用 AndroidStudio 打开
- 将项目视图切换为 Project 视图
- 复制您的 app 签名文件到 app 模块下

Tips：如果您还没有创建自己的 app 签名文件，建议先生成自己的 app 签名文件。

不会生成 app 签名文件？[请点这里](https://www.jianshu.com/p/a1f8e5896aa2)

如果您不想生成，那么建议您使用项目中的 AppSignature.jks 文件作为本项目的签名文件，并填写如下信息到 app 模块下的 gradle.properties 文件中。

<br>

gradle.properties

```properties
StoreFile=AppSignature.jks
StorePassword=AndroidProject
KeyAlias=AndroidProject
KeyPassword=AndroidProject
```

- 在 app 模块下创建 gradle.properties 文件并填写您的签名信息（已添加到 gitignore 忽略文件中，进行 git 提交时不会泄露您的秘钥信息）
- 将项目根目录下 configs.gradle 文件中的 UMENG_APP_KEY、QQ_APP_ID、QQ_APP_SECRET、WX_APP_ID、WX_APP_SECRET、BUGLY_ID
  替换为自己的相关信息
- 点击 AndroidStudio 右上角 <font color="blue">Sync Now</font> 按钮进行同步，并等待同步完成即可

Tips：建议使用最新版本的 AndroidStudio 运行本项目。

#### 项目截图

|  |  |  |
| --- | --- | --- |
| ![](./picture/sunnybeach/注册.png) | ![](./picture/sunnybeach/登录.png) | ![](./picture/sunnybeach/忘记密码1.png) |
| ![](./picture/sunnybeach/忘记密码2.png) | ![](./picture/sunnybeach/我.png) | ![](./picture/sunnybeach/用户中心.png) |
| ![](./picture/sunnybeach/用户详情-动态.png) | ![](./picture/sunnybeach/用户详情-文章.png) | ![](./picture/sunnybeach/用户详情-回答.png) |
| ![](./picture/sunnybeach/用户详情-关注.png) | ![](./picture/sunnybeach/用户详情-粉丝.png) | ![](./picture/sunnybeach/用户详情-分享.png) |
| ![](./picture/sunnybeach/用户详情-折叠.png) | ![](./picture/sunnybeach/VIP-特权介绍.png) | ![](./picture/sunnybeach/VIP-部分已开通VIP列表.png) |
| ![](./picture/sunnybeach/用户中心-沙滩证.png) | ![](./picture/sunnybeach/富豪榜.png) | ![](./picture/sunnybeach/消息中心.png) |
| ![](./picture/sunnybeach/消息中心-文章.png) | ![](./picture/sunnybeach/消息中心-点赞.png) | ![](./picture/sunnybeach/消息中心-摸鱼.png) |
| ![](./picture/sunnybeach/消息中心-回复.png) | ![](./picture/sunnybeach/消息中心-问答.png) | ![](./picture/sunnybeach/消息中心-系统.png) |
| ![](./picture/sunnybeach/创作中心.png) | ![](./picture/sunnybeach/高清壁纸.png) | ![](./picture/sunnybeach/高清壁纸-详情列表.png) |
| ![](./picture/sunnybeach/高清壁纸-分享.png) | ![](./picture/sunnybeach/天气预报-搜索.png) | ![](./picture/sunnybeach/天气预报-详情.png) |
| ![](./picture/sunnybeach/天气预报-侧边栏搜索.png) | ![](./picture/sunnybeach/意见反馈-反馈列表.png) | ![](./picture/sunnybeach/意见反馈-常见问题.png) |
| ![](./picture/sunnybeach/意见反馈-我的.png) | ![](./picture/sunnybeach/设置.png) | ![](./picture/sunnybeach/鱼塘-强制更新对话框.png) |
| ![](./picture/sunnybeach/鱼塘-非强制更新.png) | ![](./picture/sunnybeach/设置-非强制更新.png) | ![](./picture/sunnybeach/设置-关于我们.png) |
| ![](./picture/sunnybeach/鱼塘列表.png) | ![](./picture/sunnybeach/鱼塘-摸鱼详情.png) | ![](./picture/sunnybeach/查看大图.png) |
| ![](./picture/sunnybeach/鱼塘-评论详情.png) | ![](./picture/sunnybeach/评论详情-未登录.png) | ![](./picture/sunnybeach/发布摸鱼-未登录.png) |
| ![](./picture/sunnybeach/摸鱼详情-评论.png) | ![](./picture/sunnybeach/摸鱼详情-表情评论.png) | ![](./picture/sunnybeach/摸鱼详情-摸鱼分享.png) |
| ![](./picture/sunnybeach/发布摸鱼.png) | ![](./picture/sunnybeach/发布摸鱼-图片选择.png) | ![](./picture/sunnybeach/发布摸鱼-鱼塘选择.png) |
| ![](./picture/sunnybeach/问答.png) | ![](./picture/sunnybeach/文章.png) | ![](./picture/sunnybeach/课程.png) |
| ![](./picture/sunnybeach/课程详情.png) | ![](./picture/sunnybeach/扫码.png) | ![](./picture/sunnybeach/问答-问题详情.png) |
| ![](./picture/sunnybeach/文章详情.png) | ![](./picture/sunnybeach/文章详情-代码块.png) | ![](./picture/sunnybeach/文章详情-打赏.png) |
| ![](./picture/sunnybeach/文章详情-评论.png) | ![](./picture/sunnybeach/文章详情-文章推荐.png) | ![](./picture/sunnybeach/文章详情-文章分享.png) |
| ![](./picture/sunnybeach/闪屏界面.png) |  |  |

![](./picture/sunnybeach/课程视频播放.png)


#### 如果您觉得我的这个项目对你有帮助，请扫描下方的二维码随意打赏，要是能打赏个 10.24 :monkey_face:就太:thumbsup:了。您的支持将鼓励我继续创作:octocat:

![image-20210624125821356](https://gitee.com/anjiemo/figure-bed/raw/master/img/20210624125821.png)



## License

```text
Copyright 2021 He XiaoFeng

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```