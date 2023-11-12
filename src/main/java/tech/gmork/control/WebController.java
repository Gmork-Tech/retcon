package tech.gmork.control;

import io.smallrye.common.annotation.Blocking;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestQuery;
import tech.gmork.model.dtos.ApplicationListResponse;
import tech.gmork.model.entities.Application;

@Path("/admin/applications")
public class WebController {

    @GET
    @Blocking
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public ApplicationListResponse pageApplications(@RestQuery int pageNo) {
        return ApplicationListResponse.byPageNumber(pageNo);
    }

    @POST
    @Blocking
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.WILDCARD)
    public Response updateApplication(Application app) {
        // Start by validating the config
        app.validate();
        return Response.ok().build();
    }

}
