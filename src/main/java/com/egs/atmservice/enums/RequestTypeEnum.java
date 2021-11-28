package com.egs.atmservice.enums;

public enum RequestTypeEnum {
    CHECK_BALANCE(1),
    DEPOSIT(2),
    WITHDRAW(3),
    GET_RECEIPT(4);;

    private int value;

    RequestTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
