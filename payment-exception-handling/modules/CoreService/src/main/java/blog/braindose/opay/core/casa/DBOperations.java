package blog.braindose.opay.core.casa;

import javax.transaction.Transactional;
import java.util.Date;
import blog.braindose.opay.core.AccountBalanceNotEnoughException;
import blog.braindose.opay.core.AccountNotFoundException;
import javax.enterprise.context.ApplicationScoped;
import blog.braindose.opay.obxevent.CasaEventData;
import org.apache.commons.lang3.time.DateUtils;
import org.jboss.logging.Logger;

/**
 * Provides the implementation of db operation for Casa account
 * 
 */
@ApplicationScoped
@Transactional
public class DBOperations {
    
    private static final Logger LOGGER = Logger.getLogger(DBOperations.class);

    /**
     * Perform account balancing. Throws exceptions in case of problem to be captured by ComsumeCasa @see blog.braindose.core.casa.ComsumeCasa
     * Using Transactional.TxType.REQUIRES_NEW for this method so that any exception will cause the database transactions to be rolled back but 
     * the messaging consuming and producing events in ComsumeCasa will be interrupted.
     * @param data Containing the Casa transaction data from Kafka event
     * @return The timestamp that the casa record is updated.
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Date performBalanceAccount(CasaEventData data) {

        String sourceAccNo = data.getSourceAccountNo();
        LOGGER.debug("Return from database, sourceAccNo = " + sourceAccNo);
        String targetAccNo = data.getRecipientAccountNo();
        LOGGER.debug("Return from database, targetAccNo = " + targetAccNo);
        double amount = data.getAmount();

        CasaCore casaSource = CasaCore.findById(sourceAccNo);
        LOGGER.debug("Return from database, casaSource = " + casaSource);
        CasaCore casaTarget = CasaCore.findById(targetAccNo);
        LOGGER.debug("Return from database, casaTarget = " + casaTarget);


        if (casaSource == null){
            throw new AccountNotFoundException("Source account not exist or has been dormant.");
        }

        if (casaTarget == null){
            throw new AccountNotFoundException("Recipient account not exist or has been dormant.");
        }

        if (casaSource.balance < amount) {
            throw new AccountBalanceNotEnoughException("Source account balance is insufficient.");
        }

        if (casaSource.dtaUpdatedTimestamp != null && DateUtils.isSameDay(new Date(), casaSource.dtaUpdatedTimestamp)){
            casaSource.dailyTotalAmount += amount;
            LOGGER.debug("Updated dailyTotalAmount = " + casaSource.dailyTotalAmount);
        }
        else{
            casaSource.dailyTotalAmount = amount;   //reset
            LOGGER.debug("Resetting dailyTotalAmount = " + casaSource.dailyTotalAmount);
        }
        

        Date currentTimestamp = new Date();
        casaSource.balance = casaSource.balance - amount;
        casaSource.updatedTimestamp = currentTimestamp;
        casaSource.dtaUpdatedTimestamp = currentTimestamp;
        LOGGER.debug("Persisting casaSource data... ");
        casaSource.persist();
        LOGGER.debug("Persisting casaSource data... done");
        
        casaTarget.balance = casaTarget.balance + amount;
        casaTarget.updatedTimestamp = currentTimestamp;
        LOGGER.debug("Persisting casaTarget data... ");
        casaTarget.persistAndFlush();
        LOGGER.debug("Persisting casaTarget data... done");
        
        return currentTimestamp;
    }
}
