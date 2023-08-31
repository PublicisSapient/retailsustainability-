package com.publicis.sapient.p2p.entity;

public enum Category {

    CLOTH(0), ELECTRONIC(1), BOOK(2), TOY(3), MOBILE(4), FURNITURE(5), VEHICLE(6);

    private final int value;

    Category(int i) {
        value = i;
    }

    public int getValue() {
        return value;
    }

}