package io.evolution;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static io.evolution.Constants.*;

/**
 * Created by gonzal99 on 3/23/2016.
 */
public class csvParser {


    Iterable<CSVRecord> csvRecordIterable;
    File csvFile;
    Connection c;

    public csvParser(File csvFile, Connection c) {
        this.csvFile = csvFile;
        this.c = c;

        if(readFile()){
            try {
                iterateCsv();
            } catch (SQLException e) {
                System.out.println("no file");
                e.printStackTrace();
            }
        }
    }

    public boolean readFile() {
        try {
            Reader csvReader = new FileReader(csvFile);
            if (initParser(csvReader) == false) {
                return false;
            }
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            return false;
        }
        return true;
    }

    public boolean initParser(Reader csvReader) {
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

    public boolean iterateCsv() throws SQLException {
        int i = 0;
        try {
            for (CSVRecord record : csvRecordIterable) {
                if (i > 0) {
                    StringBuilder queryBuilder = new StringBuilder("INSERT INTO " + tableName + " VALUES (00,");
                    queryBuilder.append("'" + record.get(DATETIME) + "'");
                    queryBuilder.append(",");
                    queryBuilder.append("'" + record.get(MMSI) + "'");
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
                    queryBuilder.append("'" + record.get(IMO) + "'");
                    queryBuilder.append(",");
                    queryBuilder.append("'" + record.get(NAME) + "'");
                    queryBuilder.append(",");
                    queryBuilder.append("'" + record.get(CALLSIGN) + "'");
                    queryBuilder.append(",");
                    queryBuilder.append("'" + record.get(AISTYPE) + "'");
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
                    queryBuilder.append("'" + record.get(DESTINATION) + "'");
                    queryBuilder.append(",");
                    queryBuilder.append("'" + record.get(ETA) + "'");
                    queryBuilder.append(")");
                    PreparedStatement insertData = c.prepareStatement(queryBuilder.toString());
                    insertData.execute();
                }
                i++;
            }
            PreparedStatement get = c.prepareStatement("SELECT * FROM aisData WHERE DATETIME LIKE '%2016-03-14%';");
            ResultSet resultSet = get.executeQuery();
            while (resultSet.next()) {
                System.out.println(resultSet.getString("DATETIME"));
                System.out.println("\n");
            }
        } catch (SQLException e) {
            System.out.println("didnt work");
            return false;
        }
        return true;
    }

}
