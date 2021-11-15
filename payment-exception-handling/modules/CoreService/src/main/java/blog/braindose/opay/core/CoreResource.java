package blog.braindose.opay.core;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

import blog.braindose.opay.core.casa.CasaCore;

@Path("/core")
public class CoreResource {

    private static final Logger LOGGER = Logger.getLogger(CoreResource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("casa/{accno}")
    public CasaCore getCasaAccount(@PathParam("accno") String accno) {
        LOGGER.debug("accno = " + accno);
        CasaCore casa = null;
        try{
            casa = CasaCore.findById(accno);    
            LOGGER.debug("casa = " + casa);
            if (casa != null)   LOGGER.debug("casa.accountNo = " + casa.accountNo);
        }
        catch(Exception e){
            LOGGER.error("Error retrieving customer record from database", e);
            throw e;
        }
        return casa;
    }
}