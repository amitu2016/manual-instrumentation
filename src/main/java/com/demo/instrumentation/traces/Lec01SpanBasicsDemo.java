package com.demo.instrumentation.traces;

import com.demo.instrumentation.CommonUtil;
import com.demo.instrumentation.OpenTelemetryConfig;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;

public class Lec01SpanBasicsDemo {

    private static final Tracer tracer = OpenTelemetryConfig.tracer(Lec01SpanBasicsDemo.class);

    public static void main(String[] args) {
        var demo = new Lec01SpanBasicsDemo();
        //POST /orders
        demo.processOrder();

        CommonUtil.sleepSeconds(2);
    }


    private void processOrder(){
        var span = tracer.spanBuilder("processOrder").startSpan();
        try {
            processPayment();
            deductInventory();
            sendNotification();
            span.setAttribute("order.id", "12345");
            span.setAttribute("order.amount", 100.0);
            span.setAttribute("order.currency", "USD");

            span.setStatus(StatusCode.OK);
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR);
        } finally {
            span.end();
        }

    }

    private void processPayment(){
        CommonUtil.sleepMillis(150);
    }

    private void deductInventory(){
        CommonUtil.sleepMillis(125);
    }

    private void sendNotification(){
        CommonUtil.sleepMillis(100);
       // throw new RuntimeException("Failed to send notification");
    }


}
