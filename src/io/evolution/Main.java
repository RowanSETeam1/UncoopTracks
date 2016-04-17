package io.evolution;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.IllegalFormatException;

import static io.evolution.Constants.*;

/**
 * The main will accept  4 arguments, which are the CSV filename
 * containing AIS data set, MMSI#, Time since last known AIS signal
 * , and Date of last AIS signal.
 */
public class Main {
    static Connection dbConnection;
    static String csv = "H:\\IdeaProjects\\UncoopTracks\\csv.csv";
    static String mmsi = "229206000";
    static String time = "30";
    static String date = "2016-03-14";
    static csvParser parse;

    /**
     * Creates all needed modules (AreaPredictor, KMLGenerator) and gives
     * each module the needed variables.
     *
     * Runs execute() function  at the end which handles methods
     * called within modules.
     *
     * @param args CSV Filename, MMSI, Time, Date
     * @throws SQLException
     * @throws IOException
     * @throws CSVParserException
     */
    public static void main(String[] args) throws SQLException, IOException, CSVParserException {
        boolean database = createDatabase();
        if(database != true)
        {
            System.err.println("Does not compute");
            System.exit(1);
        }

        //parseArgs(args);
        parse = new csvParser(new File(csv), dbConnection);
        parse.iterateCsv();

        //initiating modules, also pass the database connection to them
        // Area Prediction Algorithm takes db, ship MMSI number, and time after signal loss
        AreaPredictor areaPredict = new AreaPredictor(dbConnection, mmsi, date, time);

        //to grab xml file
        KmlGenerator kmlGen = new KmlGenerator();  // KML generator
        //File file = new File(“Insert Path to file here\AIS_DATA.xml”); // sets the file as xml file

        // executes the methods needed
        execute(areaPredict, kmlGen);

    }

    /**
     * Will check if all of the inputs are in proper format and will
     * return an error if any arguements are not in correct format
     *
     * @param args CSV Filename, MMSI, Time, Date
     * @return check
     */
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
        return true;
    }

    /**
     * Creates the database that will hold the AIS data set that was parsed from
     * the CSV file.
     *
     * @return check
     */
    public static boolean createDatabase() {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
        } catch (Exception e) {
            System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
            e.printStackTrace();
            return false;
        }
        try {
            dbConnection = DriverManager.getConnection("jdbc:hsqldb:mem:mydb", "SA", "");
            createTable(dbConnection);
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

    /**
     * This method will execute the needed methods from in their specific
     * order. If any of method returns false an error will be returned specifying
     * which module did not function correctly.
     *
     * @param algo Area predictor algorithm
     * @param kmlGen kml generating algorithm
     * @return boolean check
     */
    private static void execute(AreaPredictor algo, KmlGenerator kmlGen) throws IOException, SQLException {
        // flag to determine errors
        //boolean flag = true;
        // runs area predictor algorithm
        algo.execute();
        // check areaPredict ran with no errors
       // if (flag = !true) {
        System.err.println("areaPredict Error");
      //  }
        // runs the kml generator
        kmlGen.pull(dbConnection);
        kmlGen.generate();
        // check if kmlGen ran with no errors
        //if (flag = !true) {
        //    System.err.println("kmlGen Error");
       // }
      //  return flag;
    }
}

