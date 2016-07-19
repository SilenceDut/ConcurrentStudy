package com.silencedut.concurrentstudy;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created by SilenceDut on 16/7/18.
 */

public class FutureTest {
    public static void main(String[] args) {
        FutureTest futureTest = new FutureTest();
        futureTest.useExecutor();
        futureTest.useThread();
    }

    private void useExecutor() {
        SumTask sumTask = new SumTask(1000);
        ExecutorService executor = Executors.newCachedThreadPool();
        FutureTask<Integer> futureTask = new FutureTask<Integer>(sumTask);
        executor.submit(futureTask);
        executor.shutdown();
        try {
            System.out.println("task运行结果" + futureTask.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void useThread() {
        SumTask sumTask = new SumTask(500);
        ExecutorService executor = Executors.newCachedThreadPool();
        FutureTask<Integer> futureTask = new FutureTask<Integer>(sumTask);
        Thread thread = new Thread(futureTask);
        thread.start();
        try {
            System.out.println("task运行结果" + futureTask.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    class SumTask implements Callable<Integer> {
        int number;
        public SumTask(int num) {
            this.number = num;
        }
        @Override
        public Integer call() throws Exception {
            System.out.println(Thread.currentThread());
            Thread.sleep(5000);
            int sum = 0;
            for (int i = 0; i < number; i++) {
                sum += i;
            }
            return sum;
        }
    }
}
