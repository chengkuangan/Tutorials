package blog.braindose.opay.casa;

import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.transaction.Transactional;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import io.debezium.outbox.quarkus.ExportedEvent;
import java.time.Instant;
import blog.braindose.opay.util.GenTxnId;
import blog.braindose.opay.util.TxnTypes;
import blog.braindose.opay.model.Status;
import org.jboss.logging.Logger;
import blog.braindose.opay.obxevent.CasaEventData;
import blog.braindose.opay.obxevent.AggregateTypes;
import blog.braindose.opay.obxevent.CasaAuditEvent;
import blog.braindose.opay.obxevent.CasaFailedEvent;
import blog.braindose.opay.obxevent.EventTypes;

/**
 * Provides REST interfaces for Casa services.
 */
@Path("casa")
public class CasaResource {

    @Inject
    Event<ExportedEvent<?, ?>> event;
    
    private static final Logger LOGGER = Logger.getLogger(CasaResource.class);
    private CasaEventData casaEventData = null;
    private boolean failed = false;

    /**
     * Create a new Casa transaction
     * @param casa
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Casa add(Casa casa) {
        try{
            casa.id = GenTxnId.id(TxnTypes.CASA);
            casa.createdTimestamp = Instant.now();
            casa.status = Status.SUBMITTED;
            casaEventData = new CasaEventData(casa.id, casa.recipientAccountNo, casa.sourceAccountNo, casa.amount, casa.createdTimestamp, casa.recipientReference, casa.paymentType, casa.status);
            casaEventData.setEventTimestamp(casa.createdTimestamp);
            casaEventData.setEventSources(Casa.class.getName());
            casa.persistAndFlush();
            event.fire(new CasaEvent(casaEventData));
        }
        catch(PersistenceException e){
            failed = true;
            if (casaEventData != null){
                casaEventData.setStatus(Status.FAILED);
                casaEventData.setResponseMessages("Error creating the Casa record in database.");
            }
            LOGGER.error("Error creating the Casa record in database.", e);
            throw e;
        }
        finally{
            if (casaEventData != null){
                event.fire(new CasaAuditEvent(casaEventData, EventTypes.PAYMENT, AggregateTypes.AUDIT_CASA));
                if (failed) event.fire(new CasaFailedEvent(casaEventData));
            }
        }
        return casa;
    }

    /**
     * Get the casa record
     * @param casa
     * @return
     */
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    @GET
    public Casa getById(@PathParam("id") String id) {
        LOGGER.debug("Id = " + id);
        Casa casa = null;
        try{
            casa = Casa.findById(id);    
            LOGGER.debug("casa = " + casa);
            if (casa != null)   LOGGER.debug("casa.id = " + casa.id);
        }
        catch(Exception e){
            LOGGER.error("Error retrieving casa record from database", e);
            throw e;
        }
        return casa;
    }
}