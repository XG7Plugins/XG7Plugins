package com.xg7plugins.data.database;


import lombok.Getter;

@Getter
public enum ConnectionType {

    SQLITE("org.sqlite.JDBC"),
    MYSQL("com.mysql.cj.jdbc.Driver"),
    MARIADB("org.mariadb.jdbc.Driver");

    private final String driverClassName;

    ConnectionType(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public boolean isDriverLoaded() {
        try {
            Class.forName(driverClassName);
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
