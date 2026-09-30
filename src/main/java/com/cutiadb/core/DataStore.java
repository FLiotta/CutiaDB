package com.cutiadb.core;

// @ Project
import com.cutiadb.core.models.Entry;

// @ Java
import java.time.Instant;
import java.util.HashMap;

public class DataStore {
    private final HashMap<String, Entry> store = new HashMap<>();
    public int entriesCounter;

    private final Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public DataStore() {}

    public void set(String key, Object value) {
        if (!this.store.containsKey(key)) {
            this.entriesCounter += 1;
        }

        Entry newEntry = new Entry(value);
        this.store.put(key, newEntry);
        this.updatedAt = Instant.now();
    }

    public int delete(String key) {
        if (!store.containsKey(key)) {
            return 0;
        }

        this.store.remove(key);
        this.entriesCounter -= 1;
        this.updatedAt = Instant.now();
        return 1;
    }

    public Entry get(String key) {
        return this.store.get(key);
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    @Override
    public String toString() {
        return String.format(
                "<DataStore entities=%d />", this.entriesCounter
        );
    }
}
