package org.example;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Client {
    private String host;
    private int port;
    private Socket socket;
    private ExecutorService exec;
    private BufferedReader consoleReader;
    private int messageCount = 0;

    public Client(String serverAddress, int serverPort) {
        this.host = serverAddress;
        this.port = serverPort;
    }

    // méthode pour se connecter
    public void Connect() throws IOException, InterruptedException, ExecutionException {
        socket = new Socket(host, port);
        exec = Executors.newFixedThreadPool(2);

        Future<?> thread1 = exec.submit(this::receiveMessages);
        Thread.sleep(100);
        Future<?> thread2 = exec.submit(this::sendMessages);

        thread1.get();
        thread2.get();

        shutdown();
    }

    // reception des messages
    private void receiveMessages() {
        try {
            InputStream inputStream = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader r = new BufferedReader(isr);
            String msg;
            while ((msg = r.readLine()) != null) {
                System.out.println("\r" + msg);
                System.out.print("You: ");
            }
        } catch (IOException e) {
            System.out.println("Disconnected");
        }
        // TODO: fermer le reader
    }

    // envoi messages
    private void sendMessages() {
        try {
            OutputStream outputStream = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(outputStream);
            BufferedWriter w = new BufferedWriter(osw);
            consoleReader = new BufferedReader(new InputStreamReader(System.in));
            String input;
            while ((input = consoleReader.readLine()) != null) {
                w.write(input);
                w.newLine();
                w.flush();
                System.out.print("You: ");
                messageCount = messageCount + 1;
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
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