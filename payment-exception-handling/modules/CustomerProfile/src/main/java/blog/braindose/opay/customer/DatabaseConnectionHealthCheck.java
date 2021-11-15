package blog.braindose.opay.customer;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import javax.enterprise.context.ApplicationScoped;
import blog.braindose.opay.customer.model.CustomerEntity;

@Readiness
@ApplicationScoped
public class DatabaseConnectionHealthCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("Database connection health check");
            CustomerEntity c = CustomerEntity.findById(1L);        /// a dummy find just to check db is alive
            if (c == null){
                responseBuilder.down();
            }
            responseBuilder.up();
        return responseBuilder.build();
    }
}