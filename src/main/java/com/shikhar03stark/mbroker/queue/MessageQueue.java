package com.shikhar03stark.mbroker.queue;

import com.shikhar03stark.mbroker.exception.NoPayloadForConsumptionException;
import com.shikhar03stark.mbroker.model.Payload;

public interface MessageQueue {
    void publish(Payload payload);
    Payload poll() throws NoPayloadForConsumptionException;
}
