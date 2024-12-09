package com.shikhar03stark.mbroker.exception;

public class NoPayloadForConsumptionException extends Exception {
    private static final String EXCEPTION_NAME = NoPayloadForConsumptionException.class.getCanonicalName();
    public NoPayloadForConsumptionException(String message) {
        super(String.format("[%s]::%s", EXCEPTION_NAME, message));
    }
}
