package app.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityNotFoundException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(EntityNotFoundException.class);

    public EntityNotFoundException(String message){
        super(message);
        logger.error("EntityNotFoundException: {}", message);
    }
}
