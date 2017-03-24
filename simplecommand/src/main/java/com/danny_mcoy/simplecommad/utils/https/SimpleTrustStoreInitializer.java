package com.danny_mcoy.simplecommad.utils.https;


import android.content.Context;

import com.danny_mcoy.simplecommad.log.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * Created by dmitry.kazakov on 4/18/2016.
 */
public class SimpleTrustStoreInitializer {

    private static final String PROTOCOL = "TLS";

    private static String mPassword = null;
    private static SimpleTrustManager simpleTrustManager = null;

    public static final SimpleTrustManager getSimpleTrustManager() {
        return simpleTrustManager;
    }

    /**
     * 初始化自定义TrustManager --> SimpleTrustManager
     *
     * @param context       上下文，通过上下文对象获取raw文件夹下证书资源的流对象
     * @param rawId         保存在raw文件夹下的资源文件ID
     * @param certType      证书类型
     * @param password      证书的密码，可以是null
     * @return
     */
    public static boolean initialize(Context context, int rawId, String certType,String password) {


        InputStream in = null;
        try {
//            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

//            KeyStore keyStore = KeyStore.getInstance(certType);
//            in = context.getResources().openRawResource(rawId);
//            if (password != null) {
//                mPassword = password;
//                keyStore.load(in, password.toCharArray());
//            } else {
//                keyStore.load(in, null);
//
//                //keyStore.load(null);
//                //keyStore.setCertificateEntry("0", certificateFactory.generateCertificate(in));
//            }


            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = context.getResources().openRawResource(rawId);
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                Logger.e("Longer", "ca=" + ((X509Certificate) ca).getSubjectDN());
                Logger.e("Longer", "key=" + ((X509Certificate) ca).getPublicKey());
            } finally {
                caInput.close();
            }

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
            
            simpleTrustManager = new SimpleTrustManager(keyStore);
        } catch (CertificateException e) {
            Logger.e(Logger.TAG, "Failed to obtain certificate", e);
        } catch (NoSuchAlgorithmException e) {
            Logger.e(Logger.TAG, "Failed to obtain certificate", e);
        } catch (KeyStoreException e) {
            Logger.e(Logger.TAG, "Failed to obtain certificate", e);
        } catch (IOException e) {
            Logger.e(Logger.TAG, "Failed to obtain certificate", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Logger.e(Logger.TAG, "Failed to close the stream", e);
                }
            }
        }

        return false;
    }

    public static SSLContext getSSLContext() {
        SSLContext sslContext = null;
        try {

            TrustManager[] tms = new TrustManager[]{simpleTrustManager};

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(null, mPassword == null ? null : mPassword.toCharArray());
            KeyManager[] kms = keyManagerFactory.getKeyManagers();

            sslContext = SSLContext.getInstance(PROTOCOL); // TODO TLS keystore was for trust manager
            sslContext.init(kms, tms, null);
            Logger.e(Logger.TAG, "succeed to read custom certificate");
        } catch (KeyStoreException e) {
            Logger.e(Logger.TAG, "Failed to read custom certificate", e);
        } catch (NoSuchAlgorithmException e) {
            Logger.e(Logger.TAG, "Failed to read custom certificate", e);
        } catch (KeyManagementException e) {
            Logger.e(Logger.TAG, "Failed to read custom certificate", e);
        } catch (UnrecoverableKeyException e) {
            Logger.e(Logger.TAG, "Failed to obtain keys", e);
        }

        return sslContext;
    }

}
