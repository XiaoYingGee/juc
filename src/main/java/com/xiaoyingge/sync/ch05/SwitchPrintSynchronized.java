package com.xiaoyingge.sync.ch05;

import java.util.Random;

/**
 * 使用两个线程交替打印出 1A2B3C4D。。。。
 *
 * @author Xiaoyingge
 * @description
 * @date 2020/5/21 13:25
 */
public class SwitchPrintSynchronized {

    private static final String[] A = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};

    private static final String[] B = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};

    static Thread t1 = null, t2 = null;

    static Random random = new Random();

    private static volatile boolean printNumber = true;

    public static void main (String[] args) {
        t1 = new Thread(() -> {
            sleepRandomTime();
            for (String s : A) {
                synchronized (SwitchPrintSynchronized.class) {
                    System.out.println(s);
                    try {
                        printNumber = false;
                        SwitchPrintSynchronized.class.notify();
                        SwitchPrintSynchronized.class.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t2 = new Thread(() -> {
            sleepRandomTime();
            for (String s : B) {
                synchronized (SwitchPrintSynchronized.class) {
                    //如果不加这个判断，在T2先启动时就凉了
                    if (printNumber) {
                        try {
                            SwitchPrintSynchronized.class.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println(s);
                    printNumber = true;
                    SwitchPrintSynchronized.class.notify();
                }
            }
        });
        t1.start();
        t2.start();

    }

    private static void sleepRandomTime () {
        try {
            Thread.sleep(random.nextInt(10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
