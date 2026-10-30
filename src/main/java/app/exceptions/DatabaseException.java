package app.exceptions;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class DatabaseException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseException.class);

    public DatabaseException(String message, Throwable cause){
        super(message, cause);
        logger.error("DatabaseException: {}", message, cause);
    }
}
