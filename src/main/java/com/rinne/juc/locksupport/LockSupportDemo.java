package com.rinne.juc.locksupport;

import org.junit.Test;

/**
 * @PROJECT_NAME: JUC
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/7/13 14:46
 */
public class LockSupport {

    //来到LockSupport类来实现阻塞和唤醒，这个类的强大之处就在于不需要锁，也不要求顺序，解决了之前两种方法的痛点
    //其原理是通过unpark方法来给其中的线程传递一个permit通行证，一旦持有通行证就不再阻塞,所以即使顺序颠倒也可以完成唤醒（其实此时与不阻塞并无区别）
    //注意：permit通行证的持有上限是1，不能通过多次unpark颁发多个通行证，所以如果调用一次以上park方法那么一定会阻塞，所以请保证一次park
    @Test
    public void LockSupportDemo(){
        
    }
}
