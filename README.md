# SimpleCommand

[1.简单使用](https://github.com/McoyJiang/SimpleCommand/blob/master/docs/1.%E7%AE%80%E5%8D%95%E4%BD%BF%E7%94%A8.md)

[2.图片下载](https://github.com/McoyJiang/SimpleCommand/blob/master/docs/2.%E5%9B%BE%E7%89%87%E4%B8%8B%E8%BD%BD.md)

<br>
## 简介:
SimpleCommand是一款轻量级框架。框架很小也很容易理解。使用这款框架能实现的功能主要是快速集成网络请求、图片请求、文件操作等各种比较耗时的操作。对于网络图图片请求，内部使用的是OkHttp实现
## 使用场景:
此框架并不适合于短时间内有大量请求的场景，比较适合于并发执行4~6个异步请求

## 工程目录介绍：
在SimpleCommand整个工程目录下，有三个比较重要的module：app、sample、simplecommand
### simplecommand：
这个是最重要的一个依赖module，所有核心功能都是在此module种完成的。主要包括以下几个核心类：
**Command：**这个类是所有Command执行者的父类。其中包含3个主要的核心方法：
  start--此方法会启动CommandService服务，并在子线程中执行耗时操作
  execute--在CommandService中回调到的方法，一般将耗时操作放在此方法中执行
  notifuListeners--通知所有的ResultListener
  
### app：
在app module里主要是做演示功能。通过module dependency依赖到了simplecommand module。然后自定义NetworkCommand实现请求网络数据并显示到UI界面

### sample：
是一个比较完整的案例演示module。通过添加simplecommand.aar包实现网络请求功能。具体的使用方法请继续往下看！
