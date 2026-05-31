package swati4star.createpdf.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundExecutor {
    private static final ExecutorService mExecutor = Executors.newFixedThreadPool(4);
    private static final Handler mMainHandler = new Handler(Looper.getMainLooper());

    public static void execute(Runnable runnable) {
        mExecutor.execute(runnable);
    }

    public static void postToMainThread(Runnable runnable) {
        mMainHandler.post(runnable);
    }
}
