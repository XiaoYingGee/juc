package com.xiaoyingge.sync.ch05;

import java.util.Random;

/**
 * 使用两个线程交替打印出 1A2B3C4D。。。。
 *
 * @author Xiaoyingge
 * @description
 * @date 2020/5/21 13:45
 */
public class SwitchPrintCas {

    private static final String[] A = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};

    private static final String[] B = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};

    static Thread t1 = null, t2 = null;

    private static volatile boolean printNumber = true;

    static Random random = new Random();

    public static void main (String[] args) {
        t1 = new Thread(() -> {
            sleepRandomTime();
            for (String s : A) {
                while (!printNumber) {
                }
                System.out.println(s);
                printNumber = false;
            }
        });
        t2 = new Thread(() -> {
            sleepRandomTime();
            for (String s : B) {
                while (printNumber) {

                }
                //如果不加这个判断，在T2先启动时就凉了
                System.out.println(s);
                printNumber = true;
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
