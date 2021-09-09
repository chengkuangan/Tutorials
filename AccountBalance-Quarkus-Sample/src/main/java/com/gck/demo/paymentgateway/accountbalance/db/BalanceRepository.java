package com.gck.demo.paymentgateway.accountbalance.db;

import java.util.List;
import com.gck.demo.paymentgateway.accountbalance.db.*;
import com.gck.demo.paymentgateway.accountbalance.models.*;
// --- Commented out for Quarkus Migration
// import org.springframework.data.mongodb.repository.MongoRepository;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import javax.enterprise.context.ApplicationScoped;
import io.quarkus.mongodb.panache.PanacheQuery;

// --- Commented out for Quarkus Migration
// public interface BalanceRepository extends MongoRepository<Balance, String> {
@ApplicationScoped	
public class BalanceRepository implements PanacheMongoRepository<Balance> {	
	// --- Commented out for Quarkus Migration
	// Balance findByAccountId(String accountId);
	public Balance findByAccountId(String accountId){
		return find("accountId", accountId).firstResult();
	}
	
}