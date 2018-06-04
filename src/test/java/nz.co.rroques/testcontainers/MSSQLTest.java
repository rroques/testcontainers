package nz.co.rroques.testcontainers;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MSSQLServerContainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MSSQLTest {

    private static final Logger LOG = LogManager.getLogger(MySQLTest.class);

    @Rule
    public static MSSQLServerContainer mssqlServerContainer = new MSSQLServerContainer();

    private static final Flyway flyway = new Flyway();

    @BeforeAll
    public static void beforeAll() {
        mssqlServerContainer.start();
        flyway.setDataSource(mssqlServerContainer.getJdbcUrl(), mssqlServerContainer.getUsername(), mssqlServerContainer.getPassword());
        flyway.setLocations("nz/co/rroques/testcontainers");
        flyway.migrate();
    }

    @AfterAll
    public static void afterAll() {
        mssqlServerContainer.stop();
    }

    @Test
    public void test() {
        LOG.info(mssqlServerContainer.getDatabaseName());
        LOG.info(mssqlServerContainer.getJdbcUrl());

        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setUrl(mssqlServerContainer.getJdbcUrl());
        mysqlDataSource.setUser(mssqlServerContainer.getUsername());
        mysqlDataSource.setPassword(mssqlServerContainer.getPassword());

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
