package org.example.gateway;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@Component
public class GatewayRouteLogger implements ApplicationRunner {

    private final RouteLocator routeLocator;

    public GatewayRouteLogger(RouteLocator routeLocator) {
        this.routeLocator = routeLocator;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<String> routes = new ArrayList<>();
        Flux<Route> routeFlux = routeLocator.getRoutes();
        routeFlux.collectList().block().forEach(route ->
                routes.add(route.getId() + " -> " + route.getUri() + " predicates=" + route.getPredicate())
        );

        // #region agent log
        writeLog("H1,H5", "GatewayRouteLogger.java:run",
                "Registered gateway routes at startup",
                "{\"routeCount\":" + routes.size() + ",\"routes\":\"" + escape(String.join(" | ", routes)) + "\"}");
        // #endregion
    }

    private static Path resolveLogPath() {
        Path cwd = Paths.get(System.getProperty("user.dir"));
        Path inParent = cwd.getParent() != null ? cwd.getParent().resolve("debug-695786.log") : cwd.resolve("debug-695786.log");
        return inParent;
    }

    private static void writeLog(String hypothesisId, String location, String message, String dataJson) {
        try {
            String line = "{\"sessionId\":\"695786\",\"hypothesisId\":\"" + hypothesisId
                    + "\",\"location\":\"" + location + "\",\"message\":\"" + escape(message)
                    + "\",\"data\":" + dataJson + ",\"timestamp\":" + System.currentTimeMillis() + "}\n";
            Files.writeString(resolveLogPath(), line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception ignored) {
        }
    }

    private static String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
