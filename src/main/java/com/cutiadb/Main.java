package com.cutiadb;

import com.cutiadb.server.Config;
import com.cutiadb.server.Server;

public class Main {
    static void main() {
        Config svConfig = new Config();
        Server sv = new Server(svConfig);

        sv.start();
    }
}
