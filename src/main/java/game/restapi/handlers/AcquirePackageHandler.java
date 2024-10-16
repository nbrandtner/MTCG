package game.restapi.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import game.packs.Package;
import game.user.User;

import java.io.IOException;
import java.io.OutputStream;

import static game.restapi.MTCGServer.availablePackages;
import static game.restapi.MTCGServer.users;

public class AcquirePackageHandler implements HttpHandler {
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