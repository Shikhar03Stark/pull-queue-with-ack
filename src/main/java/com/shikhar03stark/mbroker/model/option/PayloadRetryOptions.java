package com.shikhar03stark.mbroker.model.option;

public class PayloadRetryOptions {
    private int maxRetry;
    private int currentTry;

    public PayloadRetryOptions() {
        maxRetry = -1;
        currentTry = 0;
    }

    public void withMaxRetries(int num) {
        maxRetry = num;
    }

    public void increaseTry() {
        this.currentTry++;
    }

    public boolean canExecute() {
        if(maxRetry < 0) return true;
        return currentTry <= maxRetry;
    }
}
