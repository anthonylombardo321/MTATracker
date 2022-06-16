package io.anthonylombardo321.github.mtatracker;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.os.Looper;
import android.os.Handler;

public class ApplicationExecutors {
    private final Executor background;
    private final Executor mainThread;

    public Executor getBackground(){
        return background;
    }
    public Executor getMainThread(){
        return mainThread;
    }
    public ApplicationExecutors(){
        this.background = Executors.newSingleThreadExecutor();
        this.mainThread = new MainThreadExecutor();
    }

    private static class MainThreadExecutor implements Executor{
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        @Override
        public void execute(Runnable command){
            mainThreadHandler.post(command);
        }
    }
}
