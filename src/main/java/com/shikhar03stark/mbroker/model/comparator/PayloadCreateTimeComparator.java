package com.shikhar03stark.mbroker.model.comparator;

import com.shikhar03stark.mbroker.model.Payload;

import java.util.Comparator;

public class PayloadCreateTimeComparator implements Comparator<Payload> {
    @Override
    public int compare(Payload a, Payload b) {
        return a.getCreatedAt().compareTo(b.getCreatedAt());
    }
}
