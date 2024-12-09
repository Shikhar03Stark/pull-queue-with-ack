package com.shikhar03stark.mbroker.queue;

import com.shikhar03stark.mbroker.exception.NoPayloadForConsumptionException;
import com.shikhar03stark.mbroker.model.Payload;
import com.shikhar03stark.mbroker.queue.functional.AckFunction;
import com.shikhar03stark.mbroker.queue.functional.ProcessPayloadFunction;

public interface PollWithAck {
    void poll(ProcessPayloadFunction processPayload) throws NoPayloadForConsumptionException;
}
