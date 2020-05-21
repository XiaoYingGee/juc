package com.xiaoyingge.sync.ch05;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用两个线程交替打印出 1A2B3C4D。。。。
 *
 * @author Xiaoyingge
 * @description
 * @date 2020/5/21 18:57
 */
public class SwitchPrintReentrantLock {

    private static final String[] A = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};

    private static final String[] B = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};

    static Thread t1 = null, t2 = null;

    private static ReentrantLock lock = new ReentrantLock(true);

    static Condition condition1 = lock.newCondition();

    static Condition condition2 = lock.newCondition();

    static Random random = new Random();

    public static void main (String[] args) {
        t1 = new Thread(() -> {
            sleepRandomTime();
            try {
                lock.lock();
                for (String s : A) {
                    System.out.println(s);
                    condition2.signalAll();
                    condition1.await();
                }
                condition2.signalAll();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        });
        t2 = new Thread(() -> {
            sleepRandomTime();
            try {
                lock.lock();
                for (String s : B) {
                    //如果不加这个判断，在T2先启动时就凉了

                    System.out.println(s);
                    condition1.signalAll();
                    condition2.await();
                }
                condition1.signalAll();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
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
