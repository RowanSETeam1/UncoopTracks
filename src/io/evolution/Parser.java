package io.evolution;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import static jdk.nashorn.internal.runtime.JSONFunctions.parse;

/**
 * Created by Eliakah Kakou on 6/21/2016.
 * This class reads a KML file
 * Parses through its tags
 * creates a csv file where the information from the tags classified
 */
public class Parser {
    String[] table ={"",""};

    public static void main(String [] args) throws IOException {
        Parser p = new Parser("testfile.kml", "description");
    }

    private File file; //kml file to be read
    private String tag; //tag who's content we're grabbing


    /**
     * Parameter
     * it takes the file name and the tag to extract
     * @param file
     * @param tag
     * @throws IOException
     */
    public Parser(String file, String tag ) throws IOException {

        this.file = new File(file);
        this.tag = tag;
        ArrayList<String> list = getTagContent(getPlacemarkContent());
        for (int i = 0; i < list.size(); i++) {
            System.out.println("No\t"+i+":\t"+list.get(i));
            System.out.println();
        }

    }


    /**
     * This method scans the file for all the placemarks and stores their
     * individual content as an item in the list
     * @return Arraylist of Strings (Placemark content)
     * @throws IOException
     */
    ArrayList<String> getPlacemarkContent  () throws IOException {
        ArrayList<String> list = new ArrayList<>();
        String text = "";
        boolean check = false;
        String lineFromFile = "";
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            text = "";
            lineFromFile = scanner.nextLine(); //puts line in String
            if (lineFromFile.contains("<Placemark>")) { //check if line contains tag
                check = true;
                while (scanner.hasNextLine()) {
                    lineFromFile = scanner.nextLine();
                    if (lineFromFile.contains("</Placemark>")) {
                        break;
                    }else{

                        text +="\n"+lineFromFile;}
                }
                list.add(text);


            }


        }


        return list;
    }


    /**
     * This method scans each item in the list and extracts the content for the specified tag
     * @return Arraylist of Strings (tag content)
     * @throws IOException
     */
    ArrayList<String> getTagContent  (ArrayList<String> list) throws IOException {
        ArrayList<String> fList = new ArrayList<>();
        String lineFromFile = "";
        Scanner scanner;
        for (int i = 0; i <list.size(); i++) {
            scanner = new Scanner(list.get(i));
            while (scanner.hasNextLine()) {
                //get it there
                lineFromFile = scanner.nextLine(); //puts line in String
                if (lineFromFile.contains("<" + tag + ">")) { //check if line contains tag
                    while(true){//keep adding to string while closing tag not contained
                        if (lineFromFile.contains("</" + tag + ">"))
                            break;
                        lineFromFile += scanner.nextLine();
                    }
                    lineFromFile = rmString(rmString(lineFromFile, "<" + tag + ">"),"</" + tag + ">") ; //remove tags
                    fList.add(lineFromFile);//add to list
                }

            }
        }




      //  fList =  rmItemWith(fList, "No recent data");//remove items that won't be used
       // fList =  rmItemWith(fList, "height=\"220\" width=\"400\" alt=\"Five-day plot of water level at");//remove items that won't be used
       // fList =  rmItemWith(fList,"http://tao.ndbc.noaa.gov/refreshed/site.php?site=");//remove items that won't be used
        return fList;

    }

    private ArrayList<String> rmItemWith(ArrayList<String> rmList, String str){
        ArrayList<String> secondList = new ArrayList<>();
        for (int i = 0; i <rmList.size() ; i++) { //for all items
            if(!rmList.get(i).contains(str)) { //if the substring in string
                secondList.add(rmList.get(i)); //remove item
            }
        }




        return secondList;
    }


    /**
     * This method takes in a string and removes the substring specified
     * @param ogString
     * @param subString
     * @return
     */
    private String  rmString(String ogString, String subString){
        ogString = ogString.replace(subString,"");
        return ogString;
    }


    public void createJSONFile(){

    }

//TODO: makes sure you can open and download the kml file
    private static void open(String path, ArrayList<String> ar) {
        File file = new File(path);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(parse("this", parse("<Description>", line)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

