package com.xiaoyingge.sync.ch04;

import java.util.Random;

/**
 * A线程从0打印到9.到打印出第5位时，B线程要给出提示，然后A线程继续
 *
 * @author Xiaoyingge
 * @description
 * @date 2020/5/21 11:28
 */
public class PrintFiveSynchronized {

    static Thread t1 = null, t2 = null;

    private static volatile int i = 0;

    private static final Object o = new Object();

    static Random random = new Random();

    public static void main (String[] args) {
        t1 = new Thread(() -> {
            //让T1,T2随机一个先启动
            try {
                Thread.sleep(random.nextInt(100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (o) {
                System.out.println("t1 get lock");
                for (; i < 10; i++) {
                    System.out.println("sout " + i);
                    if (i == 4) {
                        try {
                            o.notify();
                            o.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        t2 = new Thread(() -> {
            //让T1,T2随机一个先启动
            try {
                Thread.sleep(random.nextInt(100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (o) {
                System.out.println("t2 get lock");
                try {
                    if (i != 4) {
                        o.wait();
                    }
                    System.out.println("heiheihei");
                    o.notify();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        t1.start();
        t2.start();

    }
}
