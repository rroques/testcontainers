package nz.co.rroques.testcontainers;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLTest {

    private static final Logger LOG = LogManager.getLogger(MySQLTest.class);

    @Rule
    public static MySQLContainer mySQLContainer = new MySQLContainer();

    private static final Flyway flyway = new Flyway();

    @BeforeAll
    public static void beforeAll() {
        mySQLContainer.start();
        flyway.setDataSource(mySQLContainer.getJdbcUrl(), mySQLContainer.getUsername(), mySQLContainer.getPassword());
        flyway.setLocations("nz/co/rroques/testcontainers");
        flyway.migrate();
    }

    @AfterAll
    public static void afterAll() {
        mySQLContainer.stop();
    }

    @Test
    public void test() {
        LOG.info(mySQLContainer.getDatabaseName());
        LOG.info(mySQLContainer.getJdbcUrl());

        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setUrl(mySQLContainer.getJdbcUrl());
        mysqlDataSource.setUser(mySQLContainer.getUsername());
        mysqlDataSource.setPassword(mySQLContainer.getPassword());

        try (
                Connection connection = mysqlDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM Events")
        ) {

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                System.out.println(resultSet.getInt(1));
            }

        } catch (SQLException e) {
            LOG.error(e);
        }
    }
}
