package org.example;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Server {
    private final int port;
    private final List<ClientHandler> clientsList = new ArrayList<>();
    private ServerSocket severSocket;
    private boolean isRunning = false;
    private final List<String> history = new ArrayList<>();
    private int count = 0;

    public Server(int port) {
        this.port = port;
    }

    public void startListenForNewClient() throws IOException {
        try {
            severSocket = new ServerSocket();
            String hostname = "0.0.0.0";
            severSocket.bind(new InetSocketAddress(hostname, port));
            isRunning = true;
            System.out.println("Chat server started on port " + port);

            while (isRunning) {
                try {
                    Socket clientHandlerSocket = severSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(clientHandlerSocket, this);
                    clientsList.add(clientHandler);
                    Thread thread = new Thread(clientHandler);
                    thread.start();
                } catch (IOException e) {
                    System.out.println("Error accepting client: " + e.getMessage());
                }
            }
        }catch (IOException e) {
            System.out.println("Server failed to start: " + e.getMessage());
        }
    }

//    public void stop() throws IOException {
//        isRunning = false;
//        if (severSocket != null && !severSocket.isClosed()) {
//            severSocket.close();
//        }
//    }

    // méthode pour envoyer message à tout le monde
    public void broadcastMessage(ClientHandler sender, String msg) {
        if (msg == null || msg.isBlank()) return;

        history.add(msg);
        if (history.size() > 100) {
            history.remove(0);
        }

        List<ClientHandler> disconnectedClients = new ArrayList<>();

        for (ClientHandler clientHandler : clientsList) {
            if (clientHandler != sender && clientHandler.userName != null) {
                try {
                    clientHandler.writer.println(msg);
                } catch (Exception e) {
                    System.out.println("Failed to send message to " + clientHandler.userName);
                    disconnectedClients.add(clientHandler);
                }
            }
        }

        clientsList.removeAll(disconnectedClients);
    }

    // envoi historique
    public void sendHistoryToClient(ClientHandler client) {
        for (int i = 0; i < history.size(); i++) {
            client.writer.println(history.get(i));
        }
    }

    // Classe interne pour gérer chaque client
    class ClientHandler implements Runnable {
        Socket socket;
        PrintWriter writer;
        String userName;
        private int clientId;

        public ClientHandler(Socket socket, Server srv) {
            this.socket = socket;
            this.clientId = count++;
        }

        @Override
        public void run() {
            try {
                InputStream inputStream = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                OutputStream outStream = socket.getOutputStream();
                writer = new PrintWriter(new OutputStreamWriter(outStream), true);

                String msgPrint = InputAndDisplayClientJoin(reader);

                sendHistoryToClient(this);
                broadcastMessage(this, msgPrint);

                String messageReceived;
                while ((messageReceived = reader.readLine()) != null) {
                    msgPrint = userName + ": " + messageReceived;
                    System.out.println(msgPrint);
                    broadcastMessage(this, msgPrint);
                }

                UserLeft();

            } catch (IOException e) {
                System.out.println(userName + " has been disconnected");
            }
        }

        private String InputAndDisplayClientJoin(BufferedReader reader) throws IOException {
            writer.println("Enter your name: ");
            userName = reader.readLine();
            String msgPrint = userName + " has joined the chat.";
            System.out.println(msgPrint);
            return msgPrint;
        }

        private void UserLeft() {
            String msgLeave = userName + " has left the chat.";
            System.out.println(msgLeave);
            broadcastMessage(this, msgLeave);
        }
    }
}