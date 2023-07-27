package com.rinne.juc.completablefuture;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @PROJECT_NAME: JUC
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/7/11 12:50
 */

/*利用真实电商案例来介绍CompletableFuture的强大之处
* 我们想要从各个电商平台，查询同一件商品来对比价格，假定从数据库查询一件商品需要消耗1秒钟时间
* 如果不采用多线程的方式，那么一个线程处理这些会变得相当缓慢
* */
public class CompletableFutureCase {
    static List<NetMall> netMallList=Arrays.asList(
            new NetMall("jd"),
            new NetMall("tb"),
            new NetMall("dd")
    );

    //不使用多线程的方式
    public static List<String> getprice(List<NetMall> netMallList,String productName) throws Exception {
        //利用stream流的方式处理字符串
        List<String> collect = netMallList.stream().map(netMall -> {
            try {
                //String.format是转换为想要的格式，%s是占位符的意思，在后续的参数中填充，%.2f是保留两位小数的浮点类型
                return String.format(productName + " in %s price is %.2f", netMall.getNetMallName(), netMall.getPrice(productName));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        return collect;
    }
    //使用多线程的方式
    public static List<String> getPriceByCompletableFuture(List<NetMall> netMallList,String productName) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        //利用stream流的方式处理字符串
        List<String> collect = netMallList.stream().map(netMall -> {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return String.format(productName + " in %s price is %.2f", netMall.getNetMallName(), netMall.getPrice(productName));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            },executorService);
        }).collect(Collectors.toList()).stream().map(CompletableFuture::join)
                .collect(Collectors.toList());
        executorService.shutdown();
        return collect;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("不使用多线程的方式执行...");
        String productName="mysql";
        //不采用多线程的方法看看运行的效率
        long before = System.currentTimeMillis();
        List<String> getprice = CompletableFutureCase.getprice(CompletableFutureCase.netMallList, productName);
        for (String s:getprice){
            System.out.println(s);
        }
        long after=System.currentTimeMillis();
        System.out.println("不采用多线程的方式，请求时间为.."+(after-before)+"ms");

        //采用多线程的方式
        System.out.println("使用多线程的方式执行...");
        long before1 = System.currentTimeMillis();
//        ExecutorService executorService = Executors.newFixedThreadPool(3);
//        for (NetMall netMall:CompletableFutureCase.netMallList){
//          CompletableFuture.supplyAsync(() -> {
//                try {
//                    return String.format(productName + " in %s price is %.2f ", netMall.getNetMallName(), netMall.getPrice(productName));
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }, executorService).whenComplete((s,t)->{
//                if (t==null){
//                    System.out.println(s);
//                }
//            }).exceptionally(throwable -> {
//                return throwable.getCause().getMessage();
//            });
//        }
        List<String> priceByCompletableFuture = CompletableFutureCase.getPriceByCompletableFuture(CompletableFutureCase.netMallList, productName);
        for (String s:priceByCompletableFuture){
            System.out.println(s);
        }
        long after1=System.currentTimeMillis();
        System.out.println("采用多线程的方式，请求时间为.."+(after1-before1)+"ms");

    }
}
    @AllArgsConstructor
    class NetMall {
        //定义电商的名字
        @Getter
        private String NetMallName;

        //根据产品名称得到产品价格的方法
        public BigDecimal getPrice(String productName) throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(1000);
            //用这个表达式来模拟价格，chatAt获取的是ASCLL码转换的数字
            double price = ThreadLocalRandom.current().nextDouble() * 2 + productName.charAt(0);
            return new BigDecimal(price);
        }
    }
