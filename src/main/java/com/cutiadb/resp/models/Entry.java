package com.cutiadb.resp.models;

// @ Project
import com.cutiadb.resp.utils.DataType;

// @ Java
import java.util.List;

public class Entry {
    public final DataType type;
    public final String message;          // SIMPLE_STRING, SIMPLE_ERROR, BULK_STRING
    public final long integer;            // INTEGER
    public final List<Entry> elements;    // ARRAY

    private Entry(DataType type, String message, long integer, List<Entry> elements) {
        this.type = type;
        this.message = message;
        this.integer = integer;
        this.elements = elements;
    }

    public static Entry simpleString(String s) {
        return new Entry(DataType.SIMPLE_STRING, s, 0, List.of());
    }

    public static Entry error(String error) {
        return new Entry(DataType.SIMPLE_ERROR, error, 0, List.of());
    }

    public static Entry bulkString(String s) {
        return new Entry(DataType.BULK_STRING, s, 0, List.of());
    }

    public static Entry integer(long integer) {
        return new Entry(DataType.INTEGER, "", integer, List.of());
    }

    public static Entry array(List<Entry> elements) {
        return new Entry(DataType.ARRAY, "", 0, List.copyOf(elements));
    }
}