package blog.braindose.paygate.kafka;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/")
public class IndexResource {

    // Matches with index.html
    @Inject
    Template index;
    @ConfigProperty(name = "ui.index.title") 
    String uiTitle;
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
        return index.data("title", uiTitle);
    }

}