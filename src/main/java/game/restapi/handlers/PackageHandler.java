package game.restapi.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import game.packs.Package;
import game.restapi.MTCGServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.stream.Collectors;

import static game.restapi.MTCGServer.availablePackages;

public class PackageHandler implements HttpHandler {
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