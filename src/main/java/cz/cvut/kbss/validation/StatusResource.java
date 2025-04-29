package cz.cvut.kbss.validation;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/status")
public class StatusResource {

    @GET
    public String status() {
        return "UP";
    }
}
