package game.restapi;

import com.sun.net.httpserver.HttpServer;
import game.packs.Package;
import game.restapi.handlers.*;
import game.user.User;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MTCGServer {
    private static final int PORT = 10001;
    public static final Map<String, User> users = new HashMap<>();
    public static final List<Package> availablePackages = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/users", new UserHandler());
        server.createContext("/sessions", new SessionHandler());
        server.createContext("/packages", new PackageHandler());
        server.createContext("/transactions/packages", new AcquirePackageHandler());
        server.createContext("/cards", new CardsHandler()); // Placeholder for fetching cards
        server.createContext("/deck", new DeckHandler()); // Placeholder for managing decks
        server.createContext("/tradings", new TradingHandler()); // Placeholder for trading
        server.createContext("/stats", new StatsHandler()); // Placeholder for stats
        server.createContext("/scoreboard", new ScoreboardHandler()); // Placeholder for scoreboard
        server.createContext("/battles", new BattleHandler()); // Placeholder for battles
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port " + PORT);
    }
}
