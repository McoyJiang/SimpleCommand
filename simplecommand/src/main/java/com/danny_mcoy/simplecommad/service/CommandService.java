package com.danny_mcoy.simplecommad.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.ResultReceiver;

import com.danny_mcoy.simplecommad.cmd.Command;
import com.danny_mcoy.simplecommad.extra.Params;
import com.danny_mcoy.simplecommad.log.Logger;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Danny_姜新星 on 6/28/2016.
 *
 * 1 内部维护一个阻塞式的线程池
 * 2 在子线程中执行耗时操作
 * 3 当耗时操作执行完之后，调用stopSelf(int id)方法结束服务分支
 */
public class CommandService extends Service {

    private static final long KEEP_ALIVE = 1;
    private static final int THREADS = 4;
    private static final int MAX_THREAD = 6;

    private final ConcurrentLinkedQueue<Integer> startIdQueue = new ConcurrentLinkedQueue<>();
    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(THREADS, MAX_THREAD, KEEP_ALIVE,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    public final static void start(final Context context, final Command command, final ResultReceiver receiver) {
        context.startService(
                new Intent(context, CommandService.class)
                        .putExtra(Params.Intent.EXTRA_COMMAND, command)
                        .putExtra(Params.Intent.EXTRA_RECEIVER, receiver)
        );
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        pool.shutdown();
        super.onDestroy();
        Logger.d(Logger.TAG, "Network service is destroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        processIntent(intent, startId);
        return START_REDELIVER_INTENT;
    }


    private void processIntent(final Intent intent, int startId) {
        startIdQueue.add(startId);
        pool.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    final Command command = intent.getParcelableExtra(Params.Intent.EXTRA_COMMAND);
                    final ResultReceiver resultReceiver = intent.getParcelableExtra(Params.Intent.EXTRA_RECEIVER);
                    if (command != null) {
                        command.execute(getApplicationContext(), resultReceiver);
                    }
                } catch (Exception e){
                    Logger.e(Logger.TAG, "Failed to start command", e);
                } finally {
                    stopSelf(startIdQueue.remove());
                }

            }
        });
    }
}
