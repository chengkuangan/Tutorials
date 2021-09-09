package com.gck.demo.paymentgateway.accountbalance.rest;

// --- Commented out for Quarkus Migration
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestMethod;
// import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

/**
 * Healthz endpoint for liveness and readiness of the application
 *
 * Created by ganck.
 */

 // --- Commented out for Quarkus Migration
// @RestController
// @RequestMapping("/ws/healthz")
@Path("/ws/healthz")
public class Healthz {

    // --- Commented out for Quarkus Migration
    //@RequestMapping(method = RequestMethod.GET, value = "/")
    @Path("/")
    @GET
    public String healthz() {
        return "OK";
    }
}
