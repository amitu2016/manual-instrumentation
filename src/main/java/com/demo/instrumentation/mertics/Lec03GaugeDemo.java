package com.demo.instrumentation.mertics;

import com.demo.instrumentation.CommonUtil;
import com.demo.instrumentation.OpenTelemetryConfig;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.metrics.ObservableLongGauge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class Lec03GaugeDemo {
    private static final Meter meter = OpenTelemetryConfig.meter(Lec03GaugeDemo.class);
    private static final Logger logger = LoggerFactory.getLogger(Lec03GaugeDemo.class);

    public static void main(String[] args) {
        try(var gauge = createJvmMemoryUsageGauge()){
            simulateMemoryUsage();
        }catch (Exception e){
            logger.error("Error", e);
        }
    }

    private static void simulateMemoryUsage() {
        var memory = new ArrayList<byte[]>();

        for (int i = 0; i < 10_000; i++) {
            memory.add(new byte[1024 * 1024 * 10]);
            logger.info("Allocated {} MB", memory.size() * 10);
            CommonUtil.sleepSeconds(1);

            if(i % 60 == 0){
                memory.clear();
                logger.info("Cleared memory");
                System.gc(); // force garbage collection for demo purposes
            }
        }
    }

    private static ObservableLongGauge createJvmMemoryUsageGauge() {
        return meter.gaugeBuilder("jvm.memory.usage")
                .ofLongs()
                .setDescription("JVM memory usage")
                .setUnit("bytes")
                .buildWithCallback(measurement -> {
                    var runtime = Runtime.getRuntime();
                    var freeMemory = runtime.freeMemory();
                    var totalMemory = runtime.totalMemory();
                    var usedMemory = totalMemory - freeMemory;
                    logger.info("JVM memory usage: free={}, total={}, used={}", freeMemory, totalMemory, usedMemory);
                    measurement.record(usedMemory);
                });
    }

}
