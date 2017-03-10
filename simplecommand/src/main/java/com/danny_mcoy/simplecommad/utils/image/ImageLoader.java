package com.danny_mcoy.simplecommad.utils.image;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.StatFs;
import android.support.annotation.IntRange;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.widget.ImageView;

import com.danny_mcoy.simplecommad.R;
import com.danny_mcoy.simplecommad.log.BuildConfig;
import com.danny_mcoy.simplecommad.log.Logger;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by Danny_姜新星 on 03/10/2017.
 */
public class ImageLoader {

    private static final String BIG_CACHE_PATH                   = "picasso-big-cache";
    private static final int    MIN_DISK_CACHE_SIZE              = 32 * 1024 * 1024;       // 32MB
    private static final int    MAX_DISK_CACHE_SIZE              = 512 * 1024 * 1024;      // 512MB

    private static final float  MAX_AVAILABLE_SPACE_USE_FRACTION = 0.9f;
    private static final float  MAX_TOTAL_SPACE_USE_FRACTION     = 0.25f;
    private static final int DEFAULT_PLACEHOLDER = 0;

    private int placeholder;
    private int errorImage;
    private String imageUri;
    private String tag;
    private int imageResourceId;

    private boolean simple;
    private boolean withScaling;
    private int widthResId;
    private int heigthResId;

    private Transformation transformation;

    private Callback callback;

    private final Picasso picasso;
    private boolean fit;
    private boolean centerCrop;
    private boolean centerInside;

    /**
     * Make sure to keep imageLoader and underlying library as single instance per activity.
     * It gives ability to use image library cache, otherwise app will load it from server each time
     * */
    public ImageLoader(Context context) {
        this(context, false);
    }

    /**
     * 注意: 次构造器不支持下载进度提示功能
     * @param context
     * @param withCache 是否支持缓存
     *                  false--不带缓存
     *                  true--支持缓存功能，默认缓存路径在外置存储缓冲目录中的picasso-big-cache文件夹中
     */
    public ImageLoader(Context context, boolean withCache) {
        this(context, null, withCache);
    }

    /**
     * 支持下载进度提示，以及设置缓存路径
     * @param context
     * @param listener  下载进度监听器
     * @param cachePath  缓存路径字符串
     */
    public ImageLoader(Context context, ProgressListener listener, String cachePath) {
        // TODO extend to support multiple libraries as Glide
        // TODO must be initialized and kept as an member instance to avoid losing cache
        Picasso.Builder builder = setupLoaderClientWithCachePath(context, listener, cachePath);
        setupListener(builder);

        picasso = builder.build();
        picasso.setIndicatorsEnabled(BuildConfig.DEBUG);
        picasso.setLoggingEnabled(BuildConfig.DEBUG);
    }

    /**
     * 支持下载进度提示，以及设置缓存路径为默认路径picasso-big-cache
     * @param context
     * @param listener  下载进度监听器
     * @param withCache  是否支持缓存
     */
    public ImageLoader(Context context, ProgressListener listener, boolean withCache) {
        // TODO extend to support multiple libraries as Glide
        // TODO must be initialized and kept as an member instance to avoid losing cache
        Picasso.Builder builder = setupLoaderClient(context, listener, withCache);
        setupListener(builder);

        picasso = builder.build();
        picasso.setIndicatorsEnabled(BuildConfig.DEBUG);
        picasso.setLoggingEnabled(BuildConfig.DEBUG);
    }

    private Picasso.Builder setupLoaderClientWithCachePath(Context context, ProgressListener listener, String cachePath) {
        Picasso.Builder builder = new Picasso.Builder(context);
        OkHttpClient client = getProgressBarClientWithPath(context, listener, cachePath);

        /** OkHttp 2.1之后没有次方法，需要使用Builder构建
        client.setConnectTimeout(context.getResources().getInteger(R.integer.image_loader_connection_timeout), TimeUnit.SECONDS);
        client.setReadTimeout(context.getResources().getInteger(R.integer.image_loader_read_timeout), TimeUnit.SECONDS);
         */

        client.newBuilder().connectTimeout(context.getResources().getInteger(R.integer.image_loader_connection_timeout), TimeUnit.SECONDS);
        client.newBuilder().readTimeout(context.getResources().getInteger(R.integer.image_loader_read_timeout), TimeUnit.SECONDS);

        //TODO 后期需要添加Okhttp对Https请求的支持
        /**client.setSslSocketFactory(new TrustStoreInteractor()
         .getSSLContext(context)
         .getSocketFactory()
         );*/

        //File folder = createDefaultCacheDir(context, cachePath);
        //builder.downloader(new OkHttpDownloader(folder, calculateDiskCacheSize(folder)));

        builder.downloader(new OkHttp3Downloader(client));
        return builder;
    }

    private Picasso.Builder setupLoaderClient(Context context, ProgressListener listener, boolean withCache) {
        Picasso.Builder builder = new Picasso.Builder(context);
        OkHttpClient client = getDefaultProgressBarClient(context, listener, withCache);

        /** OkHttp 2.1之后没有次方法，需要使用Builder构建
         client.setConnectTimeout(context.getResources().getInteger(R.integer.image_loader_connection_timeout), TimeUnit.SECONDS);
         client.setReadTimeout(context.getResources().getInteger(R.integer.image_loader_read_timeout), TimeUnit.SECONDS);
         */
        client.newBuilder().connectTimeout(context.getResources().getInteger(R.integer.image_loader_connection_timeout), TimeUnit.SECONDS);
        client.newBuilder().readTimeout(context.getResources().getInteger(R.integer.image_loader_read_timeout), TimeUnit.SECONDS);

        //TODO 后期需要添加Okhttp对Https请求的支持
        /**client.setSslSocketFactory(new TrustStoreInteractor()
         .getSSLContext(context)
         .getSocketFactory()
         );*/

        //File folder = createDefaultCacheDir(context, BIG_CACHE_PATH);
        //builder.downloader(withCache ? new OkHttpDownloader(folder, calculateDiskCacheSize(folder)) : new OkHttpDownloader(client));
        builder.downloader(new OkHttp3Downloader(client));
        return builder;
    }

    /**
     * Not singleton
     */
    private OkHttpClient getProgressBarClientWithPath(final Context context, final ProgressListener listener, String cachePath) {
        File cacheFolder = createDefaultCacheDir(context, cachePath);

        return new OkHttpClient().newBuilder()
                .cache(new Cache(cacheFolder, calculateDiskCacheSize(cacheFolder)))
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response originalResponse = chain.proceed(chain.request());
                        return originalResponse.newBuilder()
                                .body(new ProgressResponseBody(originalResponse.body(), listener))
                                .build();
                    }
                }).build();
    }

    /**
     * Not singleton
     */
    private OkHttpClient getDefaultProgressBarClient(final Context context, final ProgressListener listener, boolean withCache) {
        if (withCache) {
            File cacheFolder = createDefaultCacheDir(context, BIG_CACHE_PATH);

            return new OkHttpClient().newBuilder()
                    .cache(new Cache(cacheFolder, calculateDiskCacheSize(cacheFolder)))
                    .addNetworkInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Response originalResponse = chain.proceed(chain.request());
                            return originalResponse.newBuilder()
                                    .body(new ProgressResponseBody(originalResponse.body(), listener))
                                    .build();
                        }
                    }).build();
        } else {
            return new OkHttpClient().newBuilder().addNetworkInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                            .body(new ProgressResponseBody(originalResponse.body(), listener))
                            .build();
                }
            }).build();
        }
    }

    private Picasso.Builder setupListener(Picasso.Builder builder) {
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                // no op
                Logger.e(Logger.TAG, "Failed to load image: " + uri.toString(), exception);
            }
        });
        return builder;
    }

    public void shutdown() {
        if (picasso == null) return;

        Logger.d(Logger.TAG, "Image loader has been shutdown");
        picasso.shutdown();
        callback = null;
    }

    public ImageLoader cancelRequest(ImageView imageView) {
        picasso.cancelRequest(imageView);
        return this;
    }

    public ImageLoader load(final int resourceId) {
        cleanResources();
        this.imageResourceId = resourceId;
        return this;
    }

    public ImageLoader load(final String imageUri) {
        cleanResources();
        this.imageUri = imageUri;
        return this;
    }

    public ImageLoader withPlaceholder(final int placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public ImageLoader withErrorImage(final int errorImage) {
        this.errorImage = errorImage;
        return this;
    }

    public ImageLoader withTag(final String tag) {
        this.tag = tag;
        return this;
    }

    public ImageLoader withCallback(final Callback callback) {
        this.callback = callback;
        return this;
    }

    public ImageLoader simple(boolean isSimple) {
        this.simple = isSimple;
        return this;
    }

    public ImageLoader withScaling(boolean withScaling, int widthResId, int heightResId) {
        this.withScaling = withScaling;
        this.widthResId = widthResId;
        this.heigthResId = heightResId;

        return this;
    }

    public ImageLoader withTransformation(Transformation transformation) {
        this.transformation = transformation;
        return this;
    }

    public ImageLoader fit() {
        this.fit = true;
        return this;
    }

    public ImageLoader centerCrop() {
        this.centerCrop = true;
        return this;
    }

    public ImageLoader centerInside() {
        this.centerInside = true;
        return this;
    }

    public ImageLoader resize(final int widthResId, final int heightResId) {
        this.widthResId = widthResId;
        this.heigthResId = heightResId;
        return this;
    }

    public void pause(final String tag) {
        if (picasso == null) return;

        picasso.pauseTag(tag);
    }

    public void resume(final String tag) {
        if (picasso == null) return;

        picasso.resumeTag(tag);
    }

    public void into(final ImageView imageView) {
        run(imageView);
    }

    private void run(final ImageView imageView) {
        RequestCreator creator = picasso.load(imageUri);
        if (!TextUtils.isEmpty(tag)) creator.tag(tag);
        if (transformation != null) creator.transform(transformation);
        if (placeholder != DEFAULT_PLACEHOLDER) creator.placeholder(placeholder);
        if (errorImage != DEFAULT_PLACEHOLDER) creator.error(errorImage);
        if (fit) creator.fit();
        if (centerCrop) creator.centerCrop();
        if (centerInside) creator.centerInside();

        if (widthResId != DEFAULT_PLACEHOLDER && heigthResId != DEFAULT_PLACEHOLDER) {
            Resources res = imageView.getResources();
            creator.resize(
                    res.getInteger(widthResId),
                    res.getInteger(heigthResId)
            );
        }

        creator.into(imageView, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                if (callback != null) callback.onSuccess();
            }

            @Override
            public void onError() {
                if (callback != null) callback.onError();
            }
        });
    }


    private File createDefaultCacheDir(Context context, String path) {
        File cacheDir = context.getApplicationContext().getExternalCacheDir();
        if (cacheDir == null)
            cacheDir = context.getApplicationContext().getCacheDir();
        File cache = new File(cacheDir, path);
        if (!cache.exists()) {
            cache.mkdirs();
        }
        return cache;
    }

    private long calculateDiskCacheSize(File dir) {
        long size = Math.min(calculateAvailableCacheSize(dir), MAX_DISK_CACHE_SIZE);
        return Math.max(size, MIN_DISK_CACHE_SIZE);
    }

    @SuppressLint("NewApi")
    private long calculateAvailableCacheSize(File dir) {
        long size = 0;
        try {
            StatFs statFs = new StatFs(dir.getAbsolutePath());
            int sdkInt = Build.VERSION.SDK_INT;
            long totalBytes;
            long availableBytes;
            if (sdkInt < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                int blockSize = statFs.getBlockSize();
                availableBytes = ((long) statFs.getAvailableBlocks()) * blockSize;
                totalBytes = ((long) statFs.getBlockCount()) * blockSize;
            } else {
                availableBytes = statFs.getAvailableBytes();
                totalBytes = statFs.getTotalBytes();
            }
            // Target at least 90% of available or 25% of total space
            size = (long) Math.min(availableBytes * MAX_AVAILABLE_SPACE_USE_FRACTION, totalBytes * MAX_TOTAL_SPACE_USE_FRACTION);
        } catch (IllegalArgumentException ignored) {
            // ignored
        }
        return size;
    }

    /**
     * It is mandatory to clean optional resources as loader can be shared among multiple clients
     * */
    private void cleanResources() {
//        return;
        placeholder = DEFAULT_PLACEHOLDER;
        errorImage = DEFAULT_PLACEHOLDER;
        imageUri = null;
        tag = null;
        imageResourceId = DEFAULT_PLACEHOLDER;
        simple = false;
        withScaling = false;
        widthResId = DEFAULT_PLACEHOLDER;
        heigthResId = DEFAULT_PLACEHOLDER;
        transformation = null;
        callback = null;
        fit = false;
        centerCrop = false;
        centerInside = false;
    }

    public interface Callback {
        void onSuccess();
        void onError();
    }

    public interface ProgressListener {
        @WorkerThread
        void update(@IntRange(from = 0, to = 100) int percent);
    }

    private static class ProgressResponseBody extends ResponseBody {

        private final ResponseBody responseBody;
        private final ProgressListener progressListener;
        private BufferedSource bufferedSource;

        public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
            this.responseBody = responseBody;
            this.progressListener = progressListener;
        }

        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override
        public long contentLength() {
            return responseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {

            return new ForwardingSource(source) {
                long totalBytesRead = 0L;

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    if (progressListener != null) {
                        progressListener.update(
                                ((int) ((100 * totalBytesRead) / responseBody.contentLength())));
                    }
                    return bytesRead;
                }
            };
        }
    }
}
