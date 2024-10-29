package com.hendisantika.springbootliquibase;

import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Testcontainers
@SpringBootTest(
        properties = {
                "management.endpoint.health.show-details=always",
                "spring.datasource.url=jdbc:tc:mysql:8.4.0:///bankDB?TC_INITSCRIPT=sql/01-create-users-and-addresses-schema.sql"
        },
        webEnvironment = RANDOM_PORT
)
class SpringbootLiquibaseApplicationTests {

//    @Test
//    @Sql({"/test-schema.sql", "/test-user-data.sql"})
//    public void userTest {
//        // execute code that relies on the test schema and test data
//    assert true;
//    }

}
