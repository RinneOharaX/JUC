package com.rinne.juc.completablefuture;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.util.concurrent.*;
import java.util.function.BiConsumer;

/**
 * @PROJECT_NAME: JUC
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/7/11 11:42
 */
public class CompletableFutureDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //CompletableFuture这个类通常是使用静态方法来创建，而不是通过new对象的方式
        //runAsync()这个方法是没有返回值的，参数是实现了Runnable接口的类,如果不传入线程池的对象则调用CompletableFuture默认的线程池。
        CompletableFuture.runAsync(()->{
            System.out.println("线程名为:"+Thread.currentThread().getName());
        });
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        //如果传入一个自己创建的线程池，那么它就会使用主动使用自己的线程池
        CompletableFuture.runAsync(()->{
            System.out.println("线程名为:"+Thread.currentThread().getName());
        },executorService);

        //supplyAsync()方法区别于runAsync的是，该方法可拥有返回值，所以用的比前者多
        //参数是一个Supplier的函数式接口，可选择是否传入线程池，特性与前者相同
        CompletableFuture<String> completableFuture01 = CompletableFuture.supplyAsync(()->{
            System.out.println("线程名为:"+Thread.currentThread().getName());
            return "Hello,CompletableFuture";
        });

        CompletableFuture<String> completableFuture02 = CompletableFuture.supplyAsync(()->{
            System.out.println("线程名为:"+Thread.currentThread().getName());
            try {
                //设定该业务需要3s完成
                TimeUnit.MILLISECONDS.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "Hello,CompletableFuture";
        },executorService);

        System.out.println(completableFuture01.get());

        //这里使用whenComplete()方法来代替Future中的get方法，它提供了更为强大的异步功能，不会造成阻塞
        //参数是一个BigConsumer接口，s代表线程中的返回值，throwable代表异常信息
        completableFuture02.whenComplete((s,throwable)->{
            //如果不存在异常就拿到线程中的返回值
            if (throwable==null){
                System.out.println(s);
            }
            //否则就打印异常信息
        }).exceptionally((throwable -> {
            System.out.println(throwable.getCause().getMessage());
            return null;
        }));
        //注意，如果CompletableFuture没有使用自定义的线程池，内置的线程池中的线程会类似于守护线程
        //由于主线程运行的太快，而导致守护线程直接被回收，那么就无法完成线程中的任务，需要延迟主线程。所以一般推荐使用自己的线程池
        System.out.println("主线程调用.....");
        executorService.shutdown();
    }
}
