package com.cutiadb.server.models;

// @ Project
import com.cutiadb.server.Server;
import com.cutiadb.server.exceptions.InvalidCommandException;
import com.cutiadb.server.exceptions.InvalidParametersException;

// @ Java
import java.io.*;
import java.net.Socket;


public class ClientHandler implements Runnable {
    private final Server server;
    private final Socket client;

    public ClientHandler(Server server, Socket client) {
        this.server = server;
        this.client = client;
    }

    private String welcome() {
        return (
            " dP\"\"b8 88   88 888888 88    db        8888b.  88\"\"Yb\n" +
            "dP   `\" 88   88   88   88   dPYb        8I  Yb 88__dP\n" +
            "Yb      Y8   8P   88   88  dP__Yb       8I  dY 88\"\"Yb\n" +
            " YboodP `YbodP'   88   88 dP\"\"\"\"Yb     8888Y\"  88oodP\n"
        );
    }

    @Override
    public void run() {
        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(this.client.getOutputStream()), true);

            writer.print("\u001B[2J\u001B[H");
            writer.flush();
            writer.println(this.welcome());
            writer.println("Version 0.0.0.0.0.0.1");
            writer.println("======================\n\n");
            String command = reader.readLine();

            while (!command.equals("EXIT")) {
                if (command.isEmpty()) {
                    continue;
                }

                try {
                    String response = this.server.processCommand(command);
                    writer.println(response);
                } catch (InvalidCommandException e) {
                    writer.println("<INVALID COMMAND\n");
                } catch (InvalidParametersException e) {
                    writer.println("<INVALID PARAMETERS\n");
                }

                command = reader.readLine();
            }

            this.client.close();
            this.server.onClientClose(this.client);
        } catch (IOException e) {
            System.out.println("Error on client");
        }
    }
}
