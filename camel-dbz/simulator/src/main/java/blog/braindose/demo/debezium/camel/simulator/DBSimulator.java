package blog.braindose.demo.debezium.camel.simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.jboss.logging.Logger;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.Scheduled.ConcurrentExecution;

@ApplicationScoped
public class DBSimulator {

    private static final Logger LOGGER = Logger.getLogger(DBSimulator.class);
    private List<Sku> records = null;
    @Inject
    DBOperations dbo;

    public DBSimulator() {

        records = new ArrayList<>();
        
        InputStreamReader reader = new InputStreamReader(DBSimulator.class.getClassLoader().getResourceAsStream("/sku.csv"));

        if (reader == null) {
            throw new IllegalArgumentException("sku.csv file not found!");
        } else {
            try {
                records = loadSku(reader);
                LOGGER.info("Simulation SKU data is loaded. Total record: " + records.size());
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }

    }

    @Scheduled(every = "{cron.scheduler.interval}", delay = 5, delayUnit = TimeUnit.SECONDS, concurrentExecution = ConcurrentExecution.SKIP)
    void simulateNewOrder() {
        LOGGER.info("Simulating new order now ...");
        
        Customer customer = Customer.findById(randomIndex(1, 30));
        Sku sku = records.get(randomIndex(0, records.size()));

        LOGGER.info("The lucky customer is: " + customer);
        LOGGER.info("The lucky SKU is: " + sku);

        dbo.createOrder(customer.custId, sku.sku, sku.description, sku.price);

        /*
        records.forEach(sku -> {
            LOGGER.debug(sku);
        });
         */
    }

    private List<Sku> loadSku(Reader reader) throws IOException, CsvValidationException{
        List<Sku> records = new ArrayList<Sku>();
        try (CSVReader csvReader = new CSVReader(reader);) {
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                //records.add(Arrays.asList(values));
                Sku sku = new Sku();
                if (values[0] != null) {
                    sku.sku = (String) values[0];
                }
                if (values[1] != null) {
                    sku.description = (String) values[1];
                }
                if (values[2] != null) {
                    String p = values[2];
                    try {
                        LOGGER.debug("sku.csv - > price = " + p);
                        sku.price = (p != null && p.trim().length() > 0) ? Double.parseDouble(p) : 0;
                    } catch (NumberFormatException e) {
                        LOGGER.warn("SKU price is not valid. SKU: " + sku.sku);
                        sku = null;
                    }
                }
                if (sku != null && sku.price > 0d) 
                    records.add(sku);
            }
        }
        return records;
    }

    static private int randomIndex(int start, int size){
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i=start; i<=size; i++) {
            list.add(Integer.valueOf(i));
        }
        Collections.shuffle(list);
        return list.get(0);
    }

}
