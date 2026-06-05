package org.example.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Component
public class GatewayDebugFilter implements GlobalFilter, Ordered {

    private static final Path LOG_PATH = resolveLogPath();

    private static Path resolveLogPath() {
        Path cwd = Paths.get(System.getProperty("user.dir"));
        Path inParent = cwd.getParent() != null ? cwd.getParent().resolve("debug-695786.log") : cwd.resolve("debug-695786.log");
        Path inCwd = cwd.resolve("debug-695786.log");
        return Files.exists(inParent.getParent()) ? inParent : inCwd;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getRawPath();
        Route route = exchange.getAttribute(org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        String routeId = route != null ? route.getId() : "NO_ROUTE_MATCH";
        String targetUri = route != null ? route.getUri().toString() : "none";

        // #region agent log
        writeLog("H1,H2", "GatewayDebugFilter.java:filter:entry",
                "Incoming gateway request",
                "{\"path\":\"" + escape(path) + "\",\"routeId\":\"" + escape(routeId) + "\",\"targetUri\":\"" + escape(targetUri) + "\"}");
        // #endregion

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            int status = exchange.getResponse().getStatusCode() != null
                    ? exchange.getResponse().getStatusCode().value()
                    : -1;

            // #region agent log
            writeLog("H1,H3,H4", "GatewayDebugFilter.java:filter:exit",
                    "Gateway response",
                    "{\"path\":\"" + escape(path) + "\",\"routeId\":\"" + escape(routeId) + "\",\"status\":" + status + "}");
            // #endregion
        }));
    }

    private static void writeLog(String hypothesisId, String location, String message, String dataJson) {
        try {
            String line = "{\"sessionId\":\"695786\",\"hypothesisId\":\"" + hypothesisId
                    + "\",\"location\":\"" + location + "\",\"message\":\"" + escape(message)
                    + "\",\"data\":" + dataJson + ",\"timestamp\":" + System.currentTimeMillis() + "}\n";
            Files.writeString(LOG_PATH, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception ignored) {
        }
    }

    private static String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
