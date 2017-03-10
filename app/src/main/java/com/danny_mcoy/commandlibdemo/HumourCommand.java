package com.danny_mcoy.commandlibdemo;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;

import com.danny_mcoy.simplecommad.cmd.network.NetworkCommand;
import com.danny_mcoy.simplecommad.log.Logger;

import okhttp3.Request;
//import com.squareup.okhttp.Request;

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
