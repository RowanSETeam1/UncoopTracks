package io.evolution;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import static io.evolution.Constants.*;
public class Main {

    public static void main(String[] args){
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
        } catch (Exception e) {
            System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
            e.printStackTrace();
            return;
        }
        try {
            Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:mydb", "SA", "");
            createTable(c);
        }catch (SQLException e){

        }

    }
    public static void createTable(Connection c) {
        try {
            PreparedStatement create = c.prepareStatement("CREATE TABLE PUBLIC.AISDATA\n" +
                    "(ID INTEGER,\n" +
                    DATETIME+" VARCHAR(25),\n" +
                    MMSI+" VARCHAR(25),\n" +
                    LAT+" FLOAT,\n" +
                    LONG+" FLOAT,\n" +
                    COURSE+" FLOAT,\n" +
                    SPEED+" FLOAT,\n" +
                    HEADING+" INTEGER,\n" +
                    IMO+" VARCHAR(25),\n" +
                    NAME+" VARCHAR(50),\n" +
                    CALLSIGN+" VARCHAR(25),\n" +
                    AISTYPE+" VARCHAR(5),\n" +
                    A+" INTEGER,\n" +
                    B+" INTEGER,\n" +
                    C+" INTEGER,\n" +
                    D+" INTEGER,\n" +
                    DRAUGHT+" FLOAT,\n" +
                    DESTINATION+" VARCHAR(25),\n" +
                    ETA+" VARCHAR(25));");
            create.execute();
        }catch(SQLException e){

        }
    }
}


/**
 * Josh's Controller below
 */
//    public static void main(String[] args) throws SQLException, IOException, InterruptedIOException, InterruptedException {
//        // accepting argument and checking if it is a integer
//        // then displays the integer to verify the MMSI entered
//        int mmsi = 0;
//        if (args.length > 0) {
//            try {
//                mmsi = Integer.parseInt(args[0]);
//            } catch (NumberFormatException e) {
//                System.err.println("Argument" + args[0] + " must be an integer.");
//                System.err.println("Please Enter a MMSI Number");
//                System.exit(1);
//            }
//        }
//        System.out.println("MMSI entered: "+mmsi);
//
//        // Creating database and setting a connection to database to pass around
//        // modules
//        try {
//            Class.forName("org.hsqldb.jdbc.JDBCDriver");
//        } catch (Exception e) {
//            System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
//            e.printStackTrace();
//            return;
//        }
//        Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:mydb", "SA", "");
//        PreparedStatement create = c.prepareStatement("CREATE TABLE PUBLIC.AISDATA\n" +
//                "(ID INTEGER,\n" +
//                "DATETIME VARCHAR(25),\n" +
//                "MMSI VARCHAR(25),\n" +
//                "LATITUDE FLOAT,\n" +
//                "LONGITUDE FLOAT,\n" +
//                "COURSE FLOAT,\n" +
//                "SPEED FLOAT,\n" +
//                "HEADING INTEGER,\n" +
//                "IMO VARCHAR(25),\n" +
//                "NAME VARCHAR(50),\n" +
//                "CALLSIGN VARCHAR(25),\n" +
//                "AISTYPE VARCHAR(5),\n" +
//                "A INTEGER,\n" +
//                "B INTEGER,\n" +
//                "C INTEGER,\n" +
//                "D INTEGER,\n" +
//                "DRAUGHT FLOAT,\n" +
//                "DESTINATION VARCHAR(25),\n" +
//                "ETA VARCHAR(25));");
//        create.execute();
//        csvParser p = new csvParser(new File("vessels-movements-report.csv"),c);
//        p.iterateCsv();
//
//        //initiating modules, also pass the database connection to them
//        // Area Prediction Algorithm takes db, ship MMSI number, and time after signal loss
//        AreaPredictor areaPredict = new AreaPredictor(c, MMSI);
//        KMLGenerator kmlGen = new KMLGenerator(c);  // KML generator
//
//        //to grab xml file
//        //File file = new File(“Insert Path to file here\AIS_DATA.xml”); // sets the file as xml file
//
//        // executes the methods needed
//        boolean check = execute(areaPredict, kmlGen);
//
//    }
//
//    /**
//     * execute() method will execute all of the module methods needed.
//     */
//    private static boolean execute(AreaPredictor algo, KMLGenerator kmlGen){
//        // flag to determine errors
//        boolean flag = true;
//        // runs area predictor algorithm
//        flag = algo.execute();
//        // check areaPredict ran with no errors
//        if (flag = !true){
//            System.err.println("areaPredict Error");
//        }
//        // runs the kml generator
//        flag = kmlGen.generate();
//        // check if kmlGen ran with no errors
//        if (flag = !true){
//            System.err.println("kmlGen Error");
//        }
//        return flag;
//    }
