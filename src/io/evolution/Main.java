package io.evolution;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.IllegalFormatException;

import static io.evolution.Constants.*;

public class Main {
    static Connection c;
    static String csv = "";
    static String mmsi = "";
    static String startTime = "";
    static String endTime = "";
    static String startDate = "";
    static String endDate = "";
    static csvParser p;

    public static void main(String[] args) throws SQLException {


        parseArgs(args);
        p = new csvParser(new File(csv), c);
        p.iterateCsv();

        //initiating modules, also pass the database connection to them
        // Area Predi ction Algorithm takes db, ship MMSI number, and time after signal loss
        AreaPredictor areaPredict = new AreaPredictor(c, mmsi, startDate, startTime, endDate, endTime);
        KmlGenerator kmlGen = new KmlGenerator();  // KML generator

        //to grab xml file
        //File file = new File(“Insert Path to file here\AIS_DATA.xml”); // sets the file as xml file

        // executes the methods needed
        boolean check = execute(areaPredict, kmlGen);

    }

    public static boolean parseArgs(String args[]) {
        if (args.length == 6) {
            try {
                csv = args[0];
            } catch (IllegalFormatException s) {
                System.err.println("IllegalFormatException: Please enter a Filename");
                System.exit(1);
            }

            try {
                mmsi = args[1];
            } catch (NumberFormatException n) {
                System.err.println("NumberFormatException: Please enter an MMSI  number");
                System.exit(1);
            }

            try {
                startDate = args[2];
            } catch (IllegalFormatException t) {
                System.err.println("IllegalFormatException: Please enter Start Date");
                System.exit(1);
            }

            try {
                startTime = args[3];
            } catch (IllegalFormatException t) {
                System.err.println("IllegalFormatException: Please enter Start Time");
                System.exit(1);
            }

            try {
                endDate = args[4];
            } catch (IllegalFormatException t) {
                System.err.println("IllegalFormatException: Please enter End Date");
                System.exit(1);
            }

            try {
                startDate = args[5];
            } catch (IllegalFormatException t) {
                System.err.println("IllegalFormatException: Please enter End Time");
                System.exit(1);
            }

        } else {
            System.err.println("Invalid Number of Arguements.");
            System.err.println("Please Enter in the following order:");
            System.err.println("CSV Filename, MMSI Number, Start Date, Start Time, End Date, End Time");
            System.exit(1);
        }
        System.out.println("CSV entered: " + csv);
        System.out.println("MMSI entered: " + mmsi);
        System.out.println("Start Date entered: " + startDate);
        System.out.println("Start Time entered: " + startTime);
        System.out.println("End Date entered: " + endDate);
        System.out.println("End Time entered: " + endTime);
        return true;
    }

    public static boolean createDatabase() {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
        } catch (Exception e) {
            System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
            e.printStackTrace();
            return false;
        }
        try {
            c = DriverManager.getConnection("jdbc:hsqldb:mem:mydb", "SA", "");
            createTable(c);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    /**
     * Defines the aisData table headers
     *
     * @param c Connection to JDBC datatbase
     */
    public static void createTable(Connection c) {
        try {
            PreparedStatement create = c.prepareStatement("CREATE TABLE PUBLIC.AISDATA\n" +
                    "(ID INTEGER,\n" +
                    DATETIME + " VARCHAR(25),\n" +
                    MMSI + " VARCHAR(25),\n" +
                    LAT + " FLOAT,\n" +
                    LONG + " FLOAT,\n" +
                    COURSE + " FLOAT,\n" +
                    SPEED + " FLOAT,\n" +
                    HEADING + " INTEGER,\n" +
                    IMO + " VARCHAR(25),\n" +
                    NAME + " VARCHAR(50),\n" +
                    CALLSIGN + " VARCHAR(25),\n" +
                    AISTYPE + " VARCHAR(5),\n" +
                    A + " INTEGER,\n" +
                    B + " INTEGER,\n" +
                    C + " INTEGER,\n" +
                    D + " INTEGER,\n" +
                    DRAUGHT + " FLOAT,\n" +
                    DESTINATION + " VARCHAR(25),\n" +
                    ETA + " VARCHAR(25));");
            create.execute();
        } catch (SQLException e) {

        }
    }

    private static boolean execute(AreaPredictor algo, KmlGenerator kmlGen) {
        // flag to determine errors
        boolean flag = true;
        // runs area predictor algorithm
        //flag = algo.execute();
        // check areaPredict ran with no errors
        if (flag = !true) {
            System.err.println("areaPredict Error");
        }
        // runs the kml generator
        //flag = kmlGen.generate();
        // check if kmlGen ran with no errors
        if (flag = !true) {
            System.err.println("kmlGen Error");
        }
        return flag;
    }
}


/**
 * Josh's Controller below
 */
// accepting argument and checking if there is 6 arguments
// Checks if all arguements are in fact what they are (checks if string is string int is int)
// then displays the integer to verify the inputs
//    String csv = "";
//    int mmsi = 0;
//    String startTime = "";
//    String endTime = "";
//    String startDate = "";
//    String endDate = "":
//
//        if (args.length == 6) {
//        try {
//        csv = args[0];
//        } catch (IllegalFormatException s) {
//        System.err.println("IllegalFormatException: Please enter a Filename");
//        System.exit(1);
//        }
//
//        try {
//        mmsi = Integer.parseInt(args[1]);
//        } catch (NumberFormatException n) {
//        System.err.println("NumberFormatException: Please enter an MMSI  number");
//        System.exit(1);
//        }
//
//        try {
//        startDate = args[2];
//        } catch (IllegalFormatException t) {
//        System.err.println("IllegalFormatException: Please enter Start Date");
//        System.exit(1);
//        }
//
//        try {
//        startTime = args[3];
//        } catch (IllegalFormatException t) {
//        System.err.println("IllegalFormatException: Please enter Start Time");
//        System.exit(1);
//        }
//
//        try {
//        endDate = args[4];
//        } catch (IllegalFormatException t) {
//        System.err.println("IllegalFormatException: Please enter End Date");
//        System.exit(1);
//        }
//
//        try {
//        startDate = args[5];
//        } catch (IllegalFormatException t) {
//        System.err.println("IllegalFormatException: Please enter End Time");
//        System.exit(1);
//        }
//
//        } else {
//        System.err.println("Invalid Number of Arguements.");
//        System.err.println("Please Enter in the following order:");
//        System.err.println("CSV Filename, MMSI Number, Start Date, Start Time, End Date, End Time"
//        System.exit(1);
//        }
//        System.out.println("CSV entered: "+csv);
//        System.out.println("MMSI entered: "+mmsi);
//        System.out.println("Start Date entered: "+startDate);
//        System.out.println("Start Time entered: "+startTime);
//        System.out.println("End Date entered: "+endDate);
//        System.out.println("End Time entered: "+endTime);
//
//        // Creating database and setting a connection to database to pass around
//        // modules
//        try {
//        Class.forName("org.hsqldb.jdbc.JDBCDriver");
//        } catch (Exception e) {
//        System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
//        e.printStackTrace();
//        return;
//        }
//        Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:mydb", "SA", "");
//        PreparedStatement create = c.prepareStatement("CREATE TABLE PUBLIC.AISDATA\n" +
//        "(ID INTEGER,\n" +
//        "DATETIME VARCHAR(25),\n" +
//        "MMSI VARCHAR(25),\n" +
//        "LATITUDE FLOAT,\n" +
//        "LONGITUDE FLOAT,\n" +
//        "COURSE FLOAT,\n" +
//        "SPEED FLOAT,\n" +
//        "HEADING INTEGER,\n" +
//        "IMO VARCHAR(25),\n" +
//        "NAME VARCHAR(50),\n" +
//        "CALLSIGN VARCHAR(25),\n" +
//        "AISTYPE VARCHAR(5),\n" +
//        "A INTEGER,\n" +
//        "B INTEGER,\n" +
//        "C INTEGER,\n" +
//        "D INTEGER,\n" +
//        "DRAUGHT FLOAT,\n" +
//        "DESTINATION VARCHAR(25),\n" +
//        "ETA VARCHAR(25));");
//        create.execute();
//        csvParser p = new csvParser(new File(csv),c);
//        p.iterateCsv();
//
//        //initiating modules, also pass the database connection to them
//        // Area Prediction Algorithm takes db, ship MMSI number, and time after signal loss
//        AreaPredictor areaPredict = new AreaPredictor(c, MMSI, startDate, startTime, endDate, endTime);
//        KMLGenerator kmlGen = new KMLGenerator(c);  // KML generator
//
//        //to grab xml file
//        //File file = new File(“Insert Path to file here\AIS_DATA.xml”); // sets the file as xml file
//
//        // executes the methods needed
//        boolean check = execute(areaPredict, kmlGen);
//
//        }
//
///**
// * execute() method will execute all of the module methods needed.
// */
//private static boolean execute(AreaPredictor algo, KMLGenerator kmlGen){
//        // flag to determine errors
//        boolean flag = true;
//        // runs area predictor algorithm
//        flag = algo.execute();
//        // check areaPredict ran with no errors
//        if (flag = !true){
//        System.err.println("areaPredict Error");
//        }
//        // runs the kml generator
//        flag = kmlGen.generate();
//        // check if kmlGen ran with no errors
//        if (flag = !true){
//        System.err.println("kmlGen Error");
//        }
//        return flag;
//        }
