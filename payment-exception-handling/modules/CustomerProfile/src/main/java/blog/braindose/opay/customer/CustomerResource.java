package blog.braindose.opay.customer;

import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.transaction.Transactional;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.util.Date;
import org.jboss.logging.Logger;
import blog.braindose.opay.customer.model.CustomerEntity;

/**
 * Provides REST interfaces for Casa services.
 */
@Path("cust")
public class CustomerResource {

    private static final Logger LOGGER = Logger.getLogger(CustomerResource.class);
    private boolean failed = false;

    /**
     * Create a new Customer profile
     * @param cust
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public CustomerEntity add(CustomerEntity customer) {
        try{
            customer.persistAndFlush();
        }
        catch(PersistenceException e){
            LOGGER.error("Error creating the customer record in database.", e);
            throw e;
        }
        return customer;
    }

    /**
     * Get the customer profile details by customer id
     * @param id
     * @return
     */
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    @GET
    public CustomerEntity getById(@PathParam("id") Long id) {
        LOGGER.debug("Id = " + id);
        CustomerEntity cust = null;
        try{
            cust = CustomerEntity.findById(id);    
            LOGGER.debug("cust = " + cust);
            if (cust != null)   LOGGER.debug("cust.custId = " + cust.custId);
        }
        catch(Exception e){
            LOGGER.error("Error retrieving customer record from database", e);
            throw e;
        }
        return cust;
    }

    /**
     * Get the customer profile details by accountNo
     * @param accountNo
     * @return
     */
    @Produces(MediaType.APPLICATION_JSON)
    @Path("acc/{number}")
    @GET
    public CustomerEntity getByAccountNo(@PathParam("number") String accNo) {
        LOGGER.debug("accNo = " + accNo);
        CustomerEntity cust = null;
        try{
            cust = CustomerEntity.findByAccount(accNo);    
            LOGGER.debug("cust = " + cust);
            if (cust != null)   LOGGER.debug("cust.custId = " + cust.custId);
        }
        catch(Exception e){
            LOGGER.error("Error retrieving customer record from database", e);
            throw e;
        }
        return cust;
    }
}