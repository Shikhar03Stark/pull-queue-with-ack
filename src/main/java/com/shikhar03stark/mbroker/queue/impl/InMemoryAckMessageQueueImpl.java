package com.shikhar03stark.mbroker.queue.impl;

import com.shikhar03stark.mbroker.exception.NoPayloadForConsumptionException;
import com.shikhar03stark.mbroker.model.Payload;
import com.shikhar03stark.mbroker.model.comparator.PayloadCreateTimeComparator;
import com.shikhar03stark.mbroker.queue.AckMessageQueue;
import com.shikhar03stark.mbroker.queue.functional.AckFunction;
import com.shikhar03stark.mbroker.queue.functional.ProcessPayloadFunction;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class InMemoryAckMessageQueueImpl implements AckMessageQueue {
    private final Queue<Payload> mainQueue;
    private final Set<Payload> inFlightPayloads;
    private final Semaphore pollSemaphore;

    public InMemoryAckMessageQueueImpl() {
        mainQueue = new ConcurrentLinkedQueue<>();
        inFlightPayloads = new ConcurrentSkipListSet<>(new PayloadCreateTimeComparator());
        pollSemaphore = new Semaphore(1);
    }

    private boolean isTTLExpired(Payload payload) {
        if (!payload.getPayloadTTLOptions().isHasTTL()) return false;
        return payload.getPayloadTTLOptions().getTtlEndTime().isBefore(LocalDateTime.now());
    }

    private void processPayloadHandler(Payload payload, ProcessPayloadFunction process) {
        try {
            inFlightPayloads.add(payload);
            AtomicBoolean isAck = new AtomicBoolean(false);
            AckFunction ackFn = () -> {
                isAck.set(true);
            };
            process.processPayload(payload, ackFn);

            if (!isAck.get()) {
                // move payload to mainQueue
                mainQueue.add(payload);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // delete from inFlight
            inFlightPayloads.remove(payload);
        }
    }

    @Override
    public void publish(Payload payload) {
        if(isTTLExpired(payload)) {
            System.out.println("[MessageQueue] TTL of payload already expired before publish");
            return;
        }
        mainQueue.add(payload);
    }

    @Override
    public Payload poll() throws NoPayloadForConsumptionException {
        try {
            pollSemaphore.acquire();
            Payload payload = null;
            do {
                payload = mainQueue.poll();
                if (Objects.isNull(payload)) {
                    throw new NoPayloadForConsumptionException("");
                }
            } while (isTTLExpired(payload) || !payload.getPayloadRetryOptions().canExecute());
            payload.getPayloadRetryOptions().increaseTry();
            return payload;
        } catch (InterruptedException iex) {
            throw new RuntimeException(iex);
        } finally {
            pollSemaphore.release();
        }
    }

    @Override
    public void poll(ProcessPayloadFunction processPayload) throws NoPayloadForConsumptionException {
        boolean isReleased = false;
        try{
            pollSemaphore.acquire();
            Payload payload = null;
            do {
                payload = mainQueue.poll();
                if (Objects.isNull(payload)){
                    // no elements to execute processPayload fn
                    throw new NoPayloadForConsumptionException("");
                }
            } while (isTTLExpired(payload) || !payload.getPayloadRetryOptions().canExecute());
            // release lock for mainQueue
            isReleased = true;
            pollSemaphore.release();

            // process payload
            payload.getPayloadRetryOptions().increaseTry();
            inFlightPayloads.add(payload);
            processPayloadHandler(payload, processPayload);

        } catch (InterruptedException iex) {
            throw new RuntimeException(iex);
        } finally {
            if (!isReleased) pollSemaphore.release();
        }
    }
}
