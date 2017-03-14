package com.danny_mcoy.simplecommad.entities;


import com.danny_mcoy.simplecommad.log.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.MediaType;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * Created by axing on 17/3/14.
 */

public class SimpleRequestBody extends okhttp3.RequestBody{

    /**
     * 如果是上传图片则以1M为缓存进度
     * 如果是上传视频则以4M为缓存进度
     */
    private static final int ONE_MEGABYTE = 1024 * 1024;
    private static final int FOUR_MEGABYTES = 4 * ONE_MEGABYTE;

    private static final int IMAGE_THRESHOLD = ONE_MEGABYTE;
    private static final int VIDEO_THRESHOLD = 10 * ONE_MEGABYTE;

    private MediaType mediaType;
    private File file;

    private WeakReference<ProgressListener> progressListenerWeakRef;

    public SimpleRequestBody(final MediaType mediaType, final File file) {
        this.mediaType = mediaType;
        this.file = file;
    }

    public void setProgressListener(ProgressListener listener) {
        progressListenerWeakRef = new WeakReference<>(listener);
    }

    @Override
    public MediaType contentType() {
        return mediaType;
    }

    @Override
    public long contentLength() throws IOException {
        return file.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Source source = null;
        try {
            source = Okio.source(file);
            long total = 0;
            long read;

            final long contentLength = contentLength();
            final int bufferSize = getBufferSize();
            while ((read = source.read(sink.buffer(), bufferSize)) != -1) {
                total += read;
                sink.flush();
                if (progressListenerWeakRef != null && progressListenerWeakRef.get() != null) {
                    progressListenerWeakRef.get().onDataTransferred(total, contentLength);
                }
            }
        } finally {
            Util.closeQuietly(source);
        }


    }

    private int getBufferSize() {
        try {
            final long fileSizeInMegabytes = contentLength() / ONE_MEGABYTE;
            return fileSizeInMegabytes >= VIDEO_THRESHOLD ? FOUR_MEGABYTES : ONE_MEGABYTE;
        } catch (IOException e) {
            Logger.e(Logger.TAG, "Failed to obtain buffer", e);
        }
        return ONE_MEGABYTE;
    }

    public interface ProgressListener {
        /**
         *
         * @param transferred   已经传输成功的data数据长度
         * @param contentLength 需要传输的数据总长度
         *
         * 注意：如果需要计算出百分比可以使用以下公式
         * Double percent = 100 *  ((double) transferred / (double) contentLength);
         */
        void onDataTransferred(long transferred, long contentLength);
    }
}
