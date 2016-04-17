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

/**
 * The type Kml generator.
 */
public class KmlGenerator {

    /**
     * The Points.
     */
    ArrayList<Point> points = new ArrayList<Point>(); //polygon points
    int index = 0;
    /**
     * The Placemarks.
     */
    ArrayList<Point> placemarks = new ArrayList<Point>(); //points where pins are dropped.

    /**
     * Pull.
     *
     * @param c the c
     * @throws SQLException the sql exception
     */
//pull from database
    void pull(Connection c) throws SQLException {

        PreparedStatement get = c.prepareStatement("SELECT * FROM PUBLIC.KMLPOINTS;");
        ResultSet resultSet = get.executeQuery();
        while (resultSet.next()) {
            points.add(new Point(resultSet.getFloat("latitude"),resultSet.getFloat("longitude"), resultSet.getString("datetime")));
            System.out.println(resultSet.getFloat("latitude") +", "+resultSet.getFloat("longitude")+", "+resultSet.getString("datetime"));
        }

    }

    /**
     * Generate.
     *
     * @throws IOException the io exception
     */
    void generate() throws IOException {
        //creates file
        String filename = (getFileName());
        File outPutFile = new File(filename);

        if (outPutFile.createNewFile()) {
            String text = "";
            PrintWriter writer = new PrintWriter(
filename, "UTF-8");
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n <kml xmlns=\"http://www.opengis.net/kml/2.2\">\n");
            writer.write(" <Document>\n<name>"+filename+"</name> \n");
            //writting first point as placemark
            for (int i = 0; i < points.size(); i++) {
                writer.write(createPlacemark(points.get(i)));
            }

            //writting polygon
            //writer.write(createPolygon());





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


    /**
     * Create placemark string.
     *
     * @param point the point
     * @return the string
     */
    public String createPlacemark(Point point) {
        index = index+1;
        String tag = "";
        tag += "<Placemark>\n<name>" + point.getLatitude() + ", " + point.getLongitude() + "</name>\n";
        tag += "<description>"+index+"</description>\n<Point>\n<coordinates>" + point.getLongitude() + "," + point.getLatitude();

        tag += "</coordinates>\n</Point>\n </Placemark>\n";

        return tag;
    }

    /**
     * Create polygon string.
     *
     * @return the string
     */
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
            tag +=  points.get(i).getLongitude() + "," + points.get(i).getLatitude()+"\n";
        }

        tag += "</coordinates>\n" +
                "</LinearRing>\n" +
                "</outerBoundaryIs>\n" +
                "</Polygon>\n" +
                "</Placemark>\n";

        return tag;
    }

    /**
     * Add placemark.
     *
     * @param p the p
     */
    public void addPlacemark(Point p) {
        placemarks.add(p);
    }

    /**
     * Add polygon points.
     *
     * @param p the p
     */
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


    /**
     * The type Point.
     */
    public class Point {
        /**
         * The Latitude.
         */
        float latitude,
        /**
         * The Longitude.
         */
        longitude;
        /**
         * The Description.
         */
        String  description;

        /**
         * Instantiates a new Point.
         *
         * @param latitude  the latitude
         * @param longitude the longitude
         */
        Point(float latitude, float longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        /**
         * Instantiates a new Point.
         *
         * @param latitude    the latitude
         * @param longitude   the longitude
         * @param description the description
         */
        Point(float latitude, float longitude, String description) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.description = description;
        }

        /**
         * Gets latitude.
         *
         * @return the latitude
         */
        public float getLatitude() {
            return latitude;
        }

        /**
         * Gets longitude.
         *
         * @return the longitude
         */
        public float getLongitude() {
            return longitude;
        }

        /**
         * Gets description.
         *
         * @return the description
         */
        public String getDescription() {
            return description;
        }

        /**
         * Gets coordinate.
         *
         * @return the coordinate
         */
        public String getCoordinate() {
            return ("" + longitude + latitude);
        }
    }

}
