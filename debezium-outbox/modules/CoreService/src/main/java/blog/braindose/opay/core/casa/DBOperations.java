package blog.braindose.opay.core.casa;

import javax.transaction.Transactional;
import java.time.Instant;
import blog.braindose.opay.core.AccountBalanceNotEnoughException;
import blog.braindose.opay.core.AccountNotFoundException;
import javax.enterprise.context.ApplicationScoped;
import blog.braindose.opay.obxevent.CasaEventData;

/**
 * Provides the implementation of db operation for Casa account
 * 
 */
@ApplicationScoped
@Transactional
public class DBOperations {
    
    /**
     * Perform account balancing. Throws exceptions in case of problem to be captured by ComsumeCasa @see blog.braindose.core.casa.ComsumeCasa
     * Using Transactional.TxType.REQUIRES_NEW for this method so that any exception will cause the database transactions to be rolled back but 
     * the messaging consuming and producing events in ComsumeCasa will be interrupted.
     * @param data Containing the Casa transaction data from Kafka event
     * @return The timestamp that the casa record is updated.
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Instant performBalanceAccount(CasaEventData data) {
        String sourceAccNo = data.getSourceAccountNo();
        String targetAccNo = data.getRecipientAccountNo();
        Double amount = data.getAmount();

        CasaCore casaSource = CasaCore.findById(sourceAccNo);
        CasaCore casaTarget = CasaCore.findById(targetAccNo);


        if (casaSource == null){
            throw new AccountNotFoundException("Source account not exist or has been dormant.");
        }

        if (casaTarget == null){
            throw new AccountNotFoundException("Recipient account not exist or has been dormant.");
        }

        if (casaSource.balance < amount) {
            throw new AccountBalanceNotEnoughException("Source account balance is insufficient.");
        }

        Instant currentTimestamp = Instant.now();
        casaSource.balance = casaSource.balance - amount;
        casaSource.updatedTimestamp = currentTimestamp;
        casaSource.persist();
        
        casaTarget.balance = casaTarget.balance + amount;
        casaTarget.updatedTimestamp = currentTimestamp;
        casaTarget.persistAndFlush();
        
        return currentTimestamp;
    }
}
