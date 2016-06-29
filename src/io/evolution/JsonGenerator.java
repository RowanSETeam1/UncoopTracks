package io.evolution;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Research on 6/24/2016.
 * This class takes in a 2d array and generates a json file as an output
 * list properties :
 * 0:"station"
 * 1:"coordinates"
 * 2:"datetime"
 * 3:"winds"
 * 4:"atmospheric pressure"
 * 5:"air temperature"
 * 6:"dew Point"
 * 7:"wave height"
 * 8:"wave period"
 * 9:"wave direction"
 * 10:"water temperature"
 */
public class JsonGenerator {

    private String[][] graph; //2d array containing all items
    private int x;
    private int y;
    private String[] properties = new String[]{"station", "coordinates", "datetime", "winds", "atmospheric pressure", "air temperature", "dew Point", "wave height", "wave period", "wave direction", "water temperature"};

    /**
     * constructor
     * takes in values for x and y
     * @param x
     * @param y
     * @param graph
     */
    private JsonGenerator(int x, int y, String[][] graph) {
        this.x = x;
        this.y = y;
        this.graph = graph;


    }

    /**
     * main.
     *
     * @param args the input arguments
     * @throws IOException the io exception
     */
    public static void main(String[] args) throws IOException {
        Scraper scraper = new Scraper();
        scraper.download("http://www.ndbc.noaa.gov/kml/marineobs_as_kml.php?sort=owner","testfile.kml");
        Parser p = new Parser("testfile.kml");
        JsonGenerator gen = new JsonGenerator(p.table.length, p.table[0].length, p.table);
        String str = gen.generate();
        gen.makeFile(str);
        System.out.println(str);

    }

    /**
     * This method generates the content of the file with the appropriate JSON formatting
     * returns a string
     * @return
     */
    private String generate() {
        System.out.println("Json Gen: Start");
        System.out.println("Json Gen: Formatting given info");
        String content = "{\"bouy\":[\n";
        for (int i = 0; i < y; i++) {
            content += "{";
            for (int j = 0; j < x; j++) {
                content += "\"" + properties[j] + "\"" + ": " + "\"" + graph[j][i] + "\"";
                if (j != (properties.length - 1)) {
                    content += ",";
                }
            }
            content += "}";
            if (!(i == (y - 1))) {
                content += ",\n";
            }
        }
        content += "]\n}";


        System.out.println("Json Gen: Formatting done");
        return content;

    }

    /**
     * This method creates a file containing the previously data formatted
     * it overwrites the file if it already exists
     * @param content
     * @throws IOException
     */
    private void makeFile(String content) throws IOException {
        System.out.println("Json Gen: writing to file");
        File file = new File("BouyData.JSON");
        FileOutputStream stream = new FileOutputStream(file, false); // true to append
        // false to overwrite.
        byte[] myBytes = content.getBytes();
        stream.write(myBytes);
        stream.close();
        System.out.println("Json Gen: End");
    }
}
