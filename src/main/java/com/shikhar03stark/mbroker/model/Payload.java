package com.shikhar03stark.mbroker.model;

import com.shikhar03stark.mbroker.model.option.PayloadRetryOptions;
import com.shikhar03stark.mbroker.model.option.PayloadTTLOptions;
import com.shikhar03stark.mbroker.model.option.functional.PayloadOptions;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

public class Payload {
    private String id;
    private String subject;
    private String body;
    private LocalDateTime createdAt;
    private String sourceHost;

    // Options
    private PayloadTTLOptions payloadTTLOptions;
    private PayloadRetryOptions payloadRetryOptions;


    private Payload() {}

    private static void setDefaultIfNull(Payload payload) {
        payload.id = Objects.isNull(payload.id) ? String.valueOf(new Random().nextInt()) : payload.id;
        payload.subject = Objects.isNull(payload.subject) ? "" : payload.subject;
        payload.body = Objects.isNull(payload.body) ? "" : payload.body;
        payload.createdAt = Objects.isNull(payload.createdAt) ? LocalDateTime.now() : payload.createdAt;
        payload.sourceHost = Objects.isNull(payload.sourceHost) ? String.format("localhost-%d", Thread.currentThread().threadId()) : payload.sourceHost;
        payload.payloadTTLOptions = Objects.isNull(payload.payloadTTLOptions) ? new PayloadTTLOptions() : payload.payloadTTLOptions;
        payload.payloadRetryOptions = Objects.isNull(payload.payloadRetryOptions) ? new PayloadRetryOptions() : payload.payloadRetryOptions;
    }

    public static Payload New() {
        final Payload payload = new Payload();
        // set defaults
        setDefaultIfNull(payload);
        return payload;
    }

    public Payload withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public Payload withBody(String body) {
        this.body = body;
        return this;
    }

    public Payload withPayloadTTLOptions(PayloadOptions<PayloadTTLOptions> ttlOptions) {
        ttlOptions.apply(this.payloadTTLOptions);
        return this;
    }

    public Payload withPayloadRetryOptions(PayloadOptions<PayloadRetryOptions> retryOptions) {
        retryOptions.apply(payloadRetryOptions);
        return this;
    }

    // Getters
    public PayloadRetryOptions getPayloadRetryOptions() {
        return payloadRetryOptions;
    }

    public String getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getSourceHost() {
        return sourceHost;
    }

    public PayloadTTLOptions getPayloadTTLOptions() {
        return payloadTTLOptions;
    }
}
