package tech.gmork.control;

import io.smallrye.common.annotation.Blocking;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestQuery;
import tech.gmork.model.dtos.ApplicationListResponse;
import tech.gmork.model.dtos.UserListResponse;
import tech.gmork.model.entities.Application;
import tech.gmork.model.entities.LocalUser;

@Path("/admin")
public class WebController {

    @GET
    @Blocking
    @Path("/applications")
    @Produces(MediaType.APPLICATION_JSON)
    public ApplicationListResponse pageApplications(@RestQuery int pageNo) {
        return ApplicationListResponse.byPageNumber(pageNo);
    }

    @POST
    @Blocking
    @Path("/applications/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.WILDCARD)
    public Response updateApplication(Application app) {
        app.validate();
        return Response.ok().build();
    }

    @GET
    @Blocking
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    public UserListResponse pageUsers(@RestQuery int pageNo) {
        return UserListResponse.byPageNumber(pageNo);
    }

    @POST
    @Blocking
    @Path("/users/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.WILDCARD)
    public Response updateUser(LocalUser user) {
        user.validate();
        return Response.ok().build();
    }

}
