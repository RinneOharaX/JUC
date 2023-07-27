package com.rinne.juc.completablefuture;

import java.sql.Time;
import java.util.concurrent.*;

/**
 * @PROJECT_NAME: JUC
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/7/10 17:02
 */
//这个类来演示一下FutureTask类结合线程池，给传统任务性能带来的提升，可以看到消耗时间从1200ms+ 降低到了 500ms+，显著优化
public class PerformanceImprovement {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Long before=System.currentTimeMillis();
        //创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        FutureTask<String> futureTask01=new FutureTask<>(()->{
            TimeUnit.MILLISECONDS.sleep(500);
            return "futureTest01";
        });
        executorService.submit(futureTask01);
        FutureTask<String> futureTask02=new FutureTask<>(()->{
            TimeUnit.MILLISECONDS.sleep(400);
            return "futureTest02";
        });
        executorService.submit(futureTask02);
        FutureTask<String> futureTask03=new FutureTask<>(()->{
            TimeUnit.MILLISECONDS.sleep(300);
            return "futureTest03";
        });
        executorService.submit(futureTask03);
        System.out.println(futureTask01.get());
        System.out.println(futureTask02.get());
        System.out.println(futureTask03.get());
        Long after=System.currentTimeMillis();
        System.out.println("总共耗时"+(after-before)+"ms");
        executorService.shutdown();
    }
}

//不采用多线程来执行三个任务
 class Tradition{
     public static void main(String[] args) throws InterruptedException {
         long before = System.currentTimeMillis();
         //使用JUC模拟程序运行时间
         TimeUnit.MILLISECONDS.sleep(500);
         TimeUnit.MILLISECONDS.sleep(400);
         TimeUnit.MILLISECONDS.sleep(300);
         long after=System.currentTimeMillis();
         System.out.println("总共耗时"+(after-before)+"ms");
     }
}
