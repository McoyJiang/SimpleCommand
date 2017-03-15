# SimpleCommand

[1.简单使用](https://github.com/McoyJiang/SimpleCommand/blob/master/docs/1.%E7%AE%80%E5%8D%95%E4%BD%BF%E7%94%A8.md)

[2.图片下载](https://github.com/McoyJiang/SimpleCommand/blob/master/docs/2.%E5%9B%BE%E7%89%87%E4%B8%8B%E8%BD%BD.md)

[3.ImageLoader的API详细介绍](https://github.com/McoyJiang/SimpleCommand/blob/master/docs/3.ImageLoader%E7%9A%84API%E8%AF%A6%E7%BB%86%E4%BB%8B%E7%BB%8D.md)

[4.上传文件](https://github.com/McoyJiang/SimpleCommand/blob/master/docs/4.%E4%B8%8A%E4%BC%A0%E6%96%87%E4%BB%B6.md)

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

<br>
<br>

## Contribute: 如果想为此框架添砖加瓦，请参照一下步骤
- 1 Fork此工程，点击右上角的Fork按钮
- 2 创建自己的开发分之 (git checkout -b my-new-feature)
- 3 提交自己的代码修改 (git commit -am 'Add some feature')
- 4 将代码修改push到分之服务器 (git push origin my-new-feature)
- 5 创建一个新的Pull Request，具体如何创建Pull Request请自行Google
