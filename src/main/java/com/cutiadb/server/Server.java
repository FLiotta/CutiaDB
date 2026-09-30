package com.cutiadb.server;

// @ Project
import com.cutiadb.core.DataStore;
import com.cutiadb.core.models.Entry;
import com.cutiadb.server.exceptions.InvalidCommandException;
import com.cutiadb.server.exceptions.InvalidParametersException;
import com.cutiadb.server.models.ClientHandler;
import com.cutiadb.server.types.Commands;

// @ Java
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Server {
    public final Config config;
    public final DataStore ds = new DataStore();

    private boolean running;
    private ServerSocket serverSocket;
    private int connections = 0;

    public Server (Config config) {
        this.config = config;
        this.running = true;
    }

    public void start() {
        try {
            this.serverSocket = new ServerSocket(this.config.PORT);

            while (this.running) {
                if (this.connections >= this.config.MAX_CONNECTIONS) {
                    continue;
                }

                Socket client = this.serverSocket.accept();
                System.out.printf("[*] - New connection accepted %s\n", client.getRemoteSocketAddress());
                this.connections += 1;

                ClientHandler clientHandler = new ClientHandler(this, client);
                Thread clientThread = new Thread(clientHandler);

                clientThread.start();
            }
        } catch(IOException e) {
            System.out.println(e);
        };
    }

    public void shutdown() {
        this.running = false;

        try {
            this.serverSocket.close();
        } catch (IOException err) {
            System.out.printf("[/<] - Failed to shutdown server\n");
        }
    }

    public String processCommand(String command) throws InvalidCommandException, InvalidParametersException {
        String[] commandFragments = command.trim().split("\\s+");

        Commands cmd;
        String[] params = Arrays.copyOfRange(commandFragments, 1, commandFragments.length);

        try {
            cmd = Commands.valueOf(commandFragments[0]);
        } catch (IllegalArgumentException err) {
            throw new InvalidCommandException(String.format("'%s' is not a known command", commandFragments[0]));
        }

        switch (cmd) {
            case SET:
                if (params.length != 2) {
                    throw new InvalidParametersException("Invalid parameters length");
                }

                this.ds.set(params[0], params[1]);

                return "[OK]";
            case GET:
                if (params.length != 1) {
                    throw new InvalidParametersException("Invalid parameters length");
                }

                Entry found = this.ds.get(params[0]);

                return found.value.toString();
            case DEL:
                if (params.length != 1) {
                    throw new InvalidParametersException("Invalid parameters length");
                }

                this.ds.delete(params[0]);

                return "[OK]";
            default:
                // Unhandled command
                return "[?]";
        }
    }

    public void onClientClose(Socket client) {
        this.connections -= 1;
        System.out.printf("[*] - Client connection closed %s\n", client.getRemoteSocketAddress());
    }

    @Override
    public String toString() {
        return String.format(
                "<Server port=%d entries=%d />",
                this.config.PORT,
                this.ds.entriesCounter
        );
    }
}
