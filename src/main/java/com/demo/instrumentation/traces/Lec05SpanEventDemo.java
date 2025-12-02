package com.demo.instrumentation.traces;

import com.demo.instrumentation.CommonUtil;
import com.demo.instrumentation.TraceUtil;

public class Lec05SpanEventDemo {

    public static void main(String[] args) {
        var demo = new Lec05SpanEventDemo();
        //POST /orders
        demo.processOrder();

        CommonUtil.sleepSeconds(2);
    }


    private void processOrder(){
            TraceUtil.trace("processOrder", span -> {
            processPayment();
            //Thread.ofPlatform().start(this::deductInventory);
            //Thread.ofVirtual().start(this::sendNotification);
                deductInventory();
                sendNotification();
            span.setAttribute("order.id", "12345");
            span.setAttribute("order.amount", 100.0);
        });
    }

    private void processPayment(){
       TraceUtil.trace("processPayment", span -> {
        CommonUtil.sleepMillis(150);
        span.setAttribute("payment.method", "credit_card");
        span.setAttribute("payment.currency", "USD");
        span.addEvent("payment failed... retrying...");
        CommonUtil.sleepMillis(150);
        span.addEvent("payment failed... retrying...");
       });

    }

    private void deductInventory(){
        TraceUtil.trace("deductInventory", span -> {
            CommonUtil.sleepMillis(125);
            span.setAttribute("inventory.item", "12345");
            span.setAttribute("inventory.quantity", 1);
        });

    }

    private void sendNotification(){
        TraceUtil.trace("sendNotification", span -> {
            CommonUtil.sleepMillis(100);
            span.setAttribute("notification.method", "email");
            span.setAttribute("notification.to", "customer@example.com");
        });
    }
}
