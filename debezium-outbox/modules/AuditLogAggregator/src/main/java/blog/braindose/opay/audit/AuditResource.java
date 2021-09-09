package blog.braindose.opay.audit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

/**
 * Provides REST interfaces for Casa services.
 */
@Path("/audit")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuditResource {

    /**
     * Hello
     * @return
     */
    @GET
    public String hello() {
        return "hello";
    }
}