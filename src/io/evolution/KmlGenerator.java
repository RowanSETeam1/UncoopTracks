package io.evolution;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import static io.evolution.Constants.DATETIME;

/**
 * Created by eliakah on 4/3/2016.
 */
public class KmlGenerator {

    ArrayList<Point> points = new ArrayList<Point>(); //polygon points
    ArrayList<Point> placemarks = new ArrayList<Point>(); //points where pins are dropped.

    //pull from database
    void pull(Connection c) throws SQLException {

        PreparedStatement get = c.prepareStatement("SELECT * FROM PUBLIC.KMLPOINTS;");
        ResultSet resultSet = get.executeQuery();
        while (resultSet.next()) {
            points.add(new Point(resultSet.getFloat("latitude"),resultSet.getFloat("longitude"), resultSet.getString("datetime")));
            System.out.println(resultSet.getFloat("latitude") +", "+resultSet.getFloat("longitude")+", "+resultSet.getString("datetime"));
        }

    }

    void generate() throws IOException {
        //creates file
        String filename = (getFileName());
        File outPutFile = new File(filename);

        if (outPutFile.createNewFile()) {
            String text = "";
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n <kml xmlns=\"http://www.opengis.net/kml/2.2\">\n");
            writer.write(" <Document>\n<name>"+filename+"</name> \n");

            //writting polygon
            writer.write(createPolygon());

            //writting placemarks
            //for (int i = 0; i < placemarks.size(); i++) {
           //     writer.write(createPlacemark(placemarks.get(i)));
            //}


            writer.write("</Document>\n    <Style id=\"transBluePoly\">\n" +
                    "      <LineStyle>\n" +
                    "        <width>1.5</width>\n" +
                    "      </LineStyle>\n" +
                    "      <PolyStyle>\n" +
                    "        <color>7dff0000</color>\n" +
                    "      </PolyStyle>\n" +
                    "    </Style></kml>");
            writer.close();
        } else {
            System.out.println("File Creation Unsuccessful!.");
        }
    }



    public String createPlacemark(Point point) {
        String tag = "";
        tag += "<Placemark>\n<name>" + point.getLatitude() + ", " + point.getLongitude() + "</name>\n";
        tag += "<description>"+point.getDescription()+"+</description>\n<Point>\n<coordinates>" + point.getLatitude() + "," + point.getLongitude();

        tag += "</coordinates>\n</Point>\n </Placemark>\n";

        return tag;
    }

    public String createPolygon() {
        String tag = "";
        tag += " <Placemark>\n" +
                "<name>Area of Prediction</name>\n" +
                "<Polygon>\n" +
                "<extrude>1</extrude>\n" +
                "<altitudeMode>relativeToGround</altitudeMode>\n" +
                "<outerBoundaryIs>\n" +
                "<LinearRing>\n" +
                "<coordinates>\n";

        for (int i = 0; i < points.size(); i++) {
            tag += points.get(i).getLatitude() + "," + points.get(i).getLongitude()+"\n";
        }

        tag += "</coordinates>\n" +
                "</LinearRing>\n" +
                "</outerBoundaryIs>\n" +
                "</Polygon>\n" +
                "</Placemark>\n";

        return tag;
    }

    public void addPlacemark(Point p) {
        placemarks.add(p);
    }

    public void addPolygonPoints(Point p) {
        points.add(p);
    }

    private String getFileName(){
        Date date = new Date();
        String timeStamp = String.format(""+date);
        timeStamp = timeStamp.replaceAll(" ", "_").toLowerCase();
        timeStamp = timeStamp.replaceAll(":", "_").toLowerCase();
        timeStamp += ".kml";
       return timeStamp;
    }



    public class Point {
        float latitude, longitude;
        String  description;

        Point(float latitude, float longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
        Point(float latitude, float longitude, String description) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.description = description;
        }

        public float getLatitude() {
            return latitude;
        }

        public float getLongitude() {
            return longitude;
        }
        public String getDescription() {
            return description;
        }

        public String getCoordinate() {
            return ("" + longitude + latitude);
        }
    }

}
