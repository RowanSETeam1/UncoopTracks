package io.evolution;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import static io.evolution.Constants.*;
import static java.lang.Integer.parseInt;


/**
 * This class is responsible for calculating a geometric area that
 * represents possible location of a vessel based on the minutes
 * passed since the vessel experienced a loss-of-signal.
 */

public class AreaPredictor {

    private float[] initialCoordinates = new float[2];
    private Connection dbConnect;
    private int travelTime;
    private float vesselSpeed;
    private float vesselCourse;
    private String lastContactTime;
    private float maxTurn = 180;
    private float vesselTurnRate;
    private ArrayList<Point> leftCoordinates = new ArrayList<Point>();
    private ArrayList<Point> rightCoordinates = new ArrayList<Point>();
    private ArrayList<Point> forwardCoordinates = new ArrayList<Point>();

    /**
     * Instantiates a new Area predictor.
     *
     * @param dbConnect  The database connection
     * @param mmsi       The MMSI number of the vessel being located.
     * @param date       the date
     * @param travelTime The minutes passed since experiencing a loss-of-signal.
     * @throws SQLException An SQL exception.
     */
    AreaPredictor(Connection dbConnect, String mmsi, String date, String travelTime, Float maxTurn) throws SQLException {
        this.travelTime = parseInt((travelTime));
        this.dbConnect = dbConnect;
        this.maxTurn = maxTurn;
        PreparedStatement get = dbConnect.prepareStatement("SELECT * FROM aisData WHERE (MMSI='"
                + mmsi + "' AND DATETIME LIKE '%" + date + "%') ORDER BY " + DATETIME + " DESC LIMIT 1;");
        ResultSet resultSet = get.executeQuery();
        vesselSize(dbConnect, mmsi);
        while (resultSet.next()) {
            String[] dateSplit = resultSet.getString(DATETIME).split(" ");
            lastContactTime = dateSplit[1];
           // System.out.println("lastcontact: " + lastContactTime);
            initialCoordinates[0] = resultSet.getFloat(LAT);
            initialCoordinates[1] = resultSet.getFloat(LONG);
            vesselSpeed = resultSet.getFloat(SPEED);
            vesselCourse = resultSet.getFloat(COURSE);
        }
        insertCoord(0, initialCoordinates[0], initialCoordinates[1]);
    }

    /**
     * Insert a coordinate
     *
     * @param time      the time
     * @param latitude  the latitude
     * @param longitude the longitude
     * @return true is successful, false otherwise
     */
    public boolean insertCoord(int time, float latitude, float longitude) {
        try {
            //System.out.println("INSERT INTO PUBLIC.KMLPOINTS VALUES ('" + time + "'," + latitude + "," + longitude + ");");
            PreparedStatement insertCoord = dbConnect.prepareStatement("INSERT INTO PUBLIC.KMLPOINTS VALUES ('" + time + "'," + latitude + "," + longitude + ");");
            insertCoord.execute();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    /**
     * Execute the predictive algorithm.
     *
     * @return the boolean flag
     */
    public boolean execute() {
        setLeftBoundaryCoordinates();
        setRightBoundaryCoordinates();
        populateDB();
        return true;
    }

    /**
     * Calculates the distance traveled (in kilometers)
     * in the given time (in minutes)
     *
     * @param time  Minutes the vessel has been traveling.
     * @param knots Speed the vessel in travels (in knots).
     * @return the distance
     */
    public float getDistance(int time, float knots) {

        //Converts given knots to kilometers per second.
        float knotsToKps = (knots * 0.000514444f);

        //Converts given minutes to seconds.
        float timeToSeconds = time * 60;

        //The distance traveled by the vessel, in meters.
        float distance = (knotsToKps * timeToSeconds);

        return distance;
    }


    /**
     * Sets primary boundary of the predicted area.
     */
    public void setPrimaryBoundary(float course, float lat, float lng, float distance) {

        float[] primaryBoundary = calculateCoordinates(lat, lng, course, distance);

        Point tempPoint = new Point(primaryBoundary[0], primaryBoundary[1]);
        forwardCoordinates.add(tempPoint);
    }


    /**
     * Sets right boundary coordinates of the predicted area.
     */
    public void setRightBoundaryCoordinates() {

        //The amount of time simulated to far.
        int currentTime = 1;
        float changeInCourse = .0f;

        //Initializes the coordinates at the last known signal location.
        float[] currentCoordinates = initialCoordinates;

        float currentHeading = vesselCourse;
        float incrementDistance = getDistance(1, vesselSpeed);
        float lat = currentCoordinates[0];
        float lon = currentCoordinates[1];
        float turnRate = vesselTurnRate;
        Point tempPoint;

        float maxDist = getDistance(travelTime, vesselSpeed);

        //Creates outer boundary of the polygon minute by minute until the specified time is reached.
        while (currentTime <= travelTime) {
            if (changeInCourse < maxTurn/2) {
                setPrimaryBoundary(currentHeading, lat, lon, maxDist);
                maxDist -= incrementDistance;
                currentHeading += turnRate;
                changeInCourse += turnRate;
            }


            currentCoordinates = calculateCoordinates(lat, lon, currentHeading, incrementDistance);

            lat = currentCoordinates[0];
            lon = currentCoordinates[1];
            tempPoint = new Point(lat, lon);
            rightCoordinates.add(tempPoint);
            currentTime++;
        }
    }

    /**
     * Sets left boundary coordinates of the predicted area.
     */
    public void setLeftBoundaryCoordinates() {

        //The amount of time simulated to far.
        int currentTime = 1;
        float changeInCourse = .0f;
        Point tempPoint;

        //Initializes the coordinates at the last known signal location.
        float[] currentCoordinates = initialCoordinates;
        float currentHeading = vesselCourse;
        float incrementDistance = getDistance(1, vesselSpeed);
        float lat = currentCoordinates[0];
        float lon = currentCoordinates[1];
        float turnRate = vesselTurnRate;

        float maxDist = getDistance(travelTime, vesselSpeed);


        //Creates outer boundary of the polygon minute by minute until the specified time is reached.
        while (currentTime <= travelTime) {
            if (changeInCourse > maxTurn/2 * -1) {

                setPrimaryBoundary(currentHeading, lat, lon, maxDist);

                maxDist -= incrementDistance;

                currentHeading -= turnRate;
                changeInCourse -= turnRate;
            }
            currentCoordinates = calculateCoordinates(lat, lon, currentHeading, incrementDistance);
            lat = currentCoordinates[0];
            lon = currentCoordinates[1];

            tempPoint = new Point(lat, lon);
            leftCoordinates.add(tempPoint);

            currentTime++;
        }
        Collections.reverse(forwardCoordinates);
    }

    /**
     * Populates the database with the calculated points.
     * They are added in such a way to make drawing and viewing the area easy.
     */
    public void populateDB() {
        int pointCounter = 1;
        for (Point p : leftCoordinates) {
            insertCoord(pointCounter, p.getLatitude(), p.getLongitude());
            pointCounter++;
        }

        for (Point p : forwardCoordinates) {
            insertCoord(pointCounter, p.getLatitude(), p.getLongitude());
            pointCounter++;
        }

        for (int i = rightCoordinates.size() - 1; i >= 0; i--) {
            Point p = new Point(rightCoordinates.get(i).getLatitude(), rightCoordinates.get(i).getLongitude());
            insertCoord(pointCounter, p.getLatitude(), p.getLongitude());
            pointCounter++;
        }
    }


    /**
     * Calculate coordinates based on previous latitude, longitude, heading, and distance.
     * Formula Reference: http://stackoverflow.com/questions/7222382/get-lat-long-given-current-point-distance-and-bearing
     *
     * @param lat      The latitude of the starting coordinate.
     * @param lon      The longitude of the starting coordinates.
     * @param heading  The heading of the vessel at the given coordinates.
     * @param distance The distance that will be traveled by the vessel from the initial coordinates.
     * @return destinationCoordinates   An arraylist holding the latitude and longitude of the destination coordinates
     */
    public float[] calculateCoordinates(float lat, float lon, float heading, float distance) {

        //Calculates the destination coordinates given the initial coordinates, heading, and time traveled.
        float R = 6378.1f; //Radius of the Earth
        float bearing = (float) Math.toRadians(heading);

        float lat1 = (float) Math.toRadians(lat); //Current latitude point converted to radians.
        float lon1 = (float) Math.toRadians(lon); //Current longitude point converted to radians.

        float lat2 = (float) Math.asin(Math.sin(lat1) * Math.cos(distance / R) +
                Math.cos(lat1) * Math.sin(distance / R) * Math.cos(bearing));

        float lon2 = lon1 + (float) Math.atan2(Math.sin(bearing) * Math.sin(distance / R) * Math.cos(lat1),
                Math.cos(distance / R) - Math.sin(lat1) * Math.sin(lat2));

        lat2 = (float) Math.toDegrees(lat2);
        lon2 = (float) Math.toDegrees(lon2);

        float[] destinationCoordinates = {lat2, lon2};

        return destinationCoordinates;
    }


    /**
     * This method will determine whether or not the vessel's length is greater than or equal to 100 meters.
     * If it is, then is sets the vesselTurnRate to 3 degrees. Otherwise, vesselTurnRate is set to 5 degrees.
     *
     * @param mmsi      the targeted vessel's MMSI number
     * @param dbConnect the connection to the database
     */
    void vesselSize(Connection dbConnect, String mmsi) throws SQLException {
        //pulls points out of database
        PreparedStatement get = dbConnect.prepareStatement("SELECT * FROM aisData WHERE( MMSI='" + mmsi + "');");
        ResultSet resultSet = get.executeQuery();
        resultSet.next();

        //retrieve the bow length of the vessel from the database
        int bowLength = resultSet.getInt("A");

        //retrieve the stern length of the vessel from the database
        int sternLength = resultSet.getInt("B");

        //if the total length of the vessel is greater than or equal to 100 meters
        if ((bowLength + sternLength) >= 100) {
            vesselTurnRate = 3f;
            return;
        }
        vesselTurnRate = 5f;
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
        String description;

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
