package blog.braindose.opay.audit;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class AuditResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/audit/all")
          .then()
             .statusCode(200)
             .body(is("Hello RESTEasy Reactive"));
    }

}