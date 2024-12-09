package com.shikhar03stark;

import com.shikhar03stark.mbroker.exception.NoPayloadForConsumptionException;
import com.shikhar03stark.mbroker.model.Payload;
import com.shikhar03stark.mbroker.queue.MessageQueue;
import com.shikhar03stark.mbroker.queue.impl.InMemoryMessageQueueImpl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static List<Payload> getSequentialPayloads(int upto) {
        final List<Payload> payloads = new ArrayList<>();
        for(int i = 0; i<upto; i++) {
            payloads.add(Payload.New()
                    .withSubject(String.valueOf(i+1)));
        }
        return payloads;
    }

    private static MessageQueue mQ = new InMemoryMessageQueueImpl();
    private static List<Payload> payloads = getSequentialPayloads(20);

    private static void publishToQueue(List<Payload> payloads, int start, int jump) {
        for(int i = start; i<payloads.size(); i += jump) {
            mQ.publish(payloads.get(i));
        }
    }

    private static void publishEvenToQueue() {
        publishToQueue(payloads, 0, 2);
    }


    private static void publishOddToQueue() {
        publishToQueue(payloads, 1, 2);
    }

    private static void consumeFromQueue() {
        try {
            while (true) {
                final Payload payload = mQ.poll();
                System.out.println("[Consumed] "+payload.getSubject()+" "+Thread.currentThread().threadId());
                Thread.sleep(Duration.ofSeconds(1));
            }
        } catch (NoPayloadForConsumptionException e) {
            System.out.println("All messages consumed");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        ExecutorService evenPublish = Executors.newFixedThreadPool(1), oddPublish = Executors.newFixedThreadPool(1);
        evenPublish.submit(Main::publishEvenToQueue);
        oddPublish.submit(Main::publishOddToQueue);

        ExecutorService consumers = Executors.newFixedThreadPool(5);
        consumers.submit(Main::consumeFromQueue);
        consumers.submit(Main::consumeFromQueue);
        consumers.submit(Main::consumeFromQueue);
        consumers.submit(Main::consumeFromQueue);
        consumers.submit(Main::consumeFromQueue);
        consumers.submit(Main::consumeFromQueue);


        evenPublish.close();
        oddPublish.close();
        consumers.close();

    }


}