package io.evolution;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static io.evolution.Constants.*;

/**
 * Created by gonzal99 on 3/23/2016.
 */
public class csvParser {
    //CONSTANTS

    Iterable<CSVRecord> csvRecordIterable;
    File csvFile;
    Connection c;

    public csvParser(File csvFile, Connection c) throws IOException {
        Reader csvReader = new FileReader(csvFile);
        this.csvFile = csvFile;
        this.c = c;
        //define csv headers
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
    }

    public void iterateCsv() throws SQLException, InterruptedException {
        int i = 0;
        for (CSVRecord record : csvRecordIterable) {
            if (i > 0) {
                StringBuilder queryBuilder = new StringBuilder("INSERT INTO"+tableName+"VALUES (00,");
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


    }

}
