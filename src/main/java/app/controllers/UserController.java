package app.controllers;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class UserController implements EndpointGroup {
    private final UserHandler userHandler;

    public UserController(UserHandler userHandler) {
        this.userHandler = userHandler;
    }

    @Override
    public void addEndpoints() {
        post("/api/v1/users", userHandler::create);
        post("/api/v1/auth/login", userHandler::login);
    }
}
