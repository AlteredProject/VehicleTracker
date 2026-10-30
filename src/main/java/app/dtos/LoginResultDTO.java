package app.dtos;

public record LoginResultDTO(boolean success, String message, Integer userId) {}
