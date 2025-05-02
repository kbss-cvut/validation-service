package cz.cvut.kbss.validation;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.topbraid.shacl.validation.ValidationReport;

import java.util.List;

@Path("/validate")
public class ValidationResource {

    @Inject
    ValidationService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ValidationReport validate(@QueryParam("contextUri") List<String> contextUris,
                                     @QueryParam("rule") List<String> rules,
                                     @QueryParam("language") @DefaultValue("cs") String language) {
        return executeValidation(contextUris, rules, language);
    }

    private ValidationReport executeValidation(List<String> contextUris, List<String> rules, String language) {
        if (contextUris.isEmpty()) {
            throw new WebApplicationException("No context URIs specified.", Response.Status.BAD_REQUEST);
        }
        return rules.isEmpty() ? service.validate(contextUris, language) :
               service.validateWithRules(contextUris, rules, language);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public ValidationReport validateWithPost(@FormParam("contextUri") List<String> contextUris,
                                             @FormParam("rule") List<String> rules,
                                             @FormParam("language") @DefaultValue("cs") String language) {
        return executeValidation(contextUris, rules, language);
    }
}
