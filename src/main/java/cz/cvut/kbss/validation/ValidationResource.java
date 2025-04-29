package cz.cvut.kbss.validation;

import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.topbraid.shacl.validation.ValidationReport;

import java.util.List;

@Path("/validate")
public class ValidationResource {

    @Inject
    ValidationService service;

    @GET
    public ValidationReport validate(@QueryParam("contextUri") List<String> contextUris,
                                     @QueryParam("rule") List<String> rules,
                                     @QueryParam("language") @DefaultValue("cs") String language) {
        if (contextUris.isEmpty()) {
            throw new WebApplicationException("No context URIs specified.", Response.Status.BAD_REQUEST);
        }
        return rules.isEmpty() ? service.validate(contextUris, language) :
               service.validateWithRules(contextUris, rules, language);
    }
}
