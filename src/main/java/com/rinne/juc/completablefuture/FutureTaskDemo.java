package com.rinne.juc.completablefuture;

import java.sql.Time;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * @PROJECT_NAME: JUC
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/7/10 17:33
 */
public class FutureTaskDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask futureTask=new FutureTask(()->{
            TimeUnit.MILLISECONDS.sleep(5000);
            return "FutureTaskDemo";
        });
        Thread thread=new Thread(futureTask);
        thread.start();
        //get方法会等到线程执行完毕之后，才能再调用其他线程，会导致其他线程的阻塞,而且执行过程中不清楚中间的过程
//        System.out.println("FutureTask线程"+futureTask.get());
//        System.out.println("主线程活动....");

        //可以使用轮询判断的方式解决这个问题，但是轮询会消耗大量CPU资源，future对于结果的获取都有弊端
        // 所以才引出了CompletableFutureTask类
        while(true){
            if (futureTask.isDone()){
                System.out.println("FutureTask线程"+futureTask.get());
                break;
            }else {
                System.out.println("请稍后....");
                TimeUnit.SECONDS.sleep(1);
            }
        }
        System.out.println("主线程活动.....");
    }

}
