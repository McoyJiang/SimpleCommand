# SimpleCommand
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

## 使用步骤： 以下步骤是以网络请求功能作为演示Demo
1 在新建的app module中新建libs文件夹，并将sample的libs中的simplecommand.aar拷贝到此libs文件夹下

2 在你自己新建的app module的build.gragle中的android块中添加如下语句：

```
repositories {
    flatDir {
        dir 'libs'    //注意：需要提前在app module中创建出libs文件夹
    }
}
```

3 在app module的build.gradle的dependencies中添加如下依赖：

```
compile 'com.squareup.okhttp:okhttp:2.4.0'
compile (name: 'simplecommand', ext: 'aar')
```

4 新建HumourCommand(类名可以自己根据业务随便取), 并集成NetworkCommand类，具体代码如下：
```
package com.danny_mcoy.commandlibdemo;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;

import com.danny_mcoy.simplecommad.cmd.network.NetworkCommand;
import com.danny_mcoy.simplecommad.log.Logger;
import com.squareup.okhttp.Request;

/**
 * Created by Danny_姜新星 on 3/8/2017.
 */

public class HumourCommand extends NetworkCommand {

    @Override
    protected String buildUrl(Context context) {
        Uri.Builder builder = Uri.parse("http://api.laifudao.com")
                .buildUpon();
        builder.appendEncodedPath("open/xiaohua.json");
        Logger.e("JIANG", "url is " + builder.toString());

        return builder.toString();
    }

    @Override
    protected Request getRequest(String url) {
        return new Request.Builder()
                .url(url)
                .build();
    }

    public static final Creator<HumourCommand> CREATOR = new Creator<HumourCommand>() {
        @Override
        public HumourCommand createFromParcel(Parcel source) {
            return new HumourCommand();
        }

        @Override
        public HumourCommand[] newArray(int size) {
            return new HumourCommand[size];
        }
    };

}
```

5 HumourCommand创建好之后， 就可以在MainActivity中使用了，通过实现一个Button的点击事件btnClick开始 ：
```
package com.danny_mcoy.commandlibdemo;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.danny_mcoy.simplecommad.extra.Params;
import com.danny_mcoy.simplecommad.log.Logger;
import com.danny_mcoy.simplecommad.receiver.AppResultReceiver;

public class MainActivity extends AppCompatActivity implements AppResultReceiver.ResultListener {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = ((TextView) findViewById(R.id.text_Main));
    }

    public void btnClick(View view) {
        HumourCommand apiCommand = new HumourCommand();

        AppResultReceiver receiver = new AppResultReceiver(new Handler(), this);

        apiCommand.start(this, receiver);
    }

    @Override
    public void onResultSuccess(Bundle resultData) {
        if (null != resultData) {
            String body = resultData.getString(Params.CommandMessage.EXTRA_BODY);

            Logger.e("JIANG", " body is " + body);

            textView.setText(body);
        }
    }

    @Override
    public void onResultFailed(Bundle resultData) {
        Logger.e("JIANG", " failed");
    }

    @Override
    public void onResultProgress(Bundle resultData) {
        Logger.e("JIANG", " progress");
    }
}
```
简单说明以下：AppResultReceiver是一个请求结果接收器， 当我们调XXXCommand.start方法时，需要传入一个AppResultReceiver。当请求结果返回之后，就会调用AppResultReceiver中的listener相应方法：onResultSuccess、onResultFailed等等
