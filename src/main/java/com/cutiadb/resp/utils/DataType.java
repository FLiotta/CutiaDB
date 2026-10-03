package com.cutiadb.resp.utils;

public enum DataType {
    SIMPLE_STRING('+'),
    SIMPLE_ERROR('-'),
    INTEGER(':'),
    BULK_STRING('$'),
    ARRAY('*');

    private final byte prefix;

    DataType(char prefix) {
        this.prefix = (byte) prefix;
    }

    public static DataType fromByte(byte b) {
        for (DataType t : values()) if (t.prefix == b) return t;
        throw new IllegalArgumentException("Invalid RESP DataType: " + (char) b);
    }

    public byte getPrefix() {
        return this.prefix;
    }
}


