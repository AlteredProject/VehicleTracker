package app.controllers;

import app.dtos.LoginRequestDTO;
import app.dtos.UserRegistryDTO;
import app.services.JWTService;
import app.services.UserService;

import app.dtos.LoginResultDTO;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class UserHandler {
    UserService userService;
    JWTService jwtService;

    public UserHandler(UserService userService, JWTService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public void create(Context ctx) {
        UserRegistryDTO dto = ctx.bodyAsClass(UserRegistryDTO.class);

        List<String> errors = userService.createUser(dto);

        if (!errors.isEmpty()) {
            ctx.status(HttpStatus.BAD_REQUEST).json(errors);
            return;
        }

        ctx.status(HttpStatus.CREATED).json(Map.of("message", "User created successfully"));
    }

    public void login(Context ctx) {
        LoginRequestDTO dto = ctx.bodyAsClass(LoginRequestDTO.class);

        LoginResultDTO result = userService.login(dto.username(), dto.password());

        // Verify result
        if (!result.success()) {
            ctx.status(HttpStatus.UNAUTHORIZED).json(Map.of("message", result.message()));
            return;
        }

        // Generate token
        String token = jwtService.generateToken(result.userId());

        ctx.status(HttpStatus.OK).json(Map.of("message", result.message(), "token", token));

    }
}
