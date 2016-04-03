package io.evolution;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
        } catch (Exception e) {
            System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
            e.printStackTrace();
            return;
        }
        Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:mydb", "SA", "");
        PreparedStatement create = c.prepareStatement("CREATE TABLE PUBLIC.AISDATA\n" +
                "(ID INTEGER,\n" +
                "DATETIME VARCHAR(25),\n" +
                "MMSI VARCHAR(25),\n" +
                "LATITUDE FLOAT,\n" +
                "LONGITUDE FLOAT,\n" +
                "COURSE FLOAT,\n" +
                "SPEED FLOAT,\n" +
                "HEADING INTEGER,\n" +
                "IMO VARCHAR(25),\n" +
                "NAME VARCHAR(50),\n" +
                "CALLSIGN VARCHAR(25),\n" +
                "AISTYPE VARCHAR(5),\n" +
                "A INTEGER,\n" +
                "B INTEGER,\n" +
                "C INTEGER,\n" +
                "D INTEGER,\n" +
                "DRAUGHT FLOAT,\n" +
                "DESTINATION VARCHAR(25),\n" +
                "ETA VARCHAR(25));");
        create.execute();
    }
}
