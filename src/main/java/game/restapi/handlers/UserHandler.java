package game.restapi.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import game.user.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.stream.Collectors;

import static game.restapi.MTCGServer.users;

public class UserHandler implements HttpHandler {
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