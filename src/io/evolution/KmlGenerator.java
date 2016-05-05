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
import static io.evolution.Constants.*;
/**
 * The type Kml generator.
 */
public class KmlGenerator {
    String mmsi;
    Connection c;
    ArrayList<Point> points = new ArrayList<Point>(); //polygon points
    ArrayList<Point> path = new ArrayList<Point>(); //polygon points
    ArrayList<Point> ports = new ArrayList<>();
    Connection portDBConnect;
    int index = 0;


    public KmlGenerator(String mmsi,Connection c , Connection portDBConnect){
        this.c = c;
        this.mmsi = mmsi;
        this.portDBConnect = portDBConnect;
    }

    /**
     * The Placemarks.
     */
    ArrayList<Point> placemarks = new ArrayList<Point>(); //points where pins are dropped.

    /**
     * Pull.
     * @throws SQLException the sql exception
     */

//pull from database
    void pull() throws SQLException {

        PreparedStatement get = c.prepareStatement("SELECT * FROM PUBLIC.KMLPOINTS ORDER BY "+DATETIME+";");
        ResultSet resultSet = get.executeQuery();
        while (resultSet.next()) {
            points.add(new Point(resultSet.getFloat("latitude"),resultSet.getFloat("longitude"), resultSet.getString("datetime")));
           // System.out.println(resultSet.getFloat("latitude") +", "+resultSet.getFloat("longitude")+", "+resultSet.getString("datetime"));
        }

    }
    void pullPath() throws SQLException {

        PreparedStatement get = c.prepareStatement("SELECT * FROM PUBLIC.AISDATA WHERE (MMSI='"
                + mmsi+ "') ORDER BY " + DATETIME+";");
        ResultSet resultSet = get.executeQuery();
        while (resultSet.next()) {
            path.add(new Point(resultSet.getFloat("latitude"),resultSet.getFloat("longitude"), resultSet.getString("datetime")));
           // System.out.println(resultSet.getFloat("latitude") +", "+resultSet.getFloat("longitude")+", "+resultSet.getString("datetime"));
        }

    }
    void pullPorts() throws SQLException{
        PreparedStatement getPorts = portDBConnect.prepareStatement("SELECT * FROM PUBLIC.PORTS");
        ResultSet resultSet = getPorts.executeQuery();
        while (resultSet.next()){
            ports.add(new Point(resultSet.getFloat(LAT),resultSet.getFloat(LONG),resultSet.getString("PORTNAME")));
           // System.out.println(resultSet.getFloat(LAT) +", "+resultSet.getFloat(LONG)+", "+resultSet.getString("PORTNAME"));
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
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n"+
                    "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n");
            writer.write(" <Document>\n<name>"+filename+"</name>\n" +
                    "<LookAt>\n" +
                    "    <longitude>"+points.get(0).longitude+"</longitude>\n" +
                    "    <latitude>"+points.get(0).latitude+"</latitude>\n" +
                    "    <altitude>0</altitude>\n" +
                    "    <range>14794.882995</range>\n" +
                    "    <tilt>66.768762</tilt>\n" +
                    "    <heading>71.131493</heading>\n" +
                    "  </LookAt>" +
                    "    <description>Examples of paths. Note that the tessellate tag is by default\n" +
                    "      set to 0. If you want to create tessellated lines, they must be authored\n" +
                    "      (or edited) directly in KML.</description>\n" +
                    "    <Style id=\"yellowLineGreenPoly\">\n" +
                    "      <LineStyle>\n" +
                    "        <color>7f00ffff</color>\n" +
                    "        <width>4</width>\n" +
                    "      </LineStyle>\n" +
                    "      <PolyStyle>\n" +
                    "        <color>7f00ff00</color>\n" +
                    "      </PolyStyle>\n" +
                    "    </Style>\n");
            //writting first point as placemark
                writer.write(createPlacemark(points.get(0), "Initial Point"));
            for (int i = 0; i < path.size(); i++) {
                writer.write(createPlacemark(path.get(i), path.get(i).description));
            }
            for (int i = 0; i < ports.size() ; i++) {
                writer.write(createPlacemark(ports.get(i),ports.get(i).description));
            }
            //writting polygon
            writer.write(createPolygon());
            writer.write(createPath());
            writer.write("</Document>\n");



            writer.write("</kml>\n");
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
    public String createPlacemark(Point point, String des) {
        index = index+1;
        String style = "";

        style = "<Style id=\"icon\">\n" +
                "        <IconStyle>\n" +
                "          <Icon>\n" +
                "            <href>placemark.png</href>\n" +
                "          </Icon>\n" +
                "        </IconStyle>\n" +
                " </Style>\n";

        String tag = "";
        tag += "<Placemark>\n<name>" + des+ "</name>\n";
        tag += "<description>"+index+"\n" +
                des+"</description>\n <Point>\n<coordinates>" + point.getLongitude() + "," + point.getLatitude();

        tag += "</coordinates>\n</Point>\n </Placemark>\n";

        return tag;
    }

    /**
     * Create polygon string.
     *
     * @return the string
     */
    public String createPolygon() {
        int size = ((points.size() - 2)/2);
        Point origin = points.get(0);
        Point second_p = points.get(1);
        String tag = "";
        tag += " <Placemark>\n" +
                "<name>Area of Prediction</name>\n" +
                "<Polygon>\n" +
                "<extrude>1</extrude>\n" +
                "<altitudeMode>clampToGround</altitudeMode>\n" +
                "<outerBoundaryIs>\n" +
                "<LinearRing>\n" +
                "<coordinates>\n";


        System.out.println("original: "+origin.getLongitude() + "," + origin.getLatitude()+"\n");
        for (int i = 0; i < points.size(); i++) {
            tag +=  points.get(i).getLongitude() + "," + points.get(i).getLatitude()+"\n";

        }

        tag += "</coordinates>\n" +
                "</LinearRing>\n" +
                "</outerBoundaryIs>\n" +
                "</Polygon>\n" +
                " <Style> \n" +
                "  <PolyStyle>  \n" +
                "   <color>#a00000ff</color>\n" +
                "  <outline>0</outline>\n" +
                "  </PolyStyle> \n" +
                " </Style>" +
                "</Placemark>\n";

        return tag;
    }


    public String createPath() {

        String tag = "";


        for (int i = 0; i < (path.size()-1); i++) {
            tag += "    <Placemark>\n" +
                    "      <name>Absolute Extruded</name>\n" +
                    "      <description>Transparent green wall with yellow outlines</description>\n" +
                    "      <styleUrl>#yellowLineGreenPoly</styleUrl>\n" +
                    "      <LineString>\n" +
                    "        <extrude>1</extrude>\n" +
                    "        <tessellate>1</tessellate>\n" +
                    "        <altitudeMode>absolute</altitudeMode>\n" +
                    "        <coordinates>";
            tag +=  path.get(i).getLongitude() + "," + path.get(i).getLatitude() + "\n";
            tag +=  path.get(i+1).getLongitude() + "," + path.get(i+1).getLatitude() + "\n";

            tag += "  </coordinates>\n" +
                    "      </LineString>\n" +
                    "    </Placemark>\n";

        }



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
        timeStamp = "output/"+timeStamp;
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
