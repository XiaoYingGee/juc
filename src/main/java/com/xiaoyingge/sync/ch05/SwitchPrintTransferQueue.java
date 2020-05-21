package com.xiaoyingge.sync.ch05;

import java.util.Random;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

/**
 * 使用两个线程交替打印出 1A2B3C4D。。。。
 *
 * @author Xiaoyingge
 * @description
 * @date 2020/5/21 19:16
 */
public class SwitchPrintTransferQueue {

    private static final String[] A = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};

    private static final String[] B = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};

    static Thread t1 = null, t2 = null;

    static TransferQueue<String> lock = new LinkedTransferQueue<>();

    static Random random = new Random();

    public static void main (String[] args) {
        t1 = new Thread(() -> {
            sleepRandomTime();
            for (String s : A) {
                try {
                    lock.transfer(s);
                    System.out.println(lock.take());

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t2 = new Thread(() -> {
            sleepRandomTime();
            for (String s : B) {
                try {
                    System.out.println(lock.take());
                    lock.transfer(s);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
