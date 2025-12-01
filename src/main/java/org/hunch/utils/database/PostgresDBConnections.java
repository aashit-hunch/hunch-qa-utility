package org.hunch.utils.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.log4j.Logger;

import org.hunch.utils.CryptoUtility;

import java.sql.Connection;
import java.sql.SQLException;

public class PostgresDBConnections {
    private HikariDataSource dataSource;
    private static final Logger LOGGER = Logger.getLogger(PostgresDBConnections.class);

    public void makeConnection(DBConfig dbConfig) {
        HikariConfig config = new HikariConfig();
        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s",
                dbConfig.getHost(),
                dbConfig.getPort(),
                dbConfig.getDatabase());

        config.setJdbcUrl(jdbcUrl);
        config.setUsername(dbConfig.getUsername());
        config.setPassword(dbConfig.getPassword());

        // Connection pool settings
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(30000);
        config.setMaxLifetime(1800000);

        // Performance optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        this.dataSource = new HikariDataSource(config);
        LOGGER.info("DB Connection CREATED Successfully for DEV Database !");

    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource not initialized. Call makeConnection() first.");
        }
        return dataSource.getConnection();
    }

    public void closePool(String... env) {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
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

    public boolean isPoolActive() {
        return dataSource != null && !dataSource.isClosed();
    }


}
