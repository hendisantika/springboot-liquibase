package com.hendisantika.springbootliquibase;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Testcontainers
@SpringBootTest(webEnvironment = RANDOM_PORT)
class SpringbootLiquibaseApplicationTests {

    @Container
    @ServiceConnection
    static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.4.0");

    @Autowired
    private JdbcClient jdbcClient;

    @Test
    void liquibaseAppliesEveryChangeSet() {
        var ids = jdbcClient.sql("SELECT ID FROM DATABASECHANGELOG ORDER BY ORDEREXECUTED")
                .query(String.class)
                .list();

        assertThat(ids).containsExactly("createTable", "insertTableAddresses", "insertTableUsers");
    }

    @Test
    void seedDataIsInsertedByTheMigrations() {
        var users = jdbcClient.sql("SELECT NAME FROM USERS ORDER BY ID")
                .query(String.class)
                .list();

        assertThat(users).containsExactly("Uzumaki Naruto", "Uchiha Sasuke");
    }

    @Test
    void everyUserPointsAtAnExistingAddress() {
        var orphans = jdbcClient.sql("""
                        SELECT COUNT(*) FROM USERS u
                        LEFT JOIN ADDRESSES a ON a.ID = u.ADDRESS
                        WHERE a.ID IS NULL
                        """)
                .query(Integer.class)
                .single();

        assertThat(orphans).isZero();
    }

}
