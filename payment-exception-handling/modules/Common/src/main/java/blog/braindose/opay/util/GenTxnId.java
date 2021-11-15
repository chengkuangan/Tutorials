package blog.braindose.opay.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Generate unique transaction id base on the transaction type
 */
public class GenTxnId {

    static SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmssSSS");
    
    /**
     * Generate unique transaction id in the format of TxnType-yyyyMMdd-HHmmssSSS-RNDNUM
     * @param type Transaction type. @see blog.braindose.paygate.util.TxnTypes
     * @return unique transaction id in String format.
     */
    static public String id(int type){
        String gs = uniqueNumber();
        String ds = df.format(new Date());
        return type + "-" + ds + "-" + gs;
    }

    /**
     * Create 5 digit random number. 
     * Quarkus seems to not support/prefer Random
     * @return 5 digit random number
     */
    static private String uniqueNumber(){
        String n = "";
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i=1; i<10; i++) {
            list.add(Integer.valueOf(i));
        }
        Collections.shuffle(list);
        for (int i=0; i<5; i++) {
            n += "" + list.get(i);
        }
        return n;
    }
    
}
