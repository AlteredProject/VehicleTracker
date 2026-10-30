package app.security;

import app.services.JWTService;
import io.javalin.http.Handler;
import io.jsonwebtoken.JwtException;

import java.util.Map;

public class JWTMiddleware {
    private final JWTService jwtService;

    public JWTMiddleware(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    public Handler authenticate() {
        return ctx -> {
            String header = ctx.header("Authorization");


            if(header == null || !header.startsWith("Bearer ")) {
                ctx.status(401).json(Map.of("message", "Missing or invalid token"));
                ctx.skipRemainingHandlers();
                return;
            }

            String token = header.substring(7);

            try {
                Integer userId = jwtService.getUserIdFromToken(token);

                ctx.attribute("userId", userId);
            } catch (JwtException | IllegalArgumentException e) {
                ctx.status(401).json(Map.of("message", "Invalid or expired token"));
                ctx.skipRemainingHandlers();
                return;
            }
        };
    }
}
