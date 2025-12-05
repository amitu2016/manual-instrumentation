package com.demo.instrumentation;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;

import java.time.Duration;

public class OpenTelemetryConfig {

    private static final String COLLECTOR_ENDPOINT = "http://localhost:4317";
    private static final OpenTelemetry openTelemetry = initOpenTelemetry();

    private static OpenTelemetry initOpenTelemetry() {
        return OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider())
                .setMeterProvider(meterProvider())
                .build();
    }

    private static SdkTracerProvider tracerProvider () {
        var exporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint(COLLECTOR_ENDPOINT)
                .build();

        var processor = SimpleSpanProcessor.create(exporter);

        return SdkTracerProvider.builder()
                .setResource(resource())
                .addSpanProcessor(processor)
                .build();
    }

    private static SdkMeterProvider meterProvider() {
        var exporter = OtlpGrpcMetricExporter.builder()
                .setEndpoint(COLLECTOR_ENDPOINT)
                .build();
        var metricReader = PeriodicMetricReader.builder(exporter)
                .setInterval(Duration.ofSeconds(5))
                .build();
        return SdkMeterProvider.builder()
                .setResource(resource())
                .registerMetricReader(metricReader)
                .build();
    }

    private static Resource resource() {
        return Resource.create(Attributes.of(
                AttributeKey.stringKey("service.name"), "manual-instrumentation"
        ));
    }

    public static Tracer tracer(Class<?> clazz) {
        return openTelemetry.getTracer(clazz.getName());
    }

    public static Meter meter(Class<?> type) {
        return openTelemetry.getMeter(type.getName());
    }
}
