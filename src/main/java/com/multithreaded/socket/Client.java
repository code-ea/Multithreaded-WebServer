package com.multithreaded.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {
    public void run(String clientName, String message, String page) throws IOException {
        try (Socket socket = new Socket(InetAddress.getByName("localhost"), Server.DEFAULT_PORT);
             OutputStream out = socket.getOutputStream();
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

            String request = "GET " + page + " HTTP/1.1\r\n" +
                             "Host: localhost\r\n" +
                             "X-Client-Name: " + clientName + "\r\n" +
                             "X-Client-Message: " + message + "\r\n" +
                             "Connection: close\r\n\r\n";

            out.write(request.getBytes(StandardCharsets.UTF_8));
            out.flush();

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append(System.lineSeparator());
            }

            System.out.println("Client " + clientName + " sent: " + message);
            System.out.println("HTTP response:\n" + response);
        }
    }

    public static void main(String[] args) {
        String clientName = args.length > 0 ? args[0] : "Client-1";
        String message = args.length > 1 ? args[1] : "Hello from default client";
        String page = args.length > 2 ? args[2] : "/";

        Client client = new Client();
        try {
            client.run(clientName, message, page);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
