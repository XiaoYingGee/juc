package com.xiaoyingge.sync.ch05;

import java.util.Random;
import java.util.concurrent.locks.LockSupport;

/**
 * 使用两个线程交替打印出 1A2B3C4D。。。。
 *
 * @author Xiaoyingge
 * @description
 * @date 2020/5/21 13:40
 */
public class SwitchPrintLockSupport {

    private static final String[] A = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};

    private static final String[] B = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};

    static Thread t1 = null, t2 = null;

    static Random random = new Random();

    public static void main (String[] args) {
        t1 = new Thread(() -> {
            sleepRandomTime();
            for (String s : A) {
                System.out.println(s);
                LockSupport.unpark(t2);
                LockSupport.park();
            }
        });
        t2 = new Thread(() -> {
            sleepRandomTime();
            for (String s : B) {
                LockSupport.park();
                //如果不加这个判断，在T2先启动时就凉了
                System.out.println(s);
                LockSupport.unpark(t1);
            }
        });
        t1.start();
        t2.start();
    }

    private static void sleepRandomTime () {
        try {
            Thread.sleep(random.nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
