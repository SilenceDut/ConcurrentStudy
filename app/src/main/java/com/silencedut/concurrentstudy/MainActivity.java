package com.silencedut.concurrentstudy;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.silencedut.asynctaskscheduler.SingleAsyncTask;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    AsyncTask asyncTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        asyncTask =new MyAsyncTask1().execute();

//        new MyAsyncTask2().execute();
//
//        new MyAsyncTask3().execute();

        SingleAsyncTask mSingleAsyncTask =new SingleAsyncTask<Void,String>() {
            @Override
            public String doInBackground() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.i(TAG,"doInBackground: "+Thread.currentThread().getName());
                return "singleTask"+Thread.currentThread().getName();
            }

            @Override
            public void onExecuteSucceed(String s) {
                super.onExecuteSucceed(s);
                Log.i(TAG,"onExecuteSucceed:"+s+Thread.currentThread());
            }

            @Override
            public void onExecuteCancelled(String result) {
                super.onExecuteCancelled(result);
                Log.i(TAG,"onExecuteCancelled:"+result+Thread.currentThread());
            }

            @Override
            public void onExecuteFailed(Exception exception) {
                super.onExecuteFailed(exception);
                Log.i(TAG,"onExecuteCancelled:"+exception.getMessage()+Thread.currentThread());
            }
        };
        mSingleAsyncTask.executeSingle();

   }

    private class MyAsyncTask1 extends AsyncTask<Void,Void, Void>
    {

        @Override
        protected Void doInBackground(Void... params)
        {
//            try
//            {
                Log.e(TAG+"_MyAsyncTask1", Thread.currentThread().getName());
                while (true) {

                }
//            } catch ( e)
//            {
//                e.printStackTrace();
//            }

        }
    }

    private class MyAsyncTask2 extends AsyncTask<Void,Void, Void>
    {

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                Log.e(TAG+"_MyAsyncTask2", Thread.currentThread().getName());
                Thread.sleep(10000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            return null;
        }

    }

    private class MyAsyncTask3 extends AsyncTask<Void,Void, Void>
    {

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                Log.e(TAG+"_MyAsyncTask3", Thread.currentThread().getName());
                Thread.sleep(100);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            return null;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        asyncTask.cancel(true);
    }
}
