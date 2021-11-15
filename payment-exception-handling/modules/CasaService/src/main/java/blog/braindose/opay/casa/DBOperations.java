package blog.braindose.opay.casa;

import javax.transaction.Transactional;
import java.util.Date;
import blog.braindose.opay.exception.RecordNotFoundException;
import javax.enterprise.context.ApplicationScoped;
import blog.braindose.opay.obxevent.CasaEventData;

/**
 * Provides database operation to update the responses from core service to Casa database
 */
@ApplicationScoped
@Transactional
public class DBOperations {
    
    /**
     * Update the response from Core system into the DB. Throws exceptions to be hanled by calling class so that the database transaction can be rolled back 
     * automatically while not interrupting the messaign consumptions and producing..
     * 
     * @param data Containing the Casa response data from Kafka event
     * @return The timestamp that the casa record is updated.
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Date updateResponse(CasaEventData data) {
        
        Casa casaEntity = Casa.findById(data.getId());
        
        if (casaEntity == null){
            throw new RecordNotFoundException("Casa record not found");
        }

        Date currentTimestamp = new Date();
        casaEntity.coreProcessedTimestamp = data.getCoreProcessedTimestamp();
        casaEntity.responseTimestamp = currentTimestamp;
        casaEntity.responseMessages = data.getResponseMessages();
        casaEntity.status = data.getStatus();
        casaEntity.persistAndFlush();
        
        return currentTimestamp;
    }
}
