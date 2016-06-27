package io.evolution;

import java.io.*;

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

    public static void  main (String[] args) throws IOException {
        Parser p = new Parser("testfile.kml");
        JsonGenerator gen = new JsonGenerator(11,p.table.length, p.table);
        System.out.print(gen.generate());

    }


    String [][] graph; //2d array containing all items
    int x;
    int y;
    String[] properties = new String[] { "snippet","coordinates","datetime","winds","aPressure","atemperature","dewPoint","height","period","direction","temperature[i]"};
    public JsonGenerator(int x, int y, String [][] graph) {
        this.x= x;
        this.y= y;
        this.graph = graph;



    }
    String generate(){
        String content = "{\"bouy\":[\n";
        for (int i = 0; i < y; i++) {
            content += "{";
            for (int j = 0; j < x; j++) {
               content +=  "\""+properties[j]+"\""+ ": " + "\""+graph[j][i]+"\"";
                if(j != (properties.length - 1)){
                    content+=",";
                }
            }
            content +="}";
            if(i != (y - 1)){
                content+=",\n";
            }
        }
        content += "]\n}";



        return content;
    }

    void makeFile(String str) throws FileNotFoundException, UnsupportedEncodingException {
        File file = new File("BouyData.JSON");
        PrintWriter writer = new  PrintWriter (file, "UTF-8");
        writer.write(str);
        FileOutputStream output = new FileOutputStream(file, false);
    }
}
