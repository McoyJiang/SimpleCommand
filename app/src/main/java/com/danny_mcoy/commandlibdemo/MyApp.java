package com.danny_mcoy.commandlibdemo;

import android.app.Application;

import com.danny_mcoy.simplecommad.utils.https.SimpleTrustStoreInitializer;

import java.security.KeyStore;

/**
 * Created by axing on 17/3/23.
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SimpleTrustStoreInitializer.initialize(this,
                R.raw.cert12306, KeyStore.getDefaultType(), "pw12306");
    }
}
