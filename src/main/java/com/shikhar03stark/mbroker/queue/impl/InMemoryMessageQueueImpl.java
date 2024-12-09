package com.shikhar03stark.mbroker.queue.impl;

import com.shikhar03stark.mbroker.exception.NoPayloadForConsumptionException;
import com.shikhar03stark.mbroker.model.Payload;
import com.shikhar03stark.mbroker.queue.MessageQueue;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class InMemoryMessageQueueImpl implements MessageQueue {

    private final Queue<Payload> exposedPayloadQueue;
    private final Semaphore pollSemphore;

    public InMemoryMessageQueueImpl() {
        this.exposedPayloadQueue = new ConcurrentLinkedQueue<>();
        this.pollSemphore = new Semaphore(1);
    }

    private boolean isTTLExpired(Payload payload) {
        if (!payload.getPayloadTTLOptions().isHasTTL()) return false;
        return payload.getPayloadTTLOptions().getTtlEndTime().isBefore(LocalDateTime.now());
    }

    @Override
    public void publish(Payload payload) {
        if (isTTLExpired(payload)) {
            System.out.println("[MessageQueue] TTL of payload already expired before publish");
            return;
        }

        exposedPayloadQueue.add(payload);
    }

    @Override
    public Payload poll() throws NoPayloadForConsumptionException {
        try {
            pollSemphore.acquire();
            Payload payload = null;
            do {
                payload = exposedPayloadQueue.poll();
                if (Objects.isNull(payload)) {
                    throw new NoPayloadForConsumptionException("");
                }
            } while (isTTLExpired(payload));
            return payload;

        } catch (InterruptedException iex) {
            throw new RuntimeException(iex);
        }
        finally {
            pollSemphore.release();
        }
    }
}
