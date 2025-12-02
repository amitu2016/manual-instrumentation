package com.demo.instrumentation.traces;

import com.demo.instrumentation.CommonUtil;
import com.demo.instrumentation.TraceUtil;
import io.opentelemetry.context.Context;

public class Lec06AsyncContextPropagationDemo {

    public static void main(String[] args) {
        var demo = new Lec06AsyncContextPropagationDemo();
        //POST /orders
        demo.processOrder();

        CommonUtil.sleepSeconds(2);
    }


    private void processOrder(){
            TraceUtil.trace("processOrder", span -> {
            var t1 = Thread.ofPlatform().start(Context.current().wrap(this::processPayment));
            var t2 = Thread.ofVirtual().start(Context.current().wrap(this::deductInventory));
            var t3 = Thread.ofVirtual().start(Context.current().wrap(this::sendNotification));

            awaitCompletion(t1, t2, t3);

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

    private void awaitCompletion(Thread... threads){
        try {
            for (var thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
