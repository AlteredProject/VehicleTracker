package app.testutils;

import java.util.Base64;

public class TestJWTConfig {
    public static final String JWT_SECRET = Base64.getEncoder().encodeToString("test-only-secret-key-at-least-32-bytes".getBytes());
}
