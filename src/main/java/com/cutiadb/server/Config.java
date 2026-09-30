package com.cutiadb.server;

import java.util.Objects;

public class Config {
    public final int DEFAULT_PORT = 1816;
    public final int DEFAULT_MAX_CONNECTIONS = 100;

    public final int PORT;
    public final int MAX_CONNECTIONS;

    public Config(Integer port, Integer maxConnections) {
        this.PORT = Objects.requireNonNullElse(port, this.DEFAULT_PORT);
        this.MAX_CONNECTIONS = Objects.requireNonNullElse(maxConnections, this.DEFAULT_MAX_CONNECTIONS);
    }

    public Config(Integer port) {
        this(port, null);
    }

    public Config() {
        this(null, null);
    }

    @Override
    public String toString() {
        return String.format(
                "<Config port=%d maxConnections=%d />",
                this.PORT,
                this.MAX_CONNECTIONS
        );
    }
}
