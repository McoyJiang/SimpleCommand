package com.danny_mcoy.simplecommad.cmd.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcel;
import android.os.ResultReceiver;
import android.util.Log;

import com.danny_mcoy.simplecommad.R;
import com.danny_mcoy.simplecommad.cmd.Command;
import com.danny_mcoy.simplecommad.extra.CommandStatus;
import com.danny_mcoy.simplecommad.extra.Params;
import com.danny_mcoy.simplecommad.log.Logger;
import com.danny_mcoy.simplecommad.storage.Storage;
import com.danny_mcoy.simplecommad.utils.https.SimpleTrustManager;
import com.danny_mcoy.simplecommad.utils.https.SimpleTrustStoreInitializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.Response;

/**
 * Created by Danny_姜新星 on 3/8/2017.
 *
 * 此类是所有网络请求Command的基类
 * 项目中可以根据相应的业务逻辑，创建不同的子Command
 * 子Command中需要复写一下几个方法
 *      buildUrl--构建请求Url地址
 *      getRequest--根据Url地址构建Request对象
 *
 * 注意: 所有的子类Command中，必须书写一个CREATOR
 */

public abstract class NetworkCommand extends Command {

//    private final Class mClazz;
//
//    private final Gson mGson;
//
//    public NetworkCommand() {
//        this(null);
//    }
//
//    public NetworkCommand(Class clazz) {
//        mClazz = clazz;
//        mGson = new Gson();
//    }

    @Override
    public void execute(Context context, ResultReceiver resultReceiver) {
        final long beforeTime = System.currentTimeMillis();
        super.execute(context, resultReceiver);

        OkHttpClient client = new OkHttpClient();
        client.newBuilder().connectTimeout(context.getResources().getInteger(R.integer.connection_timeout), TimeUnit.SECONDS);
        client.newBuilder().readTimeout(context.getResources().getInteger(R.integer.read_timeout), TimeUnit.SECONDS);

        //添加Okhttp对Https请求的支持
        if (SimpleTrustStoreInitializer.getSimpleTrustManager() != null) {
            Logger.e("TAG", "https is integrated ^_^");
            client.newBuilder().sslSocketFactory(SimpleTrustStoreInitializer
                    .getSSLContext().getSocketFactory(), new SimpleTrustManager(SimpleTrustManager.getLocalKeyStore()));
        } else {
          Logger.e("TAG", "https is not supported for now!!!");
        }

        try {
            if (isNetworkAvailable(context)) {
                Request request = getRequest(buildUrl(context));
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    processSuccessfulRequest(response.body().string());
                } else {
                    Logger.d(Logger.TAG, "Error response: " + response.toString());
                    handleError(response.code());
                    this.mResultData.putSerializable(Params.CommandMessage.EXTRA_STATUS, CommandStatus.FAIL);
                    if (null != ((Integer) response.code())) {
                        this.mResultData.putInt(Params.CommandMessage.EXTRA_STATUS_CODE, ((Integer) response.code()));
                    }
                }

            } else {
                Logger.e(Logger.TAG, "Failed to perform request");
                handleError(Params.Command.StatusCode.CODE_NO_NETWORK);
            }
        } catch (Exception e) {
            handleError();
            Log.e(TAG, "Failed to perform request", e);
        } finally {
            postCommand(context);
            notifyListeners();
            final long afterTime = System.currentTimeMillis();
            Log.d(TAG, Command.class.getSimpleName() + " is finished with time" + (afterTime- beforeTime));
        }
    }

    protected void handleError() {
        handleError(null);
    }

    /**
     * 处理网络请求错误是的信息
     * @param statusCode
     */
    protected void handleError(final Integer statusCode) {
        this.mResultData.putSerializable(Params.CommandMessage.EXTRA_STATUS, CommandStatus.FAIL);
        if (statusCode != null) {
            this.mResultData.putInt(Params.CommandMessage.EXTRA_STATUS_CODE, statusCode);
        }
    }

    /**
     * 网络请求成功之后，对请求收到的字符串信息进行处理
     *
     * @param response
     * */
    protected void processSuccessfulRequest(String response) {
        this.mResultData.putSerializable(Params.CommandMessage.EXTRA_STATUS, CommandStatus.SUCCESS);

//        if (mClazz != null) {
//            Object obj = mGson.fromJson(response, mClazz);
//            Logger.e("JIANG", "obj is " + obj);
//        }
        mResultData.putString(Params.CommandMessage.EXTRA_BODY, response);
    }

    /**
     * 为相应的子Command构建相应的Url地址
     *
     * @param context
     * @return 网络请求的Url地址
     * */
    protected abstract String buildUrl(Context context);

    /**
     * 通过Url地址构建OkHttp的Request请求对象
     * @param url
     * @return
     */
    protected abstract Request getRequest(final String url);

    protected void postCommand(Context context) {
        // no op by default
    }

    protected String byteToString(URLConnection connection) throws IOException {
        InputStream response = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(response));

        StringBuilder buffer = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }

    protected String byteToStringError(URLConnection connection) throws IOException {
        InputStream response = ((HttpURLConnection) connection).getErrorStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(response));

        StringBuilder buffer = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }

    /**
     * 判断网络是否可用
     * @param context
     * @return
     */
    protected boolean isNetworkAvailable(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();

        Logger.d(Logger.TAG, "networkInfo != null " + (networkInfo != null));
        Logger.d(Logger.TAG, "networkInfo.getType() != ConnectivityManager.TYPE_WIFI " + (networkInfo.getType() != ConnectivityManager.TYPE_WIFI));
        Logger.d(Logger.TAG, "Wifi only: " + Storage.isWifiOnly(context));
        // if this network is not wifi but only wifi is allowed network is not available
        if (networkInfo != null
                && networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                && Storage.isWifiOnly(context))
            return false;

        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // no operation
    }

}
