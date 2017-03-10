package com.danny_mcoy.commandlibdemo;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.IntRange;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.danny_mcoy.simplecommad.extra.Params;
import com.danny_mcoy.simplecommad.log.Logger;
import com.danny_mcoy.simplecommad.receiver.AppResultReceiver;
import com.danny_mcoy.simplecommad.utils.image.ImageLoader;

import java.io.File;

public class MainActivity extends AppCompatActivity implements AppResultReceiver.ResultListener {

    private TextView textView;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = ((TextView) findViewById(R.id.text_Main));

        image = ((ImageView) findViewById(R.id.image_Main));
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

    public void getImage(View view) {
        final File cacheDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);

        ImageLoader.ProgressListener listener = new ImageLoader.ProgressListener() {
            @Override
            public void update(@IntRange(from = 0, to = 100) int percent) {
                Log.e("TAG", "percent is " + percent);
            }
        };

        ImageLoader imageLoader = new ImageLoader(this, listener, "jiang");

        imageLoader.load("https://www.baidu.com/img/bd_logo1.png")
                .withPlaceholder(R.mipmap.ic_launcher)
                .into(image);
    }
}
