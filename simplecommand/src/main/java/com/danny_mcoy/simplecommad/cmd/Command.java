package com.danny_mcoy.simplecommad.cmd;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;

import com.danny_mcoy.simplecommad.log.Logger;
import com.danny_mcoy.simplecommad.service.CommandService;

/**
 * Created by Danny_姜新星 on 3/8/2017.
 *
 * 1 所有Command执行者的父类
 *
 * 2 start方法启动CommandService执行耗时操作
 *      2.1 在CommandService中使用阻塞线程池执行耗时操作
 *
 * 3 execute方法是在CommandService中被回调到的方法
 *      3.1 此方法一般情况下是被线程池在子线程中执行的
 *      3.2 因此可以在此方法中执行耗时操作：如IO operation、Network operation
 *
 * 4 notifyListeners方法是通知所有的ResultReceiver
 *      4.1 调用android.os.ResultReceiver.send(int, Bundle)方法将Budle回传
 *      4.2 通过复写ResultRecever，并添加内部接口ResultListener
 *      4.2 创建子类Command时，只需要构造一个AppResultReceiver并传入一个自定义的ResultListener即可
 *      例如:
 *      //第一步：构造ResultListener
 *      ResultListener rl = new ResultListener(){
 *          @Override
 *          public void onResultSuccess(){}
 *
 *          @Override
 *          public void onResultSuccess(){}
 *
 *          @Override
 *          public void onResultSuccess(){}
 *      }
 *
 *      //第二步：构造ResultReceiver
 *      ResultReceiver rr = new AppResultReceiver(new Handler(), rl);
 *
 *      //第三步：构造并启动ApiCommand
 *      ApiCommand apiCommand = new ApiCommand();
 *      apiCommand.start(Context, rr);
 */

public abstract class Command implements Parcelable {

    protected final static String TAG = Logger.TAG;

    /**
     * 通过次变量来判断是否需要将处理结果返回给ResultReceiver
     * 默认都需要讲结果返回
     */
    protected boolean shouldNotify = true;

    /**
     * resultData就是请求处理完之后，需要回传给ResultReceiver的数据
     */
    protected Bundle mResultData = new Bundle();

    /**
     * 用来接收耗时请求处理完毕后的结果信息，
     * 具体实现一般是AppResultReceiver，并传入一个ResultListener
     */
    protected ResultReceiver mResultReceiver;

    /**
     * 启动CommandService执行耗时请求操作
     * @param context
     * @param resultReceiver 消息接受者，后续onResultSuccess/onResultFailed等
     */
    public void start(Context context, ResultReceiver resultReceiver) {
        CommandService.start(context, this, resultReceiver);
    }

    /**
     * 此方法只CommandService中被调用
     * 一般在子线程中执行
     * 可以被子类复写，并可以执行耗时操作
     *
     * @param context
     * @param resultReceiver 消息接受者，后续onResultSuccess/onResultFailed等
     */
    public void execute(Context context, ResultReceiver resultReceiver) {
        this.mResultReceiver = resultReceiver;
    }

    /**
     * 通知所有的ResultReceiver接收结果消息
     */
    protected void notifyListeners() {
        try {
            Logger.d(Logger.TAG, "shouldNotify: " + shouldNotify);
            Logger.d(Logger.TAG, "mResultReceiver:" + (mResultReceiver != null));
            if (shouldNotify && mResultReceiver != null) {
                mResultReceiver.send(0, mResultData);
            }
        } catch (Exception ex) {
            Logger.e(Logger.TAG, "Failed to send data", ex);
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }
}
