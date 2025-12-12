package com.demo.instrumentation.logs;

import com.demo.instrumentation.CommonUtil;
import com.demo.instrumentation.OpenTelemetryConfig;
import org.slf4j.MDC;

public class Lec02TurboFilterDemo {

    private final static PaymentService paymentService = new PaymentService();

    public static void main(String[] args) {
        OpenTelemetryConfig.setupLoggingAppender();

       for (int i = 0; i < 10; i++) {
           processRequest(i);
       }

       CommonUtil.sleepSeconds(1);
    }

    private static void processRequest(int userId){
        try(var ignored = MDC.putCloseable("userId", String.valueOf(userId))){
            paymentService.processPayment(userId * 100);
        }
    }
}
