package io.evolution;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Research on 6/24/2016.
 * THis class takes in a 2d array and generates a json file as an output
 * list properties :
 * 0:name
 * 1:coordinates
 * 2:winds
 * 3:wave height
 * 4:wave period
 * 5:wave direction
 * 6:water temperature
 */
public class JsonGenerator {

    private String[][] graph; //2d array containing all items
    private int x;
    private int y;
    private String[] properties = new String[]{"station", "coordinates", "datetime", "winds", "atmospheric pressure", "air temperature", "dew Point", "wave height", "wave period", "wave direction", "water temperature"};
    private JsonGenerator(int x, int y, String[][] graph) {
        this.x = x;
        this.y = y;
        this.graph = graph;


    }

    public static void main(String[] args) throws IOException {
        Scraper scraper = new Scraper();
        scraper.download("http://www.ndbc.noaa.gov/kml/marineobs_as_kml.php?sort=owner","testfile.kml");
        Parser p = new Parser("testfile.kml");
        JsonGenerator gen = new JsonGenerator(p.table.length, p.table[0].length, p.table);
        String str = gen.generate();
        gen.makeFile(str);
        System.out.println(str);

    }

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
