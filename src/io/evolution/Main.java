package io.evolution;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.IllegalFormatException;

import static io.evolution.Constants.*;

public class Main {
    static Connection c;
    static String csv = "H:\\IdeaProjects\\UncoopTracks\\csv.csv";
    static String mmsi = "366238710";
    static String time = "30";
    static String date = "03-14-2016";
    static csvParser p;

    public static void main(String[] args) throws SQLException, IOException {
        boolean database = createDatabase();
        if(database != true)
        {
            System.err.println("Does not compute");
            System.exit(1);
        }

        //parseArgs(args);
        p = new csvParser(new File(csv), c);
        p.iterateCsv();

        //initiating modules, also pass the database connection to them
        // Area Predi ction Algorithm takes db, ship MMSI number, and time after signal loss
        AreaPredictor areaPredict = new AreaPredictor(c, mmsi, date);
        KmlGenerator kmlGen = new KmlGenerator();  // KML generator

        //to grab xml file
        //File file = new File(“Insert Path to file here\AIS_DATA.xml”); // sets the file as xml file

        // executes the methods needed
        //boolean check =
        execute(areaPredict, kmlGen);

    }

    public static boolean parseArgs(String args[]) {
        if (args.length == 4) {
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
                date = args[2];
            } catch (IllegalFormatException t) {
                System.err.println("IllegalFormatException: Please enter Date");
                System.exit(1);
            }

            try {
                time = args[3];
            } catch (IllegalFormatException t) {
                System.err.println("IllegalFormatException: Please enter Time");
                System.exit(1);
            }

//            try {
//                endDate = args[4];
//            } catch (IllegalFormatException t) {
//                System.err.println("IllegalFormatException: Please enter End Date");
//                System.exit(1);
//            }
//
//            try {
//                startDate = args[5];
//            } catch (IllegalFormatException t) {
//                System.err.println("IllegalFormatException: Please enter End Time");
//                System.exit(1);
//            }

        } else {
            System.err.println("Invalid Number of Arguements.");
            System.err.println("Please Enter in the following order:");
            System.err.println("CSV Filename, MMSI Number, Date, Time");
            System.exit(1);
        }
        System.out.println("CSV entered: " + csv);
        System.out.println("MMSI entered: " + mmsi);
        System.out.println("Date entered: " + date);
        System.out.println("Time entered: " + time);
//        System.out.println("End Date entered: " + endDate);
//        System.out.println("End Time entered: " + endTime);
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
            //create AIS Data table
            PreparedStatement createAisDataTable = c.prepareStatement("CREATE TABLE PUBLIC.AISDATA\n" +
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
            createAisDataTable.execute();
            //creates database for kmlGenerator
            PreparedStatement createdKmlGeneratorTable = c.prepareStatement("CREATE TABLE PUBLIC.KMLPOINTS ("+DATETIME+" VARCHAR (255), "+
                    LAT+" FLOAT, "+LONG+" FLOAT);");
            createdKmlGeneratorTable.execute();
        } catch (SQLException e) {

        }
    }

    private static void execute(AreaPredictor algo, KmlGenerator kmlGen) throws IOException, SQLException {
        // flag to determine errors
        boolean flag = true;
        // runs area predictor algorithm
        //flag = algo.execute();
        // check areaPredict ran with no errors
        if (flag = !true) {
            System.err.println("areaPredict Error");
        }
        // runs the kml generator
        kmlGen.pull(c);
        kmlGen.generate();
        // check if kmlGen ran with no errors
        //if (flag = !true) {
        //    System.err.println("kmlGen Error");
       // }
      //  return flag;
    }
}

