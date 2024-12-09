package com.shikhar03stark.mbroker.queue.functional;

import com.shikhar03stark.mbroker.model.Payload;

@FunctionalInterface
public interface ProcessPayloadFunction {
    void processPayload(Payload payload, AckFunction ack);
}
