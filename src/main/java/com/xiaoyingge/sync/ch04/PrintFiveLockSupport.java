package com.xiaoyingge.sync.ch04;

import java.util.Random;
import java.util.concurrent.locks.LockSupport;

/**
 * A线程从0打印到9.到打印出第5位时，B线程要给出提示，然后A线程继续
 *
 * @author Xiaoyingge
 * @description
 * @date 2020/5/21 11:37
 */
public class PrintFiveLockSupport {

    static Thread t1 = null, t2 = null;

    static Random random = new Random();

    public static void main (String[] args) {
        t1 = new Thread(() -> {
            //让T1,T2随机一个先启动
            try {
                Thread.sleep(random.nextInt(100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < 10; i++) {
                System.out.println("sout " + i);
                if (i == 4) {
                    try {
                        LockSupport.unpark(t2);
                        LockSupport.park();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        t2 = new Thread(() -> {
            //让T1,T2随机一个先启动
            try {
                Thread.sleep(random.nextInt(100));
                LockSupport.park();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("heiheihei");
            LockSupport.unpark(t1);
        });
        t1.start();
        t2.start();
    }
}
