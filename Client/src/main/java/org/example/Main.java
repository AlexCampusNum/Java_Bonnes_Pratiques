package org.example;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

// Main du client
public class Main {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 12345;
        Client client = new Client(host, port);
        try {
            client.Connect();
        } catch (IOException | InterruptedException | ExecutionException e) {
            System.err.println("Failed, client connection error");
            System.exit(1);
        }
    }
}