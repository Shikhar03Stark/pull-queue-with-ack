package com.shikhar03stark.mbroker.common;

import com.shikhar03stark.mbroker.model.Payload;
import com.shikhar03stark.mbroker.queue.AckMessageQueue;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class ConsumerUtils {

    public static Runnable independentConsumer(AckMessageQueue queue, Consumer<AckMessageQueue> consumer) {
        return () -> {
            consumer.accept(queue);
        };
    }

    public static List<Runnable> independentConsumers(AckMessageQueue queue, Consumer<AckMessageQueue> consumer, int count) {
        return IntStream
                .range(0, count)
                .mapToObj(_ -> independentConsumer(queue, consumer))
                .toList();
    }
}
