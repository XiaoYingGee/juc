package com.xiaoyingge.sync.ch01;

import org.openjdk.jol.info.ClassLayout;

/**
 * 通过JOL工具类打印Object头信息
 * 测试不同的场景下的markword的变化
 * 001 没有锁的状态
 * 101 偏向锁状态  --如果没有线程ID信息，处于比较特殊的状态中
 * 00  轻量级锁，前面存放着ptr_to_lock_record 指向栈中锁记录的指针
 * 10  重量级锁  前面存放着ptr_to_heavyweight_monitor 指向线程Monitor的指针。
 *
 * @author Xiaoyingge
 * @description
 * @date 2020/4/30 10:35
 */
public class SynchronizedTest01 {

    public static void main (String[] args) throws InterruptedException {
        Object o = new Object();
        /**
         *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
         *       0     4        (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
         *       4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
         *       8     4        (object header)                           e5 01 00 20 (11100101 00000001 00000000 00100000) (536871397)
         *      12     4        (loss due to the next object alignment)
         *      打印出的内容头部4个字节最后三位为001 无锁状态
         *
         *
         */
        System.out.println(ClassLayout.parseInstance(o).toPrintable());
        Thread.sleep(5000);

        Object o2 = new Object();
//        o2.hashCode();
        /**
         *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
         *       0     4        (object header)                           05 00 00 00 (00000101 00000000 00000000 00000000) (5)
         *       4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
         *       8     4        (object header)                           e5 01 00 20 (11100101 00000001 00000000 00100000) (536871397)
         *      12     4        (loss due to the next object alignment)
         *      休眠五秒后，生成的对象处于偏向锁状态，但是线程指针为空
         *      这是因为JVM启动时竞争较激烈，hotspot为了优化性能延迟启用偏向锁
         *      可以通过-XX:BiasedLockingStartupDelay=0设置JVM启动时启用偏向锁功能
         */
        System.out.println(ClassLayout.parseInstance(o2).toPrintable());

        synchronized (o2) {
            /**
             *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
             *       0     4        (object header)                           05 38 f9 02 (00000101 00111000 11111001 00000010) (49887237)
             *       4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
             *       8     4        (object header)                           e5 01 00 20 (11100101 00000001 00000000 00100000) (536871397)
             *      12     4        (loss due to the next object alignment)
             *      真正的加上了偏向锁，头里开始有线程的指针
             */
            System.out.println(ClassLayout.parseInstance(o2).toPrintable());
        }

        //轻量级锁实现
        Thread thread = new Thread(() -> {
            synchronized (o2) {
                System.out.println("thread2 get the lock");
                /**
                 *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
                 *       0     4        (object header)                           40 f3 0f 1b (01000000 11110011 00001111 00011011) (454030144)
                 *       4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
                 *       8     4        (object header)                           e5 01 00 20 (11100101 00000001 00000000 00100000) (536871397)
                 *      12     4        (loss due to the next object alignment)
                 *      新线程进入争抢,线程升级为轻量级锁
                 */
                System.out.println(ClassLayout.parseInstance(o2).toPrintable());
            }
        });
        thread.start();
        thread.join();
        thread.sleep(3000);
        synchronized (o2) {
            System.out.println("main get the lock");
            /**
             *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
             *       0     4        (object header)                           8a 2d 83 17 (10001010 00101101 10000011 00010111) (394472842)
             *       4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
             *       8     4        (object header)                           e5 01 00 20 (11100101 00000001 00000000 00100000) (536871397)
             *       锁又回到主线程手里，lock thread指向又变化了
             */
            System.out.println(ClassLayout.parseInstance(o2).toPrintable());
        }
        //重量级锁
        thread = new Thread(() -> {
            synchronized (o2) {
                System.out.println("thread2 get the lock");
                System.out.println(ClassLayout.parseInstance(o2).toPrintable());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        synchronized (o2) {
            System.out.println("thread1 get the lock");
            System.out.println(ClassLayout.parseInstance(o2).toPrintable());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        /**
         *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
         *       0     4        (object header)                           8a 2d f9 17 (10001010 00101101 11111001 00010111) (402206090)
         *       4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
         *       8     4        (object header)                           e5 01 00 20 (11100101 00000001 00000000 00100000) (536871397)
         *      12     4        (loss due to the next object alignment)
         *
         *      经过争抢，现在尾部最后两位为10，即重量级锁
         */
        System.out.println(ClassLayout.parseInstance(o2).toPrintable());
    }

}
