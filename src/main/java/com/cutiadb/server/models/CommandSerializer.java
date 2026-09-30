package com.cutiadb.server.models;

import java.lang.reflect.Array;

class Command {
    private final String command;
    private final Array[] params;

    public Command(String command, Array[] params) {
        this.command = command;
        this.params = params;
    }
}

public class CommandSerializer {
    public static String serialize(Command deserializedCommand) {
        return "";
    };

    public static Command deserialize(String serializedCommand) {
        return null;
    }
}
