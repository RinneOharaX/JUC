package com.rinne.juc.completablefuture.api;


import com.rinne.juc.completablefuture.CompletableFutureCase;
import lombok.AllArgsConstructor;
import org.junit.Test;

import java.sql.Time;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

/**
 * @PROJECT_NAME: JUC
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/7/12 10:36
 */
public class CompletableFutureApiTest {
    @Test
    public void CompletbaleFutureThenApplyTest() {
        //thenApply方法可以对返回的结果进行进一步处理，但是如果进行处理的过程中遇见的异常那么会中断处理，依赖于上一个线程的结果
        System.out.println(CompletableFuture.supplyAsync(() -> {
            return 50;
        }).thenApply(f -> {
            int i = 10 / 0;
            return f + 50;
        }).thenApply(f -> {
            System.out.println("有异常走不到这步");
            return f + 50;
        }).join());
    }

    @Test
    //handle与thenApply的方法类似，区别在于如果出现异常依旧能继续往下走（但下面的步骤无法得到出现异常步骤的返回值），并且能捕获异常进一步处理
    public void CompletbaleFutureHandleTest() {
        System.out.println(CompletableFuture.supplyAsync(() -> {
            return 50;
        }).handle((f, t) -> {
            int i = 10 / 0;
            return f + 50;
        }).handle((f, t) -> {
            System.out.println("有异常也能走到");
            return f;
        }).join());
    }

    //thenAccept()是消费线程中得到的结果，提供的是一个Consumer接口,这个方法依赖于上一个线程的结果
    @Test
    public void CompletableFutureThenAcceptTest() {
        CompletableFuture.supplyAsync(() -> {
            return 100;
        }).thenAccept(System.out::println).join();
    }

    //thenRun()方法不依赖于上面线程的结果，相当于直接另起一个线程，但它所用的线程池与上面的线程保持一致。
    //如果想另起一个线程，但又不想使用同一个线程池可以使用thenRunAsync()方法指定想要的线程池，异步处理
    @Test
    public void CompletableFutureThenRunTest() throws InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        System.out.println(CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("线程名为:" + Thread.currentThread().getName());
            return 100;
        }, threadPool).thenRun(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("另起的线程名为:" + Thread.currentThread().getName());
        }).join());
        threadPool.shutdown();
    }

    //applyToEither()该方法会比较两个线程哪个任务完成的更快，去回调更快完成的线程结果
    @Test
    public void CompletableFutreApplyToEither() {
        System.out.println(CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 50;
        }).applyToEither(CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 100;
        }), f -> {
            return f;
        }).join());
    }

    //thenCombine()方法可以等到两个线程都运行结束之后，获得两个线程的运行结果，其中一个线程未完成的话，会等待至完成
    @Test
    public void CompletableFutureThenCombine() {
        System.out.println(CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 50;
        }).thenCombine(CompletableFuture.supplyAsync(() -> {
            return 100;
        }), (f1, f2) -> {
            return f1+f2;
        }).join());
    }
}
