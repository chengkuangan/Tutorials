package blog.braindose.opay.core;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import javax.enterprise.context.ApplicationScoped;
import blog.braindose.opay.core.casa.CasaCore;

@Readiness
@ApplicationScoped
public class DatabaseConnectionHealthCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("Database connection health check");
            CasaCore c = CasaCore.findById("1");            /// a dummy find just to check db is alive
            if (c == null){
                responseBuilder.down();
            }
            responseBuilder.up();
        return responseBuilder.build();
    }
}