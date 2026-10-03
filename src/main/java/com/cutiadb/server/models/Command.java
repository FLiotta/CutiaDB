package com.cutiadb.server.models;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Command {
    public final String command;
    public final ArrayList<String> params;
    public final String type;

    public Command(String command, ArrayList<String> params, String type) {
        this.command = command;
        this.params = params;
        this.type = type;
    }
}
