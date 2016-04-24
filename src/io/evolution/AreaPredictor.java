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
 *  passed since the vessel experienced a loss-of-signal.
 */

public class AreaPredictor {

    private float[] initialCoordinates = new float[2];
    private float[] secondaryCoordinates = new float[2];
    ArrayList<ResultSet> needTwo = new ArrayList<>();
    private final float PI = Float.parseFloat(Double.toString(Math.PI));
    private Connection dbConnect;
    private int travelTime;
    private float vesselSpeed;
    private float vesselCourse;
    private String lastContactTime;
    private final float MAX_TURN = 60f;
    private float vesselTurnRate;

    private ArrayList<Point> leftCoordinates = new ArrayList<Point>();
    private ArrayList<Point> rightCoordinates = new ArrayList<Point>();
    private ArrayList<Point> forwardCoordinates = new ArrayList<Point>();

    /**
     * Instantiates a new Area predictor.
     *
     * @param dbConnect          the database connection
     * @param mmsi       The MMSI number of the vessel being located.
     * @param date       the date
     * @param travelTime The minutes passed since experiencing a loss-of-signal.
     * @throws SQLException    An SQL exception.
     */
    AreaPredictor(Connection dbConnect, String mmsi, String date, String travelTime) throws SQLException {
        this.travelTime = parseInt((travelTime));
        this.dbConnect = dbConnect;
        PreparedStatement get = dbConnect.prepareStatement("SELECT * FROM aisData WHERE (MMSI='"
                + mmsi + "' AND DATETIME LIKE '%" + date + "%') ORDER BY " + DATETIME + " DESC LIMIT 2;");
        ResultSet resultSet = get.executeQuery();
        vesselSize(dbConnect, mmsi);
        while (resultSet.next()) {
            needTwo.add(resultSet);
            if (needTwo.size() == 1) {
                secondaryCoordinates[0] = resultSet.getFloat(LAT);
                secondaryCoordinates[1] = resultSet.getFloat(LONG);
            } else if (needTwo.size() == 2) {
                String[] dateSplit = needTwo.get(1).getString(DATETIME).split(" ");
                lastContactTime = dateSplit[1];
                System.out.println("lastcontact: " + lastContactTime);
                initialCoordinates[0] = resultSet.getFloat(LAT);
                initialCoordinates[1] = resultSet.getFloat(LONG);
                vesselSpeed = resultSet.getFloat(SPEED);
                vesselCourse = resultSet.getFloat(COURSE);
            }
        }
        insertCoord(0, initialCoordinates[0], initialCoordinates[1]);
    }

    /**
     * Insert coord boolean.
     *
     * @param time      the time
     * @param latitude  the latitude
     * @param longitude the longitude
     * @return the boolean
     */
    public boolean insertCoord(int time, float latitude, float longitude) {
        try {
            System.out.println("INSERT INTO PUBLIC.KMLPOINTS VALUES ('" + time + "'," + latitude + "," + longitude + ");");
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
        //(getHeading());
        setRightBoundaryCoordinates();
        populateDB();
        return true;
    }


    /**
     * Predicts the heading of the vessel by finding the angle between the last two known coordinates.
     *
     * @return the heading
     */
    public float getHeading() {

        //Retrieves the second-to-last known coordinates of the vessel.
        float lat2 = secondaryCoordinates[0];
        float long2 = secondaryCoordinates[1];
        float lat1 = initialCoordinates[0];
        float long1 = initialCoordinates[1];

        //Constant used to convert degrees to radians.
        float degreeToRadians = PI / 180.0f;

        //converts each latitude and longitude to radians to be used in heading calculation.
        float lat1Rads = lat1 * degreeToRadians;
        float lat2Rads = lat2 * degreeToRadians;
        float long1Rads = long1 * degreeToRadians;
        float long2Rads = long2 * degreeToRadians;


        //Calculates and returns the heading.
        float result = (float) Math.atan2(Math.sin(long2Rads - long1Rads) * Math.cos(lat2Rads),
                Math.cos(lat1Rads) * Math.sin(lat2Rads) - Math.sin(lat1Rads)
                        * Math.cos(lat2Rads) * Math.cos(long2Rads - long1Rads)
        ) * 180 / PI;

        return vesselCourse;
    }


    /**
     * Predicts the heading of the vessel by finding the angle between the last two known coordinates.
     *
     * @return the heading
     */
    public float getMaxAngle(float latitude1, float longitude1,float latitude2, float longitude2) {

        //Retrieves the second-to-last known coordinates of the vessel.
        float lat1 = latitude1;
        float long1 = longitude1;
        float lat2 = latitude2;
        float long2 = longitude2;

        //Constant used to convert degrees to radians.
        float degreeToRadians = PI / 180.0f;

        //converts each latitude and longitude to radians to be used in heading calculation.
        float lat1Rads = lat1 * degreeToRadians;
        float lat2Rads = lat2 * degreeToRadians;
        float long1Rads = long1 * degreeToRadians;
        float long2Rads = long2 * degreeToRadians;


        //Calculates and returns the heading.
        float result = (float) Math.atan2(Math.sin(long2Rads - long1Rads) * Math.cos(lat2Rads),
                Math.cos(lat1Rads) * Math.sin(lat2Rads) - Math.sin(lat1Rads)
                        * Math.cos(lat2Rads) * Math.cos(long2Rads - long1Rads)
        ) * 180 / PI;

        return result;
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
    public void setPrimaryBoundary(float course, float lat , float lng, float distance) {


        //Get last known coordinates and heading of vessel.
        //float distance = getDistance(travelTime, vesselSpeed);

        //Calculates the primary boundary coordinates.
        //float[] primaryBoundary = calculateCoordinates(initialCoordinates[0], initialCoordinates[1], course, distance);

        float[] primaryBoundary = calculateCoordinates(lat, lng, course, distance);

        Point tempPoint = new Point(primaryBoundary[0], primaryBoundary[1]);
        forwardCoordinates.add(tempPoint);
    }






    /**
     * Sets left boundary coordinates of the predicted area.
     */
    public void setRightBoundaryCoordinates() {

        //The amount of time simulated to far.
        int currentTime = 1;
        float changeInCourse = .0f;

        //Initializes the coordinates at the last known signal location.
        float[] currentCoordinates = initialCoordinates;

        float currentHeading = getHeading();
        float incrementDistance= getDistance(1, vesselSpeed);
        float lat = currentCoordinates[0];
        float lon = currentCoordinates[1];
        float turnRate = vesselTurnRate;
        Point tempPoint;

        float maxDist = getDistance(travelTime, vesselSpeed);


        //Creates outer boundary of the polygon minute by minute until the specified time is reached.
        while (currentTime <= travelTime  ) {
            if(changeInCourse < MAX_TURN) {
                setPrimaryBoundary(currentHeading,lat,lon,maxDist);
                maxDist-=incrementDistance;
                currentHeading += turnRate;
                changeInCourse += turnRate;
            }


            currentCoordinates = calculateCoordinates(lat, lon, currentHeading, incrementDistance);

            lat = currentCoordinates[0];
            lon = currentCoordinates[1];
            tempPoint = new Point(lat, lon);
            //insertCoord(counter, lat, lon);
            rightCoordinates.add(tempPoint);
            currentTime++;
        }
    }

    /**
     * Sets right boundary coordinates of the predicted area.
     */
    public void setLeftBoundaryCoordinates() {

        //The amount of time simulated to far.
        int currentTime = 1;
        float changeInCourse = .0f;
        Point tempPoint;

        //Initializes the coordinates at the last known signal location.
        float[] currentCoordinates = initialCoordinates;
        float initialHeading = getHeading();
        float currentHeading = getHeading();
        float incrementDistance = getDistance(1, vesselSpeed);
        float lat = currentCoordinates[0];
        float lon = currentCoordinates[1];
        float turnRate = vesselTurnRate;
        int maxTime = travelTime*2 + 1;

        Boolean initalPointFlag = true;

        float maxAngle;

        float maxDist = getDistance(travelTime, vesselSpeed);


        //Creates outer boundary of the polygon minute by minute until the specified time is reached.
        while (currentTime <= travelTime  ) {
            if(changeInCourse > MAX_TURN * -1) {

                setPrimaryBoundary(currentHeading,lat,lon,maxDist);

                maxDist-=incrementDistance;

                currentHeading -= turnRate;
                changeInCourse -= turnRate;
            }
            currentCoordinates = calculateCoordinates(lat, lon, currentHeading, incrementDistance);
            lat = currentCoordinates[0];
            lon = currentCoordinates[1];

            tempPoint = new Point(lat, lon);
            leftCoordinates.add(tempPoint);

            //insertCoord(maxTime--, lat, lon);

            currentTime++;
        }
        Collections.reverse(forwardCoordinates);
    }

    public void populateDB(){
        int pointCounter = 1;
        for(Point p : leftCoordinates){
            insertCoord(pointCounter, p.getLatitude(), p.getLongitude());
            pointCounter++;
        }

        for(Point p : forwardCoordinates){
            insertCoord(pointCounter, p.getLatitude(), p.getLongitude());
            pointCounter++;
        }

        for(int i=rightCoordinates.size()-1; i>= 0; i--){
            Point p = new Point(rightCoordinates.get(i).getLatitude(), rightCoordinates.get(i).getLongitude());
            insertCoord(pointCounter, p.getLatitude(), p.getLongitude());
            pointCounter++;
        }
    }










    /**
     * Calculate coordinates float [ ].
     *
     * @param lat      The latitude of the starting coordinate.
     * @param lon      The longitude of the starting coordinates.
     * @param heading  The heading of the vessel at the given coordinates.
     * @param distance The distance that will be traveled by the vessel from the initial coordinates.
     *
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
/*
    public double[] calculateCoordinates(double lat, double lon, double heading, double distance) {

        double R = 6378.1; //#Radius of the Earth
    brng = 1.57 #Bearing is 90 degrees converted to radians.
    d = 15 #Distance in km

    #lat2  52.20444 - the lat result I'm hoping for
            #lon2  0.36056 - the long result I'm hoping for.

    lat1 = math.radians(52.20472) #Current lat point converted to radians
    lon1 = math.radians(0.14056) #Current long point converted to radians

            lat2 = math.asin( math.sin(lat1)*math.cos(d/R) +
            math.cos(lat1)*math.sin(d/R)*math.cos(brng))

    lon2 = lon1 + math.atan2(math.sin(brng)*math.sin(d/R)*math.cos(lat1),
            math.cos(d/R)-math.sin(lat1)*math.sin(lat2))

    lat2 = math.degrees(lat2)
    lon2 = math.degrees(lon2)

    print(lat2)
    print(lon2)
    }*/

    /**
     * This method will determine whether or not the vessel's length is greater than or equal to 100 meters.
     * If it is, then is sets the vesselTurnRate to 3 degrees. Otherwise, vesselTurnRate is set to 5 degrees.
     *
     * @param mmsi the targeted vessel's MMSI number
     * @param dbConnect the connection to the database
     */
    void vesselSize(Connection dbConnect, String mmsi) throws SQLException {
        //pulls points out of database
        PreparedStatement get = dbConnect.prepareStatement("SELECT * FROM aisData WHERE( MMSI='"+mmsi+"');");
        ResultSet resultSet = get.executeQuery();
        resultSet.next();
        //retrieve the bow length of the vessel from the database
        int bowLength = resultSet.getInt("A");
        //retrieve the stern length of the vessel from the database
        int sternLength = resultSet.getInt("B");

        //if the total length of the vessel is greater than or equal to 100 meters
        if((bowLength + sternLength) >= 100)
        {
            vesselTurnRate = 3f;
            return;
        }
        vesselTurnRate = 5f;
        return;
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
