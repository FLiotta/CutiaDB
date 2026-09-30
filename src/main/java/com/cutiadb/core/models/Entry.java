package com.cutiadb.core.models;

// @ Java
import java.time.Instant;

public class Entry {
    public Object value;
    public Instant expiresAt;
    public Instant createdAt;
    public Instant updatedAt;

    public Entry(Object value, Integer ttl) {
        this.value = value;

        if (ttl != null) {
            this.expiresAt = Instant.now().plusSeconds(ttl);
        }

        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public Entry(Object value) {
        this(value, null);
    }

    public Class<?> getValueType() {
        return this.value.getClass();
    }

    @Override
    public String toString() {
        return String.format(
            "<KeyValue=%s createdAt=%s updatedAt=%s expiresAt=%s />",
            this.value,
            this.expiresAt,
            this.createdAt,
            this.updatedAt
        );
    }
}
