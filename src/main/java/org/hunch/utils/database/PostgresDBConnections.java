package org.hunch.utils.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.log4j.Logger;

import org.hunch.constants.GlobalData;
import org.hunch.utils.CryptoUtility;


import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public class PostgresDBConnections {
    private HikariDataSource dataSource;
    private static final Logger LOGGER = Logger.getLogger(PostgresDBConnections.class);

    public void makeConnection(DBConfig dbConfig) {
        HikariConfig config = new HikariConfig();
        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s",
                dbConfig.getHost(), dbConfig.getPort(), dbConfig.getDatabase());

        config.setJdbcUrl(jdbcUrl);
        config.setUsername(dbConfig.getUsername());
        config.setPassword(dbConfig.getPassword());

        // Dynamic pool sizing based on thread count
        int threadCount = GlobalData.THREAD_COUNT > 0 ? GlobalData.THREAD_COUNT : 10;
        int poolSize = Math.max(10, threadCount + 5);

        config.setMaximumPoolSize(poolSize);
        config.setMinimumIdle(5);
        config.setIdleTimeout(600000);        // 10 minutes
        config.setConnectionTimeout(10000);    // 10 seconds
        config.setMaxLifetime(1800000);        // 30 minutes
        config.setKeepaliveTime(300000);       // 5 minutes

        // Connection leak detection
        config.setLeakDetectionThreshold(60000);  // Warn after 60s

        // Connection validation
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(3000);     // 3 seconds

        // PostgreSQL-specific optimizations
        config.addDataSourceProperty("prepareThreshold", "3");
        config.addDataSourceProperty("preferQueryMode", "extendedForPrepared");
        config.addDataSourceProperty("ApplicationName", "hunch-qa-utility");

        // Pool monitoring
        config.setRegisterMbeans(true);
        config.setPoolName("HunchQAPool");

        this.dataSource = new HikariDataSource(config);
        LOGGER.info(String.format("DB Connection pool created: size=%d, threads=%d",
                poolSize, threadCount));
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource not initialized. Call makeConnection() first.");
        }
        return dataSource.getConnection();
    }

    public void closePool(String... env) {
        if (dataSource != null && !dataSource.isClosed()) {
            LOGGER.info("Closing HikariCP connection pool...");

            try {
                // Close the datasource with a timeout
                dataSource.close();
                LOGGER.info("HikariCP datasource closed");
            } catch (Exception e) {
                LOGGER.error("Error closing datasource", e);
            }

            // Deregister JDBC drivers to allow PostgreSQL-JDBC-Cleaner thread to terminate
            deregisterJdbcDrivers();

            if (env.length > 0) {
                if (env[0].equalsIgnoreCase("PROD")){
                    LOGGER.info("DB Connection CLOSED Successfully for PROD Database ! ");
                }
                 else {
                    LOGGER.info("DB Connection CLOSED Successfully for DEV Database ! ");
                }
            }
            else {
                LOGGER.info("DB Connection CLOSED Successfully ! ");
            }
        }
    }

    /**
     * Deregister all JDBC drivers to allow background threads to terminate properly
     */
    private void deregisterJdbcDrivers() {
        try {
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                try {
                    DriverManager.deregisterDriver(driver);
                    LOGGER.info("Deregistered JDBC driver: " + driver.getClass().getName());
                } catch (SQLException e) {
                    LOGGER.error("Error deregistering driver: " + driver.getClass().getName(), e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error during JDBC driver deregistration", e);
        }
    }

    public boolean isPoolActive() {
        return dataSource != null && !dataSource.isClosed();
    }


}
