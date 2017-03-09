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
