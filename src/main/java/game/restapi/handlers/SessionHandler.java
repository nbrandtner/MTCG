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

public class SessionHandler implements HttpHandler {
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
