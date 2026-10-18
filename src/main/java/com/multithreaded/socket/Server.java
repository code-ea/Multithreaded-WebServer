package com.multithreaded.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Server {
    public static final int DEFAULT_PORT = 8080;
    private static final String CRLF = "\r\n";

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT)) {
            System.out.println("Web server listening on port " + DEFAULT_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                Thread clientThread = new Thread(() -> handleRequest(clientSocket));
                clientThread.start();
            }
        }
    }

    private void handleRequest(Socket clientSocket) {
        String clientIp = clientSocket.getInetAddress().getHostAddress();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             OutputStream out = clientSocket.getOutputStream()) {

            String requestLine = in.readLine();
            if (requestLine == null) {
                return;
            }

            String clientInfo = readClientInfo(in, clientIp);
            System.out.println("Client: " + clientInfo + " | Request: " + requestLine);

            String[] requestParts = requestLine.split(" ");
            if (requestParts.length < 3) {
                sendResponse(out, 400, "Bad Request", "text/plain", "Invalid HTTP request.".getBytes(StandardCharsets.UTF_8));
                return;
            }

            String method = requestParts[0];
            String path = requestParts[1];

            if (!"GET".equalsIgnoreCase(method)) {
                sendResponse(out, 405, "Method Not Allowed", "text/plain", ("Method " + method + " is not supported.").getBytes(StandardCharsets.UTF_8));
                return;
            }

            Path webRoot = Path.of(System.getProperty("user.dir"), "web");
            Path filePath = resolveFile(webRoot, path);
            String body = Files.exists(filePath) ? Files.readString(filePath, StandardCharsets.UTF_8) : "";

            if (Files.exists(filePath)) {
                sendResponse(out, 200, "OK", "text/html", body.getBytes(StandardCharsets.UTF_8));
            } else {
                String notFound = "<html><body><h1>404 - Page Not Found</h1></body></html>";
                sendResponse(out, 404, "Not Found", "text/html", notFound.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException ex) {
            System.err.println("Error handling client " + clientIp + ": " + ex.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ignored) {
                // Best effort cleanup.
            }
        }
    }

    private String readClientInfo(BufferedReader in, String fallback) throws IOException {
        String clientName = fallback;
        String message = "No message received";
        String line;

        while ((line = in.readLine()) != null && !line.isEmpty()) {
            if (line.regionMatches(true, 0, "X-Client-Name:", 0, "X-Client-Name:".length())) {
                clientName = line.substring("X-Client-Name:".length()).trim();
            }
            if (line.regionMatches(true, 0, "X-Client-Message:", 0, "X-Client-Message:".length())) {
                message = line.substring("X-Client-Message:".length()).trim();
            }
        }

        return clientName + " -> " + message;
    }

    private Path resolveFile(Path webRoot, String requestPath) {
        String normalizedPath = requestPath.split("\\?")[0];

        if ("/".equals(normalizedPath) || normalizedPath.isEmpty()) {
            return webRoot.resolve("index.html");
        }

        if (normalizedPath.startsWith("/")) {
            normalizedPath = normalizedPath.substring(1);
        }

        if (normalizedPath.isEmpty()) {
            return webRoot.resolve("index.html");
        }

        if (normalizedPath.endsWith(".html") || normalizedPath.endsWith(".htm")) {
            return webRoot.resolve(normalizedPath);
        }

        if ("about".equals(normalizedPath)) {
            return webRoot.resolve("about.html");
        }

        return webRoot.resolve(normalizedPath + ".html");
    }

    private void sendResponse(OutputStream out, int statusCode, String reasonPhrase, String contentType, byte[] body) throws IOException {
        String statusLine = "HTTP/1.1 " + statusCode + " " + reasonPhrase + CRLF;
        String headers = "Content-Type: " + contentType + CRLF +
                         "Content-Length: " + body.length + CRLF +
                         "Connection: close" + CRLF + CRLF;
        out.write((statusLine + headers).getBytes(StandardCharsets.UTF_8));
        out.write(body);
        out.flush();
    }

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
