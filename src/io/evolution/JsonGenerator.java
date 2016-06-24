package io.evolution;

import java.io.*;

/**
 * Created by Research on 6/24/2016.
 * THis class takes in a 2d array and generates a json file as an output
 * list properties :
 * 0:name
 * 1:location
 * 2:winds
 * 3:wave height
 * 4:wave period
 * 5:wave direction
 * 6:water temperature
 */
public class JsonGenerator {
    String [][] graph; //2d array containing all items
    public JsonGenerator(String [][] graph){
        this.graph = graph;
    }

    String generate(){
        String content="";



        return content;
    }

    void makeFile(String str) throws FileNotFoundException, UnsupportedEncodingException {
        File file = new File("BouyData.JSON");
        PrintWriter writer = new  PrintWriter (file, "UTF-8");
        writer.write(str);
        FileOutputStream output = new FileOutputStream(file, false);
    }
}
