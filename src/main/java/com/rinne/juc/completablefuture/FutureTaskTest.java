package com.rinne.juc.completablefuture;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @PROJECT_NAME: JUC
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/7/10 16:46
 */
public class FutureTaskTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<String> futureTask=new FutureTask<>(new MyThread());
        Thread mythread=new Thread(futureTask,"t1");
        mythread.start();
        System.out.println(mythread.getName());
        //注意get()方法很有可能会导致阻塞情况，因为它会持续等待该线程处理完拿到结果之后，再运行其他线程。
        String s = futureTask.get();
        System.out.println(s);
    }

    static class MyThread implements Callable<String> {

        @Override
        public String call() throws Exception {
            System.out.println("---call()调用");
            return "hello,callable";
        }
    }
}
