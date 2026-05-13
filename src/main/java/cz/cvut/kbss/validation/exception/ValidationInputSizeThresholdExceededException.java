package cz.cvut.kbss.validation.exception;

/**
 * Exception indicating that the size of the input data for validation exceeds the configured threshold.
 */
public class ValidationInputSizeThresholdExceededException extends ValidatorException {

    public ValidationInputSizeThresholdExceededException(String message) {
        super(message);
    }
}
