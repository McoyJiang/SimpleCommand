package com.danny_mcoy.simplecommad.receiver;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.danny_mcoy.simplecommad.extra.CommandStatus;
import com.danny_mcoy.simplecommad.extra.Params;
import com.danny_mcoy.simplecommad.log.Logger;

/**
 * Created by Danny_姜新星 on 5/20/2015.
 */
public class AppResultReceiver extends ResultReceiver {

    private ResultListener listener;

    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler Handler from the listener
     */
    public AppResultReceiver(Handler handler, ResultListener listener) {
        super(handler);
        this.listener = listener;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        Logger.d(Logger.TAG, "onReceiveResult");
        if (listener == null) return;
        CommandStatus commandStatus = (CommandStatus) resultData.getSerializable(Params.CommandMessage.EXTRA_STATUS);
        switch (commandStatus) {
            case SUCCESS:
                listener.onResultSuccess(resultData);
                break;
            case FAIL:
                listener.onResultFailed(resultData);
                break;
            case IN_PROGRESS:
                listener.onResultProgress(resultData);
                break;
            default:
                Logger.d(Logger.TAG, "Failed process command");
                if (true)
                    throw new IllegalArgumentException("Did you forget to add a new Command status processor?");
        }
    }

    public void setupListener(ResultListener listener) {
        this.listener = listener;
    }

    public interface ResultListener {
        void onResultSuccess(Bundle resultData);
        void onResultFailed(Bundle resultData);
        void onResultProgress(Bundle resultData);
    }
}
