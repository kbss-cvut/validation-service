package cz.cvut.kbss.validation.exception;

import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

/**
 * Maps exceptions to HTTP responses.
 */
public class ExceptionMapper {

    @ServerExceptionMapper
    public RestResponse<String> handleValidationInputSizeThresholdExceededException(
            ValidationInputSizeThresholdExceededException e) {
        return RestResponse.status(Response.Status.REQUEST_ENTITY_TOO_LARGE, e.getMessage());
    }
}
