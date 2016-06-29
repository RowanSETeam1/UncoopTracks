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


    String[][] table;//this 2d array will contain all the information taken from the file
    private int index = 0; //index used to iterate through the 2d array
    private File file; //kml file to be read
    private String tag; //tag who's content we're grabbing
    private String[] datetime; //time value at each bouy
    private String[] winds; //winds value at each bouy
    private String[] aPressure; //athmospheric presure value at each bouy
    private String[] atemperature; //air temp value at each bouy
    private String[] dewPoint;//dew point value at each bouy
    private String[] height; //wave height value at each bouy
    private String[] period; //wave period value at each bouy
    private String[] direction; //direction value at each bouy
    private String[] temperature; //water temp value at each bouy

    /**
     * constructor
     * it takes the file name and the tag to extract
     * @param file the file
     * @throws IOException the io exception
     */
    Parser(String file) throws IOException {
        System.out.println("Parser: Start");
        System.out.println("Parser: opening file");
        this.file = new File(file); //assigns the file to private var
        System.out.println("Parser: getting tags content");
        this.tag = "Snippet";
        ArrayList<String> snippet = getTagContent(getPlacemarkContent());
        this.tag = "coordinates";
        ArrayList<String> coordinates = getTagContent(getPlacemarkContent());
        this.tag = "description";
        ArrayList<String> list = getTagContent(getPlacemarkContent());

        System.out.println("Parser: formatting content");
        //initializes all lists
        table = new String[11][list.size()];
        datetime = new String[list.size()];
        winds = new String[list.size()];
        aPressure = new String[list.size()];
        atemperature = new String[list.size()];
        dewPoint = new String[list.size()];
        height = new String[list.size()];
        period = new String[list.size()];
        direction = new String[list.size()];
        temperature = new String[list.size()];

        //strips all entries of unnecessary formatting
        for (int i = 0; i < list.size(); i++) {
            list.set(i, (rmString(list.get(i), "<b>")));
            list.set(i, (rmString(list.get(i), "</b>")));
            list.set(i, (rmString(list.get(i), "<![CDATA[")));
            list.set(i, (rmString(list.get(i), "(")));
            list.set(i, (rmString(list.get(i), ")")));
            list.set(i, (rmString(list.get(i), "\"]]>")));
            list.set(i, (rpStringwith(list.get(i), "<br />", "\n")));
            list.set(i, (rpStringwith(list.get(i), "&#", "<")));
            list.set(i, (rpStringwith(list.get(i), ";F", "°F")));
            list.set(i, (rmString(list.get(i), "]]>")));
        }

        System.out.println("Parser: storing content");
        //stores values from individual lists to single 2d array
        for (String aList : list) {
            storeInLists(aList);
        }
        int count;
        for (int i = 0; i < list.size(); i++) {
            count = 0;
            table[count][i] = snippet.get(i);
            table[count + 1][i] = coordinates.get(i);
            table[count + 2][i] = datetime[i];
            table[count + 3][i] = winds[i];
            table[count + 4][i] = aPressure[i];
            table[count + 5][i] = atemperature[i];
            table[count + 6][i] = dewPoint[i];
            table[count + 7][i] = height[i];
            table[count + 8][i] = period[i];
            table[count + 9][i] = direction[i];
            table[count + 10][i] = temperature[i];


        }

        System.out.println("Parser: End");
    }

    /**
     * main
     *
     * @param args the input arguments
     * @throws IOException the io exception
     */
    public static void main(String[] args) throws IOException {

        Parser p = new Parser("testfile.kml");
    }

    /**
     * Get table string [ ] [ ].
     *
     * @return the string [ ] [ ]
     */
    String[][] getTable() {
        return table;
    }

    /**
     * This method scans the file for all the placemarks and stores their
     * individual content as an item in the list
     *
     * @return Arraylist of Strings (Placemark content)
     * @throws IOException
     */
    private ArrayList<String> getPlacemarkContent() throws IOException {
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
                    } else {

                        text += "\n" + lineFromFile;
                    }
                }
                list.add(text);


            }


        }


        return list;
    }

    /**
     * This method scans each item in the list and extracts the content for the specified tag
     *
     * @return Arraylist of Strings (tag content)
     * @throws IOException
     */
    private ArrayList<String> getTagContent(ArrayList<String> list) throws IOException {
        ArrayList<String> fList = new ArrayList<>();
        String lineFromFile = "";
        Scanner scanner;
        for (String aList : list) {
            scanner = new Scanner(aList);
            while (scanner.hasNextLine()) {
                //get it there
                lineFromFile = scanner.nextLine(); //puts line in String
                if (lineFromFile.contains("<" + tag + ">")) { //check if line contains tag
                    while (true) {//keep adding to string while closing tag not contained
                        if (lineFromFile.contains("</" + tag + ">"))
                            break;
                        lineFromFile += scanner.nextLine();
                    }
                    lineFromFile = rmString(rmString(lineFromFile, "<" + tag + ">"), "</" + tag + ">"); //remove tags
                    fList.add(lineFromFile);//add to list
                }

            }
        }
        return fList;

    }


    /**
     * This stores all the info gathered from the file in the appropriate list
     * if the string contains the right substring, store it where it belongs
     * @param str
     */
    private void storeInLists(String str) {
        Scanner scanner;
        String line = "";
        scanner = new Scanner(str);
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (line.contains("UTC")) {
                datetime[index] = line;
            } else if (datetime[index] == null) {
                datetime[index] = "";
            }

            if (line.contains("Winds:")) {
                winds[index] = line.replace("Winds:", "");
            } else if (winds[index] == null) {
                winds[index] = "";
            }
            if (line.contains("Atmospheric Pressure:")) {
                aPressure[index] = line.replace("Atmospheric Pressure:", "");
            } else if (aPressure[index] == null) {
                aPressure[index] = "";
            }
            if (line.contains("Air Temperature:")) {
                atemperature[index] = line.replace("Air Temperature:", "");
            } else if (atemperature[index] == null) {
                atemperature[index] = "";
            }
            if (line.contains("Dew Point:")) {
                dewPoint[index] = line.replace("Dew Point:", "");
            } else if (dewPoint[index] == null) {
                dewPoint[index] = "";
            }
            if (line.contains("Significant Wave Height:")) {
                height[index] = line.replace("Significant Wave Height:", "");
            } else if (height[index] == null) {
                height[index] = "";
            }
            if (line.contains("Dominant Wave period:")) {
                period[index] = line.replace("Dominant Wave period:", "");
            } else if (period[index] == null) {
                period[index] = "";
            }
            if (line.contains("Mean Wave Direction:")) {
                direction[index] = line.replace("Mean Wave Direction:", "");
            } else if (direction[index] == null) {
                direction[index] = "";
            }
            if (line.contains("Water Temperature:")) {
                temperature[index] = line.replace("Water Temperature:", "");
            } else if (temperature[index] == null) {
                temperature[index] = "";
            }


        }
        index++;
    }

    /**
     * This method takes in a string and removes the substring specified
     *
     * @param ogString
     * @param subString
     * @return
     */
    private String rmString(String ogString, String subString) {
        ogString = ogString.replace(subString, "");
        return ogString;
    }

    /**
     * This method replaces the substring specified with another one
     * @param ogString
     * @param subString
     * @param newString
     * @return
     */
    private String rpStringwith(String ogString, String subString, String newString) {
        ogString = ogString.replace(subString, newString);
        return ogString;
    }
}

