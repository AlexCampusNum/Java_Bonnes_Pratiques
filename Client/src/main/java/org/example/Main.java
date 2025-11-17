package org.example;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

// Main du client
public class Main {
    public static void main(String[] args) {
        String host = "localhost";
        int _Port = 12345;
        Client c = new Client(host, _Port);
        try {
            c.Connect();
        } catch (IOException | InterruptedException | ExecutionException e) {
            System.out.println("Failed");
        }
    }
}