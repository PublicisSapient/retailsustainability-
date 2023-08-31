package com.publicis.sapient.p2p.entity;

public enum OfferType {

    GIVEAWAY(0), SELL(1), DONATION(2);

    private final int value;

    OfferType(int i) {
        value = i;
    }

    public int getValue() {
        return value;
    }

}