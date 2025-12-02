package com.demo.instrumentation;


import java.time.Duration;

public class CommonUtil {

    public static void sleepSeconds(int seconds) {
        sleep(Duration.ofSeconds(seconds));
    }

    public static void sleepMillis(int millis) {
        sleep(Duration.ofMillis(millis));
    }


    public static void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
