package com.yu.ymind;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import org.junit.jupiter.api.Test;

public class IOTest {
    @Test
    void test() {
        Scheduler io = Schedulers.io();
        while (true) {
            io.scheduleDirect(() -> {
                System.out.println(Thread.currentThread().getName() + " print hello");
                try {
                    Thread.sleep(50000l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
