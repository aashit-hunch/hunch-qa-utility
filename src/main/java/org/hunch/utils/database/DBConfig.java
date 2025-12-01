package org.hunch.utils.database;

import lombok.Data;

@Data
public class DBConfig {
    private String host;
    private int port;
    private String username;
    private String password;
    private String database;

    public DBConfig() {}

    public DBConfig(String host, int port, String username, String password, String database) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
    }
}
