package cz.cvut.kbss.validation.exception;

/**
 * Base exception for the validation service.
 */
public class ValidatorException extends RuntimeException {

    public ValidatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
