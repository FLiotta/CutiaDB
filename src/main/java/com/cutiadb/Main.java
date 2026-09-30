package com.cutiadb;

import com.cutiadb.core.DataStore;
import com.cutiadb.core.models.Entry;
import com.cutiadb.server.Config;
import com.cutiadb.server.Server;

public class Main {
    static void main() {
        Config svConfig = new Config();
        Server sv = new Server(svConfig);

        sv.start();
    }
}
