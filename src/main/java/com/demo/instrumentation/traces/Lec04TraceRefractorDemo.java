package com.demo.instrumentation.traces;

import com.demo.instrumentation.CommonUtil;
import com.demo.instrumentation.OpenTelemetryConfig;
import com.demo.instrumentation.TraceUtil;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;

public class Lec04TraceRefractorDemo {

    public static void main(String[] args) {
        var demo = new Lec04TraceRefractorDemo();
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
