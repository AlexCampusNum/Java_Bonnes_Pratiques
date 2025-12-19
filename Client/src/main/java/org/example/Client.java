package org.example;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Client {
    private final String host;
    private final int port;
    private Socket socket;
    private ExecutorService exec;
    private int messageCount = 0;

    public Client(String serverAddress, int serverPort) {
        this.host = serverAddress;
        this.port = serverPort;
    }

    // méthode pour se connecter
    public void Connect() throws IOException, InterruptedException, ExecutionException {
        socket = new Socket(host, port);
        socket.setSoTimeout(30000);
        exec = Executors.newFixedThreadPool(2);

        // Lancer les threads
        exec.submit(this::receiveMessages);
        exec.submit(this::sendMessages);

        // Shutdown automatique si la JVM s'arrête
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                shutdown();
            } catch (IOException ignored) {
            }
        }));
    }


    // reception des messages
    private void receiveMessages() {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String msg;
            while ((msg = bufferedReader.readLine()) != null) {
                System.out.println("\r" + msg);
                System.out.print("You: ");
            }
        } catch (IOException e) {
            if (!socket.isClosed()) {
                System.out.println("Disconnected from server");
            }
        }
    }


    // envoi messages
    private void sendMessages() {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {

            String input;
            while ((input = consoleReader.readLine()) != null) {
                writer.write(input);
                writer.newLine();
                writer.flush();
                System.out.print("You: ");
                messageCount++;
            }
        } catch (IOException e) {
            System.out.println("Error sending message : " + e.getMessage());
        }
    }

    // arrêt propre
    private void shutdown() throws IOException {
        if (exec != null) {
            exec.shutdown();
        }
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    private String formatMessage(String msg) {
        return msg.trim();
    }
}