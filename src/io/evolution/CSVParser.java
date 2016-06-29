package io.evolution;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static io.evolution.Constants.*;

/**
 * Created by Roberto Gonzalez on 3/23/2016.
 */
public class CSVParser {

    //global variables
    private Iterable<CSVRecord> csvRecordIterable;
    private File csvFile;
    private Connection c;

    /**
     * Instantiates a new Csv parser.
     *
     * @param csvFile the csv file to be parsed
     * @param c       reference to the database connection
     */
    CSVParser(File csvFile, Connection c) throws CSVParserException {
        this.csvFile = csvFile;
        this.c = c;
        //Worst case exception handling
        if (csvFile == null || c == null || !readFile()) {
            throw new CSVParserException();
        }
    }

    /**
     * Instantiates the file reader and passes it to the parser initializer
     *
     * @return boolean false when file not found
     */
    private boolean readFile() {
        try {
            Reader csvReader = new FileReader(csvFile);
            if (!initParser(csvReader)) {
                return false;
            }
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            return false;
        }
        return true;
    }

    /**
     * Initializes the csv parser with predefined headers
     *
     * @param csvReader the csv reader object for the csvFile
     * @return false if ioException
     */
    private boolean initParser(Reader csvReader) {
        //defines the csv column headers to be referenced by name
        try {
            csvRecordIterable = CSVFormat.EXCEL.withHeader(Constants.DATETIME,
                    MMSI,
                    LAT,
                    LONG,
                    COURSE,
                    SPEED,
                    HEADING,
                    IMO,
                    NAME,
                    CALLSIGN,
                    AISTYPE,
                    A,
                    B,
                    C,
                    D,
                    DRAUGHT,
                    DESTINATION,
                    ETA).parse(csvReader);
        } catch (IOException e) {
            System.out.println("io exception");
            return false;
        }
        return true;
    }

    /**
     * Iterates over all csv records found in the file.
     *
     * @return false if an an SQLException is thrown
     */
    boolean iterateCsv() {
        int i = 0;
        try {
            /**
             * Iterates over each csv record to build an SQL query containing a given csv record's data
             */
            for (CSVRecord record : csvRecordIterable) {
                //this skips the first record, as it contains header data
                if (i > 0) {
                    //goes through each portion of the record and appends it to the string
                    StringBuilder queryBuilder = new StringBuilder("INSERT INTO " + tableName + " VALUES (00,");
                    queryBuilder.append("'").append(record.get(DATETIME)).append("'");
                    queryBuilder.append(",");
                    queryBuilder.append("'").append(record.get(MMSI)).append("'");
                    queryBuilder.append(",");
                    queryBuilder.append(Float.parseFloat(record.get(LAT)));
                    queryBuilder.append(",");
                    queryBuilder.append(Float.parseFloat(record.get(LONG)));
                    queryBuilder.append(",");
                    queryBuilder.append(Float.parseFloat(record.get(COURSE)));
                    queryBuilder.append(",");
                    queryBuilder.append(Float.parseFloat(record.get(SPEED)));
                    queryBuilder.append(",");
                    queryBuilder.append(Integer.parseInt(record.get(HEADING)));
                    queryBuilder.append(",");
                    queryBuilder.append("'").append(record.get(IMO)).append("'");
                    queryBuilder.append(",");
                    queryBuilder.append("'").append(record.get(NAME)).append("'");
                    queryBuilder.append(",");
                    queryBuilder.append("'").append(record.get(CALLSIGN)).append("'");
                    queryBuilder.append(",");
                    queryBuilder.append("'").append(record.get(AISTYPE)).append("'");
                    queryBuilder.append(",");
                    queryBuilder.append(Integer.parseInt(record.get(A)));
                    queryBuilder.append(",");
                    queryBuilder.append(Integer.parseInt(record.get(B)));
                    queryBuilder.append(",");
                    queryBuilder.append(Integer.parseInt(record.get(C)));
                    queryBuilder.append(",");
                    queryBuilder.append(Integer.parseInt(record.get(D)));
                    queryBuilder.append(",");
                    queryBuilder.append(Float.parseFloat(record.get(DRAUGHT)));
                    queryBuilder.append(",");
                    queryBuilder.append("'").append(record.get(DESTINATION)).append("'");
                    queryBuilder.append(",");
                    queryBuilder.append("'").append(record.get(ETA)).append("'");
                    queryBuilder.append(")");
                    //Prepares the statement to be executed, then executes the statement to input into the database
                    PreparedStatement insertData = c.prepareStatement(queryBuilder.toString());
                    insertData.execute();
                }
                i++;
            }
        } catch (SQLException e) {
            System.out.println("problem with sql");
            return false;
        }
        return true;
    }


}
