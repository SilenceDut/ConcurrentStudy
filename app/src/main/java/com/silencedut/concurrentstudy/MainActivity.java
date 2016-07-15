package com.silencedut.concurrentstudy;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ThreadLocal<Integer> integerThreadLocal = new ThreadLocal<Integer>();
        integerThreadLocal.set(1);
        Log.i(TAG+Thread.currentThread(),integerThreadLocal.get()+"");

        HandlerThread ioThread = new HandlerThread("IoThread");
        ioThread.start();
        Handler ioHandle = new Handler(ioThread.getLooper());
        ioHandle.post(new Runnable() {
            @Override
            public void run() {
                //do in io thread
                //没调用start方法，没有新开线程，那个线程的 looper调用就就在哪个线程执行
            }
        });

        new Thread("Thread1") {
            @Override
            public void run() {
                Log.i(TAG+Thread.currentThread(),integerThreadLocal.get()+"");
            }
        }.start();

        new Thread("Thread2") {
            @Override
            public void run() {
                integerThreadLocal.set(2);
                Log.i(TAG+Thread.currentThread(),integerThreadLocal.get()+"");
            }
        }.start();




    }


}
