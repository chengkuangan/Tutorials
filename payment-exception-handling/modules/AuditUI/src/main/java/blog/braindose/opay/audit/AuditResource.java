package blog.braindose.opay.audit;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import blog.braindose.opay.audit.model.AuditData;

@Path("/audit")
public class AuditResource {

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
     public List<AuditData> getAll() { 
        //System.out.println("called ......");
        return AuditData.listAll();
    }
    
}