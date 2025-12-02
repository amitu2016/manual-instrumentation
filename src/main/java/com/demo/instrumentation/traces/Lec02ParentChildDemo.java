package com.demo.instrumentation.traces;

import com.demo.instrumentation.CommonUtil;
import com.demo.instrumentation.OpenTelemetryConfig;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;

public class Lec02ParentChildDemo {

    private static final Tracer tracer = OpenTelemetryConfig.tracer(Lec02ParentChildDemo.class);

    public static void main(String[] args) {
        var demo = new Lec02ParentChildDemo();
        //POST /orders
        demo.processOrder();

        CommonUtil.sleepSeconds(2);
    }


    private void processOrder(){
        var span = tracer.spanBuilder("processOrder").startSpan();
        try(var scope = span.makeCurrent()) {
            processPayment();
            //deductInventory();
            sendNotification();
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

    private void processPayment(){
        var span = tracer.spanBuilder("processPayment").startSpan();
        try (var scope = span.makeCurrent()){
        CommonUtil.sleepMillis(150);
        deductInventory();
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

    private void deductInventory(){
        var span = tracer.spanBuilder("deductInventory").startSpan();
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

    private void sendNotification(){
        var span = tracer.spanBuilder("sendNotification").startSpan();
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
