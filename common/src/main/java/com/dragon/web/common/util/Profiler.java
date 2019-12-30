package com.dragon.web.common.util;

import java.util.concurrent.TimeUnit;

/**
 * @author dikelongzai 15399073387@163.com
 * @date 2019-12-30 14:26
 */
public class Profiler {
    private static ThreadLocal<Long> TIME_THREADLOCAL = new ThreadLocal<>();

    public static final void begin() {
        TIME_THREADLOCAL.set(System.currentTimeMillis());
    }

    public static final long end() {
        return (System.currentTimeMillis() - TIME_THREADLOCAL.get());
    }

    public static void main(String[] args) throws Exception {
        begin();
        TimeUnit.SECONDS.sleep(1);
        System.out.println("Cost: " + Profiler.end() + " mills");
    }

    /**
     * 等待超时模式
     * 开发人员经常会遇到这样的方法调用场景：调用一个方法时等待一段时间（一般来说是给
     * 定一个时间段），如果该方法能够在给定的时间段之内得到结果，那么将结果立刻返回，反之，
     * 超时返回默认结果。
     * 前面的章节介绍了等待/通知的经典范式，即加锁、条件循环和处理逻辑3个步骤，而这种
     * 范式无法做到超时等待。而超时等待的加入，只需要对经典范式做出非常小的改动，改动内容
     * 如下所示。
     * 假设超时时间段是T，那么可以推断出在当前时间now+T之后就会超时。
     * 定义如下变量。
     * ·等待持续时间：REMAINING=T。
     * ·超时时间：FUTURE=now+T。
     * 这时仅需要wait(REMAINING)即可，在wait(REMAINING)返回之后会将执行：
     * REMAINING=FUTURE–now。如果REMAINING小于等于0，表示已经超时，直接退出，否则将
     * 继续执行wait(REMAINING)。
     * 上述描述等待超时模式的伪代码如下。
     * // 对当前对象加锁
     * public synchronized Object get(long mills) throws InterruptedException {
     * long future = System.currentTimeMillis() + mills;
     * long remaining = mills;
     * // 当超时大于0并且result返回值不满足要求
     * while ((result == null) && remaining > 0) {
     * wait(remaining);remaining = future - System.currentTimeMillis();
     * }
     * return result;
     * }
     * 可以看出，等待超时模式就是在等待/通知范式基础上增加了超时控制，这使得该模式相
     * 比原有范式更具有灵活性，因为即使方法执行时间过长，也不会“永久”阻塞调用者，而是会按
     * 照调用者的要求“按时”返回。
     *
     * @param mills
     * @return
     * @throws InterruptedException
     */
    public synchronized Object get(long mills) throws InterruptedException {
        Object result = null;
        long future = System.currentTimeMillis() + mills;
        long remain = mills;
        while (result == null && remain > 0) {
            wait(remain);
            remain = future - System.currentTimeMillis();
        }
        return result;
    }
}
