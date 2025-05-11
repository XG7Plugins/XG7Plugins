package com.xg7plugins.data.database;

public enum ConnectionType {

    SQLITE("org.sqlite.JDBC"),
    MYSQL("com.mysql.cj.jdbc.Driver"),
    MARIADB("org.mariadb.jdbc.Driver");

    private final String driver;

    ConnectionType(String driver) {
        this.driver = driver;
    }

    public boolean isDriverLoaded() {
        try {
            Class.forName(driver);
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
