package com.cutiadb.resp;

// @ Project
import com.cutiadb.resp.models.Entry;
import com.cutiadb.resp.utils.Constants;

// @ Java
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static com.cutiadb.resp.utils.Constants.CRLF_BYTES;

// This is an implementation of Redis Serialization Protocol (RESP)
// https://redis.io/docs/latest/develop/reference/protocol-spec

record ReadIntResponse(int number, int newIndex) { };

record DeserializeResponse(int startIndex, int endIndex, Entry entry) { }

// @ TODO Unittests

public class Resp {
    private static ReadIntResponse readInt(byte[] data, int index) {
        int n = 0;

        while (data[index] != '\r') {
            n = n * 10 + (data[index++] - '0');
        }

        index += Constants.CRLF_LENGTH;

        return new ReadIntResponse(n, index);
    };

    public static byte[] serialize(Entry data) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        // We add prefix
        output.write(data.type.getPrefix());

        switch(data.type) {
            case SIMPLE_ERROR, SIMPLE_STRING:
                byte[] message = data.message.getBytes(StandardCharsets.UTF_8);

                output.write(message, 0, message.length);
                output.writeBytes(CRLF_BYTES);
                break;
            case INTEGER:
                output.writeBytes(Long.toString(data.integer).getBytes(StandardCharsets.US_ASCII));
                output.writeBytes(CRLF_BYTES);
                break;
            case BULK_STRING:
                byte[] bulkString = data.message.getBytes(StandardCharsets.UTF_8);

                output.writeBytes(Long.toString(bulkString.length).getBytes(StandardCharsets.US_ASCII));
                output.writeBytes(CRLF_BYTES);
                output.write(bulkString, 0, bulkString.length);
                output.writeBytes(CRLF_BYTES);
                break;
            case ARRAY:
                // Array size
                output.writeBytes(Long.toString(data.elements.size()).getBytes(StandardCharsets.US_ASCII));
                output.writeBytes(CRLF_BYTES);

                for(int i = 0; i < data.elements.size(); i++) {
                    output.writeBytes(
                            serialize(data.elements.get(i))
                    );
                }
                break;
        }

        return output.toByteArray();
    };

    public static Entry deserialize(byte[] data) {
        return deserialize(data, 0).entry();
    }

    private static DeserializeResponse deserialize(byte[] data, int index) {
        // Support more types as needed.
        return switch (data[index++]) {
            case '*' ->  deserealizeArrayType(data, index);
            case '+' -> deserealizeSimpleStringType(data, index, false);
            case '-' -> deserealizeSimpleStringType(data, index, true);
            case '$' -> deserealizeBulkStringType(data, index);
            case ':' -> deserealizeIntegerType(data, index);
            default ->  new DeserializeResponse(index - 1, index, Entry.error("Unknown data type."));
        };
    };

    private static DeserializeResponse deserealizeIntegerType(byte[] data, int index) {
        int startIndex = index - 1;
        boolean negative = false;

        // RESP allows an optional sign: :[<+|->]<value>\r\n
        if (data[index] == '-') {
            negative = true;
            index++;
        } else if (data[index] == '+') {
            index++;
        }

        ReadIntResponse readIntResponse = readInt(data, index);

        int number = readIntResponse.number();

        Entry entry = Entry.integer(negative ? -number : number);

        return new DeserializeResponse(startIndex, readIntResponse.newIndex(), entry);
    };

    private static DeserializeResponse deserealizeSimpleStringType(byte[] data, int index, boolean error) {
        int toIndex = index;

        while (data[toIndex] != '\r') {
            toIndex += 1;
        };

        String message = new String(data, index, toIndex - index, StandardCharsets.UTF_8);

        Entry entry;

        if (error) {
            entry = Entry.simpleString(message);
        } else {
            entry = Entry.error(message);
        }
        return new DeserializeResponse(index - 1, toIndex + Constants.CRLF_LENGTH, entry);
    };

    private static DeserializeResponse deserealizeBulkStringType(byte[] data, int index) {
        int startIndex = index - 1;
        ReadIntResponse readStringSize = readInt(data, index);

        index = readStringSize.newIndex();
        int stringSize = readStringSize.number();

        String string = new String(data, index, stringSize, StandardCharsets.UTF_8);

        Entry entry = Entry.bulkString(string);

        // contenido + CRLF final
        return new DeserializeResponse(startIndex, index + stringSize + Constants.CRLF_LENGTH, entry);
    }

    private static DeserializeResponse deserealizeArrayType(byte[] data, int index) {
        int fromIndex = index - 1;
        ReadIntResponse readElementsCount = readInt(data, index);

        index = readElementsCount.newIndex();
        int numberOfElements = readElementsCount.number();

        var elements = new ArrayList<Entry>();

        for (int i = 0; i < numberOfElements; i++) {
            DeserializeResponse element = deserialize(data, index);
            elements.add(element.entry());

            index = element.endIndex();
        }

        Entry entry = Entry.array(elements);

        return new DeserializeResponse(fromIndex, index, entry);
    };
}