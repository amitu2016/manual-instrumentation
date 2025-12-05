package com.demo.instrumentation.mertics;

import com.demo.instrumentation.CommonUtil;
import com.demo.instrumentation.OpenTelemetryConfig;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;

import java.util.concurrent.ThreadLocalRandom;

public class Lec02CounterWithAttributesDemo {

    private static final Meter meter = OpenTelemetryConfig.meter(Lec02CounterWithAttributesDemo.class);

    public static void main(String[] args) {
        var counter = createProductViewCounter();
        var recorder = new ProductViewRecorder(counter);
        var controller = new ProductController(recorder);

        for (int i = 0; i < 10_000; i++) {
            controller.viewProduct(ThreadLocalRandom.current().nextLong(1, 4));
        }

    }

    private static LongCounter createProductViewCounter() {
        return meter.counterBuilder("app.product.view.count")
                .setDescription("Number of product views")
                .setUnit("{view}")
                .build();
    }

    // REST controller
    private static class ProductController {

        private final ProductViewRecorder recorder;

        private ProductController(ProductViewRecorder recorder) {
            this.recorder = recorder;
        }

        //GET /products/{productId}
        public void viewProduct(long productId){
            CommonUtil.sleepSeconds(1);
            this.recorder.recordView(productId);
        }

    }

    private static class ProductViewRecorder {
        private static final AttributeKey<Long> PRODUCT_ID_KEY = AttributeKey.longKey("product.id");
        private final LongCounter counter;

        private ProductViewRecorder(LongCounter counter) {
            this.counter = counter;
        }

        public void recordView(long productId){
            this.counter.add(1, Attributes.of(PRODUCT_ID_KEY, productId));
        }
    }
}
