package app.exceptions;

import io.javalin.http.HttpStatus;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private static final Logger logger = LoggerFactory.getLogger(ApiException.class);

    public ApiException(HttpStatus status, String message){
        super(message);
        this.status = status;
        logger.error("ApiException (code={}): {}", status, message);
    }
}