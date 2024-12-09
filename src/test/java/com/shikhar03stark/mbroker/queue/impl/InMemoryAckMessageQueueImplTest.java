package com.shikhar03stark.mbroker.queue.impl;

import com.shikhar03stark.mbroker.common.ConsumerUtils;
import com.shikhar03stark.mbroker.common.PayloadUtils;
import com.shikhar03stark.mbroker.common.ProducerUtils;
import com.shikhar03stark.mbroker.exception.NoPayloadForConsumptionException;
import com.shikhar03stark.mbroker.model.Payload;
import com.shikhar03stark.mbroker.queue.AckMessageQueue;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryAckMessageQueueImplTest {

    @Test
    public void ProducerConsumerTest() {
        AckMessageQueue mq = new InMemoryAckMessageQueueImpl();
        int consumerCount = 3;
        int badConsumerCount = 2;
        int maxRetry = 3;
        int payloads = 20;
        final List<Runnable> producers = ProducerUtils.publishPayloadForEach(mq, PayloadUtils.payloadWithIncreasingSubjectAndRetryList(payloads, maxRetry));
        Consumer<AckMessageQueue> consumeWithAck = (queue) -> {
            int noPayloadCount = 0;
            while (noPayloadCount < maxRetry+1) {
              try {
                  queue.poll(((payload, ack) -> {
                      System.out.println("[Consumer] " + Thread.currentThread().threadId() + " consumed payload with subject: " + payload.getSubject());
                      try {
                          int waitSeconds = new Random().nextInt(1, 5);
                          System.out.println("[Consumer] "+Thread.currentThread().threadId()+" is processing for "+waitSeconds+"s");
                          Thread.sleep(Duration.ofSeconds(waitSeconds));
                      } catch (InterruptedException e) {
                          throw new RuntimeException(e);
                      }
                      System.out.println("[Consumer] " + Thread.currentThread().threadId() + " doing ack");
                      ack.ack();
                  }));
              }
              catch (NoPayloadForConsumptionException ex) {
                  System.out.println("[Consumer] [ERROR] "+Thread.currentThread().threadId()+ " found no payloads");
                  noPayloadCount++;
                  try {
                      Thread.sleep(Duration.ofSeconds(1));
                  } catch (InterruptedException e) {
                      throw new RuntimeException(e);
                  }
              }
            }
        };
        Consumer<AckMessageQueue> consumeWithoutAck = (queue) -> {
            try {
                while (true) {
                    queue.poll(((payload, ack) -> {
                        System.out.println("[BadConsumer] " + Thread.currentThread().threadId() + " consumed payload with subject: " + payload.getSubject());
                        try {
                            int waitSeconds = 1;
                            System.out.println("[BadConsumer] "+Thread.currentThread().threadId()+" is processing for "+waitSeconds+"s");
                            Thread.sleep(Duration.ofSeconds(waitSeconds));
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("[BadConsumer] " + Thread.currentThread().threadId() + " not acking");
                    }));
                }
            }
            catch (NoPayloadForConsumptionException ex) {
                System.out.println("[BadConsumer] [ERROR] "+Thread.currentThread().threadId()+ " found no payloads");
                try {
                    Thread.sleep(Duration.ofSeconds(2));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        List<Runnable> consumers = ConsumerUtils.independentConsumers(mq, consumeWithAck, consumerCount);
        List<Runnable> badConsumers = ConsumerUtils.independentConsumers(mq, consumeWithoutAck, badConsumerCount);
        try(final ExecutorService executorService = Executors.newFixedThreadPool(16)){
            for (Runnable runnable: producers){
                executorService.submit(runnable);
            }
            for (Runnable runnable: badConsumers) {
                executorService.submit(runnable);
            }
            for (Runnable runnable: consumers) {
                executorService.submit(runnable);
            }
        }

    }

}