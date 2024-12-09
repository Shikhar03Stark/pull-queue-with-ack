package com.shikhar03stark.mbroker.model.option.functional;

@FunctionalInterface
public interface PayloadOptions<T> {
    void apply(T options);
}
