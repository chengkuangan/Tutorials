package blog.braindose.opay.core;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

@Path("/core")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CoreResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/hello")
    public String hello() {
        return "hello";
    }
}