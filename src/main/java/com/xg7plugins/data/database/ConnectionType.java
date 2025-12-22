package com.xg7plugins.data.database;


import lombok.Getter;

/**
 * Enum representing different types of database connections along with their driver class names.
 */
@Getter
public enum ConnectionType {

    SQLITE("org.sqlite.JDBC"),
    MYSQL("com.mysql.cj.jdbc.Driver"),
    MARIADB("org.mariadb.jdbc.Driver");

    private final String driverClassName;

    ConnectionType(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    /**
     * Checks if the JDBC driver for this connection type is loaded.
     *
     * @return true if the driver is loaded, false otherwise
     */
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
