package game.restapi;


import game.deck.Deck;
import game.packs.Package;
import game.user.User;
import game.card.Card;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MTCGServer {
    private static final int PORT = 10001;
    private static final Map<String, User> users = new HashMap<>();
    private static final List<Package> availablePackages = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/users", new UserHandler());
        server.createContext("/sessions", new SessionHandler());
        server.createContext("/packages", new PackageHandler());
        server.createContext("/transactions/packages", new AcquirePackageHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port " + PORT);
    }

    static class UserHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("[UserHandler] Received request: " + exchange.getRequestMethod());
            if ("POST".equals(exchange.getRequestMethod())) {
                ObjectMapper mapper = new ObjectMapper();
                String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                        .lines().collect(Collectors.joining("\n"));
                System.out.println("[UserHandler] Request body: " + requestBody);
                try {
                    User user = mapper.readValue(requestBody, User.class);
                    System.out.println("[UserHandler] Parsed user: " + user.getUsername());

                    if (users.containsKey(user.getUsername())) {
                        String response = "User already exists\n";
                        System.out.println("[UserHandler] User already exists: " + user.getUsername());
                        exchange.sendResponseHeaders(409, response.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    } else {
                        user.setCoins(20); // Initialize with 20 coins
                        users.put(user.getUsername(), user);
                        String response = "User created\n";
                        System.out.println("[UserHandler] User created: " + user.getUsername());
                        exchange.sendResponseHeaders(201, response.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    }
                } catch (JsonProcessingException e) {
                    System.out.println("[UserHandler] Failed to parse request body: " + e.getMessage());
                    exchange.sendResponseHeaders(400, -1); // Bad Request
                }
            } else {
                System.out.println("[UserHandler] Method not allowed: " + exchange.getRequestMethod());
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }

    static class SessionHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("[SessionHandler] Received request: " + exchange.getRequestMethod());
            if ("POST".equals(exchange.getRequestMethod())) {
                ObjectMapper mapper = new ObjectMapper();
                String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                        .lines().collect(Collectors.joining("\n"));
                System.out.println("[SessionHandler] Request body: " + requestBody);
                try {
                    User user = mapper.readValue(requestBody, User.class);
                    System.out.println("[SessionHandler] Parsed user: " + user.getUsername());

                    if (users.containsKey(user.getUsername()) && users.get(user.getUsername()).getPassword().equals(user.getPassword())) {
                        String token = user.getUsername() + "-mtcgToken";
                        System.out.println("[SessionHandler] Login successful for user: " + user.getUsername());
                        String response = token + "\n";
                        exchange.sendResponseHeaders(200, response.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    } else {
                        String response = "Login failed\n";
                        System.out.println("[SessionHandler] Login failed for user: " + user.getUsername());
                        exchange.sendResponseHeaders(401, response.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    }
                } catch (JsonProcessingException e) {
                    System.out.println("[SessionHandler] Failed to parse request body: " + e.getMessage());
                    exchange.sendResponseHeaders(400, -1); // Bad Request
                }
            } else {
                System.out.println("[SessionHandler] Method not allowed: " + exchange.getRequestMethod());
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }


    static class PackageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("[PackageHandler] Received request: " + exchange.getRequestMethod());
            if ("POST".equals(exchange.getRequestMethod())) {
                ObjectMapper mapper = new ObjectMapper();
                String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                        .lines().collect(Collectors.joining("\n"));
                System.out.println("[PackageHandler] Request body: " + requestBody);
                try {
                    Package newPackage = mapper.readValue(requestBody, Package.class);
                    if (newPackage.getCards().size() == 5) {
                        availablePackages.add(newPackage);
                        String response = "Package added\n";
                        exchange.sendResponseHeaders(201, response.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    } else {
                        String response = "Package must contain exactly 5 cards\n";
                        exchange.sendResponseHeaders(400, response.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    }
                } catch (JsonProcessingException e) {
                    System.out.println("[PackageHandler] Failed to parse request body: " + e.getMessage());
                    exchange.sendResponseHeaders(400, -1); // Bad Request
                }
            } else {
                System.out.println("[PackageHandler] Method not allowed: " + exchange.getRequestMethod());
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }

    static class AcquirePackageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("[AcquirePackageHandler] Received request: " + exchange.getRequestMethod());
            if ("POST".equals(exchange.getRequestMethod())) {
                String token = exchange.getRequestHeaders().getFirst("Authorization");
                if (token == null || !users.containsKey(token.split("-mtcgToken")[0])) {
                    String response = "Unauthorized\n";
                    exchange.sendResponseHeaders(401, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                    return;
                }

                String username = token.split("-mtcgToken")[0];
                User user = users.get(username);

                if (user.getCoins() < 5) {
                    String response = "Not enough coins\n";
                    exchange.sendResponseHeaders(400, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } else if (availablePackages.isEmpty()) {
                    String response = "No packages available\n";
                    exchange.sendResponseHeaders(400, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } else {
                    user.setCoins(user.getCoins() - 5);
                    Package acquiredPackage = availablePackages.removeFirst();
                    user.getStack().addAll(acquiredPackage.getCards());

                    // Serialize the acquired package content
                    ObjectMapper mapper = new ObjectMapper();
                    String packageContent = mapper.writeValueAsString(acquiredPackage);

                    String response = "Package acquired\n" + packageContent + "\n";
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            } else {
                System.out.println("[AcquirePackageHandler] Method not allowed: " + exchange.getRequestMethod());
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }
}
