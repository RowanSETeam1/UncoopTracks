package io.evolution;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Research on 6/21/2016.
 */
public class Main {

    public static void main(String[] args) throws SQLException, IOException, CSVParserException {
        Controller controller = new Controller("new.csv", "244790009", "2010-04-17", "330");
    }
}
