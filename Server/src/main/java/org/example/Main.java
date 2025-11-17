package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // Port du serveur
        int port = 12345;
        Server server = new Server(port);
        try {
            server.startListenForNewClient();
        } catch (IOException e) {
            System.out.println("erreur");
        }
    }
}