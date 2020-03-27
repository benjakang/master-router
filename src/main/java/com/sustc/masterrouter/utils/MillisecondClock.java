package com.sustc.masterrouter.utils;

public class MillisecondClock {
    private long rate = 0;// 频率
    private volatile long now = 0;// 当前时间

    public static final MillisecondClock CLOCK = new MillisecondClock(1);

    private MillisecondClock(long rate) {
        this.rate = rate;
        this.now = System.currentTimeMillis();
//        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        start();
    }

    private void start() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(rate);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                now = System.currentTimeMillis();
            }
        }).start();
    }

    public long now() {
        return now;
    }
}
