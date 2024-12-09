package com.shikhar03stark.mbroker.model.option;

import java.time.Duration;
import java.time.LocalDateTime;

public class PayloadTTLOptions {
    private boolean hasTTL;
    private LocalDateTime ttlEndTime;

    public PayloadTTLOptions() {
        this.hasTTL = false;
        ttlEndTime = null;
    }

    public PayloadTTLOptions withDurationFromNow(Duration duration) {
        hasTTL = true;
        ttlEndTime = LocalDateTime.now().plus(duration);
        return this;
    }

    public PayloadTTLOptions withExpireOn(LocalDateTime expireOn) {
        hasTTL = true;
        ttlEndTime = expireOn;
        return this;
    }

    public boolean isHasTTL() {
        return hasTTL;
    }

    public LocalDateTime getTtlEndTime() {
        return ttlEndTime;
    }
}
