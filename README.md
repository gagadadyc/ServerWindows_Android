# ServerWindows
服务器监控平台中的Android端，服务器性能可视化，支持实时刷新。

## 主要功能
监控多个服务器，并将监控到的数据以图表的形式绘制出来。（在本说明的最后一部分给出了效果示例）  
用户停留在图表界面时，刷新线程会每隔十秒向服务器获取一次数据，然后对图表进行更新，确保监控数据的实时性

## 开发平台
Java： jdk1.8.0_181及以下（java10可能会由于时间戳转换格式的区别，导致与服务器交互的json格式出现错误）
IDE： Android Studio2.2 以上。

## 项目架构
ServerWindows是一个监控平台，由三个模块组成，本项目为其中的Android端。  
*本文为Androidd端的说明书，所以平台架构不予赘述，平台架构在个人博客中说明：*[https://imdyc.com/java/serverwindows](https://imdyc.com/java/serverwindows-%E6%9C%8D%E5%8A%A1%E5%99%A8%E5%8F%AF%E8%A7%86%E5%8C%96%E7%9B%91%E6%8E%A7%E5%B9%B3%E5%8F%B0.html)

**以下是Android端的工作流程**
![Android端的工作流程](http://images-1252121815.cosgz.myqcloud.com/blog/ServerWindows/TIM%E6%88%AA%E5%9B%BE20180811162634.png)
整个平台流程的关键点在于服务器端与APP用户端的数据交互部分，Volley使用了多线程技术，所以会产生类似于“生产者-消费者”的多线程同步问题，所以设计使用SharedPreferences作为数据容器。当客户端收到服务器发来的数据后，会将数据写在SharedPreferences中。在APP发起网络请求时，建立子线程对中转文件SharedPreferences进行轮询访问，若访问不到数据说明Volley还未得到数据，睡眠1秒后再次访问，访问次数满10次后将会报错，此时应该检查客户端到服务器端的网络问题。  
  
数据提交给MPAndroidChart框架后，MPAndroidChart框架将数据绘制成图表，显示给用户。此时仍未结束，定时刷新线程还将会**每隔10秒钟对服务器进行一次请求，然后用请求到的数据对图表进行更新，保证用户看到的数据是实时的。直到用户关闭本界面为止**。


## 使用技术
Gson：Google推出的**Json序列化/反序列化的类库**。本项目使用该记录来实现服务器端与客户端的交互。

MPAndroidChart：基于Android的数据可视化框架。开源地址：[https://github.com/PhilJay/MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)

Volley：Google官方推出的网络通信框架，它以Get、Psost为基础，以多线程的形式来通信，主要以Json格式来进行数据交互。

Multithreading: 多线程，在Android中添加一条线程，每隔十秒刷新一次图表。

## 运行平台 
Android 4.0以上。


## 效果示例
### 控制台列表
如图所示，本平台为一对多平台，即一个客户端可以监控多台服务器。
![控制台列表](http://images-1252121815.cosgz.myqcloud.com/blog/ServerWindows/TIM%E5%9B%BE%E7%89%8720180523001952.jpg)

### 监控数据图表
下图为监控数据总览图，可监控24小时内的数据。
![监控数据图表](http://images-1252121815.cosgz.myqcloud.com/blog/ServerWindows/TIM%E5%9B%BE%E7%89%8720180523001958.jpg)

横屏后双指放大可获取指定时间点的详细数据，精度为10s。
![详细数据](https://images-1252121815.cosgz.myqcloud.com/blog/ServerWindows/TIM%E5%9B%BE%E7%89%8720180527213636.jpg)
