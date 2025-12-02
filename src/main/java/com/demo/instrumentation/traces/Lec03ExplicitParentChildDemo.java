package com.demo.instrumentation.traces;

import com.demo.instrumentation.CommonUtil;
import com.demo.instrumentation.OpenTelemetryConfig;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;

public class Lec03ExplicitParentChildDemo {

    private static final Tracer tracer = OpenTelemetryConfig.tracer(Lec03ExplicitParentChildDemo.class);

    public static void main(String[] args) {
        var demo = new Lec03ExplicitParentChildDemo();
        //POST /orders
        demo.processOrder();

        CommonUtil.sleepSeconds(2);
    }


    private void processOrder(){
        var span = tracer.spanBuilder("processOrder").startSpan();
        try {
            processPayment(span);
            Thread.ofPlatform().start(() -> {
                deductInventory(span);
            });
            Thread.ofVirtual().start(() -> {
                sendNotification(span);
            });
            span.setAttribute("order.id", "12345");
            span.setAttribute("order.amount", 100.0);

            span.setStatus(StatusCode.OK);
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR);
        } finally {
            span.end();
        }

    }

    private void processPayment(Span parent){
        var span = tracer.spanBuilder("processPayment")
                .setParent(Context.current().with(parent))
                .startSpan();
        try{
        CommonUtil.sleepMillis(150);
            span.setAttribute("payment.method", "credit_card");
            span.setAttribute("payment.currency", "USD");

            span.setStatus(StatusCode.OK);
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR);
        } finally {
            span.end();
        }
    }

    private void deductInventory(Span parent){
        var span = tracer.spanBuilder("deductInventory")
                .setParent(Context.current().with(parent))
                .startSpan();
        try {
        CommonUtil.sleepMillis(125);
            span.setAttribute("inventory.item", "12345");
            span.setAttribute("inventory.quantity", 1);

            span.setStatus(StatusCode.OK);
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR);
        } finally {
            span.end();
        }
    }

    private void sendNotification(Span parent){
        var span = tracer.spanBuilder("sendNotification")
                .setParent(Context.current().with(parent))
                .startSpan();
        try {
        CommonUtil.sleepMillis(100);
            span.setAttribute("notification.method", "email");
            span.setAttribute("notification.to", "customer@example.com");

            span.setStatus(StatusCode.OK);
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR);
        } finally {
            span.end();
        }
    }
}
