package com.shikhar03stark.mbroker.common;

import com.shikhar03stark.mbroker.model.Payload;

import java.time.Duration;
import java.util.List;
import java.util.stream.IntStream;

public class PayloadUtils {

    public static Payload payloadWithSubject(String subject) {
        return Payload
                .New()
                .withSubject(subject);
    }

    public static Payload payloadWithSubjectAndRetry(String subject, int maxRetry) {
        return Payload
                .New()
                .withSubject(subject)
                .withPayloadRetryOptions(opt -> {
                    opt.withMaxRetries(maxRetry);
                });
    }

    public static Payload payloadWithSubjectAndTTL(String subject, Duration fromNow) {
        return Payload
                .New()
                .withSubject(subject)
                .withPayloadTTLOptions(opt -> {
                    opt.withDurationFromNow(fromNow);
                });
    }

    public static List<Payload> payloadWithIncreasingSubjectList(int count) {
        return IntStream
                .range(0, count)
                .mapToObj(idx -> payloadWithSubject(String.valueOf(idx+1)))
                .toList();
    }

    public static List<Payload> payloadWithIncreasingSubjectAndRetryList(int count, int maxRetry) {
        return IntStream
                .range(0, count)
                .mapToObj(idx -> payloadWithSubjectAndRetry(String.valueOf(idx+1), maxRetry))
                .toList();
    }
}
