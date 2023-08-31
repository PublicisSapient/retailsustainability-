package com.publicis.sapient.p2p.entity;

public enum DropLocationType {

    PUBLICIS_SAPIENT_OFFICE(0), PARTNERED_NGOS(1);

    private final int value;

    DropLocationType(int i) {
        value = i;
    }

    public int getValue() {
        return value;
    }

}
