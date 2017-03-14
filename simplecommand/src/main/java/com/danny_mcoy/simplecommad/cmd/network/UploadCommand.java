package com.danny_mcoy.simplecommad.cmd.network;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.ResultReceiver;

import com.danny_mcoy.simplecommad.entities.SimpleRequestBody;
import com.danny_mcoy.simplecommad.extra.Params;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.Request;

/**
 * Created by Danny_姜新星 on 17/3/14.
 */

public class UploadCommand extends NetworkCommand {

    public static final String UPLOAD_CMD = UploadCommand.class.getSimpleName();

    private String domain;
    private String path;
    private String token;
    private String charset;
    private String accept;
    private String contentType;
    private MediaType mediaType;
    private String fileAbsolutePath;
    private SimpleRequestBody.ProgressListener progressListener;

    public static class Builder {
        private String domain;
        private String path;
        private String token;
        private String charset;
        private String accept;
        private String fileAbsolutePath;
        private String contentType;
        private MediaType mediaType;
        private SimpleRequestBody.ProgressListener progressListener;

        /**
         * 设置上传的域名地址
         * @param domain
         * @return
         */
        public Builder domain(String domain) {
            this.domain = domain;
            return this;
        }

        /**
         * 设置上传的接口地址
         * @param path
         * @return
         */
        public Builder path(String path) {
            this.path = path;
            return this;
        }

        /**
         * 设置请求头中Content-Type的字段，例如"application/json"
         * 参考 http://tools.jb51.net/table/http_header
         * @return
         */
        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        /**
         * 设置上传数据RequestBody中的媒体类型, 例如"application/json; charset=utf-8"
         * 参考 http://www.iana.org/assignments/media-types/media-types.xhtml
         *
         * Params.Body.MEDIA_TYPE_TEXT  上传文本字符串
         * Params.Body.MEDIA_TYPE_STREAM  上传字节流或者File
         * Params.Body.MEDIA_TYPE_JSON  上传Json字符串
         * Params.Body.MEDIA_TYPE_XML  上传xml字符串
         * Params.Body.MEDIA_TYPE_PNG  上传png图片
         * Params.Body.MEDIA_TYPE_JPEG  上传jpeg图片
         * Params.Body.MEDIA_TYPE_VIDEO  上传MP4视频文件
         *
         * @return
         */
        public Builder mediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        /**
         * 设置请求中的token，默认返回空串""
         * @return
         */
        public Builder token(String token) {
            this.token = token == null ? Params.Headers.TOKEN : token;
            return this;
        }

        /**
         * 设置请求的编码，默认返回 utf-8
         * @return
         */
        public Builder charset(String charset) {
            this.charset = charset == null ? Params.Headers.CHARSET_UTF_8 : charset;
            return this;
        }

        /**
         * 设置请求头中的Accept字段，默认为"Accept"
         * @return
         */
        public Builder accept(String accept) {
            this.accept = accept == null ? Params.Headers.ACCEPT : accept;
            return this;
        }

        /**
         * 设置需要上传的文件绝对路径
         * @param fileAbsolutePath
         * @return
         */
        public Builder file(String fileAbsolutePath) {
            this.fileAbsolutePath = fileAbsolutePath;
            return this;
        }

        /**
         * 设置上传的进度监听器
         * @param progressListener
         * @return
         */
        public Builder transferListener(SimpleRequestBody.ProgressListener progressListener) {
            this.progressListener = progressListener;
            return this;
        }

        public UploadCommand build() {
            return new UploadCommand(this);
        }
    }

    private UploadCommand() {
        // hidden
    }

    private UploadCommand(Builder builder) {
        this.domain = builder.domain;
        this.path = builder.path;
        this.token = builder.token;
        this.charset = builder.charset;
        this.accept = builder.accept;

        this.fileAbsolutePath = builder.fileAbsolutePath;
        this.contentType = builder.contentType;
        this.mediaType = builder.mediaType;
        this.progressListener = builder.progressListener;
    }

    @Override
    protected String buildUrl(Context context) {
        Uri.Builder builder = Uri.parse(domain).buildUpon();
        builder.appendEncodedPath(path);
        return builder.build().toString();
    }

    @Override
    public void start(Context context, ResultReceiver resultReceiver) {
        //添加CMD_CODE值， 后续再onResultSuccess方法中可以通过此值进行区分Command
        mResultData.putString(Params.CommandMessage.CMD_CODE, UPLOAD_CMD);

        super.start(context, resultReceiver);
    }

    @Override
    protected Request getRequest(String url) {
        SimpleRequestBody simpleRequestBody = new SimpleRequestBody(this.mediaType, new File(fileAbsolutePath));
        if (progressListener != null) {
            simpleRequestBody.setProgressListener(progressListener);
        }

        return new Request.Builder().header(Params.Headers.CONTENT_TYPE, this.contentType)
                .addHeader(Params.Headers.ACCEPT, this.accept)
                .addHeader(Params.Headers.CHARSET, this.charset)
                .addHeader(Params.Headers.TOKEN, this.token)
                .post(simpleRequestBody)
                .build();

    }

    public static final Creator<UploadCommand> CREATOR = new Creator<UploadCommand>() {
        @Override
        public UploadCommand createFromParcel(Parcel source) {
            return new UploadCommand();
        }

        @Override
        public UploadCommand[] newArray(int size) {
            return new UploadCommand[size];
        }
    };

}
