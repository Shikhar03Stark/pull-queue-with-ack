package com.shikhar03stark.mbroker.common;

import com.shikhar03stark.mbroker.model.Payload;
import com.shikhar03stark.mbroker.queue.AckMessageQueue;

import java.util.ArrayList;
import java.util.List;

public class ProducerUtils {

    public static Runnable publishPayload(AckMessageQueue queue, Payload payload) {
        return () -> {
            queue.publish(payload);
        };
    }

    public static List<Runnable> publishPayloadForEach(AckMessageQueue queue, List<Payload> payloads) {
        final List<Runnable> runnables = new ArrayList<>();
        for(Payload payload: payloads) {
            runnables.add(publishPayload(queue, payload));
        }
        return runnables;
    }
}
