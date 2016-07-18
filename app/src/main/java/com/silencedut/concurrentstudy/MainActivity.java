package com.silencedut.concurrentstudy;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v4.os.AsyncTaskCompat;
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
        for(int i = 1 ;i <= 145 ; i++)
        {
            new MyAsyncTask2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        new Thread("Thread1") {
            @Override
            public void run() {
                 final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
                 final int CORE_POOL_SIZE = CPU_COUNT + 1;
                 final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
                Log.i(TAG,CPU_COUNT+"");
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

    private class MyAsyncTask2 extends AsyncTask<Void,Void, Void>
    {

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                Log.e(TAG, Thread.currentThread().getName());
                Thread.sleep(100);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            return null;
        }

    }



}
