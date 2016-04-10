package io.evolution;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static io.evolution.Constants.DATETIME;
import static io.evolution.Constants.SPEED;

/**
 * Created by michael on 4/3/2016.
 */
public class AreaPredictor {

    private float[] initialCoordinates;
    private float[] primaryBoundry;
    private ArrayList<float[]> outerBoundryCoordinates;

    ArrayList<ResultSet> needTwo = new ArrayList<>();
    private float initialLat;
    private float initialLong;
    private float primaryBoundryLat;
    private float primaryBoundryLong;
    private final float PI = Float.parseFloat(Double.toString(Math.PI));


    private Connection c;
    private int travelTime;
    private float vesselSpeed;
    private String lastContactTime;

    AreaPredictor(Connection c, String mmsi, String date) throws SQLException {
        this.c = c;
        PreparedStatement get = c.prepareStatement("SELECT * FROM aisData WHERE (MMSI='"
                + mmsi + "' AND DATETIME LIKE '"+date+"') ORDER BY "+DATETIME+" DESC LIMIT 2;");
        ResultSet resultSet = get.executeQuery();

        while (resultSet.next()) {
            needTwo.add(resultSet);
            if (needTwo.size() == 2) {
                String[] dateSplit = needTwo.get(1).getString(DATETIME).split(" ");
                lastContactTime = dateSplit[1];
            }
        }
    }

    public boolean insertCoord(int time, float latitude,float longitude){
        try {
            PreparedStatement insertCoord = c.prepareStatement("INSERT INTO PUBLIC.KMLPOINTS VALUES ('" + time + "'," + latitude + "," + longitude + ");");
            insertCoord.execute();
        }catch (SQLException e){return false;}
        return true;
    }




    private float getHeading() {

        // retrieves the second-to-last known coordinates of the vessel
        float[] secondaryCoordinates = new float[2];

        float lat1 = initialLat;
        float long1 = initialLong;
        float lat2 = secondaryCoordinates[0];
        float long2 = secondaryCoordinates[1];

        float degreeToRadians = PI / 180.0f;

        //converts each latitude and longitude to radians to be used in heading calculation
        float lat1Rads = lat1 * degreeToRadians;
        float lat2Rads = lat2 * degreeToRadians;
        float long1Rads = long1 * degreeToRadians;
        float long2Rads = long2 * degreeToRadians;

        //calculates and returns the heading
        return (float) Math.atan2(Math.sin(long2Rads - long1Rads) * Math.cos(lat2Rads),
                Math.cos(lat1Rads) * Math.sin(lat2Rads) - Math.sin(lat1Rads)
                        * Math.cos(lat2Rads) * Math.cos(long2Rads - long1Rads)
        ) * 180 / PI;
    }


//    private float getHeading(){
//
//        //the coordindates given in second-to-last signal sent
//        float[] secondaryCoordinates = ;
//
//        double lat1Radians = initialCoordinates[0];
//        double long1Radians = initialCoordinates[1];
//        double lat2Radians = secondaryCoordinates[0];
//        double long2Radians = secondaryCoordinates[1];
//
//         lat1Radians = Math.toRadians(lat1Radians);
//         long1Radians = Math.toRadians(long1Radians);
//         lat2Radians = Math.toRadians(lat2Radians);
//         long2Radians = Math.toRadians(long2Radians);
//
//        float[] firstCoordSet = initialCoordinates;
//
//        float heading = 0;

    //Get last known coordinates
    //Get second-to-last known coordinates
    //Use math function to determine the degree between the two latitude points
    //Return heading in degrees

//        double dLon = (long2 - long1);
//
//        double y = Math.sin(dLon) * Math.cos(lat2);
//        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
//                * Math.cos(lat2) * Math.cos(dLon);
//
//        double heading = Math.atan2(y, x);
//
//        heading = Math.toDegrees(heading);
//        heading = (heading + 360) % 360;
//        heading = 360 - brng;


//
//  return heading;
//    }


    private float getDistance(int time, float knots) {

        //
        float knotsToMps = (knots * 0.5144f);
        float timeToSeconds = time * 60;

        //the distance traveled by the vessel, in meters.
        float distance = (knotsToMps * timeToSeconds);
        return distance;
    }




    private void setPrimaryBoundry() {
        //get last known coordinates of vessel
        float distance = getDistance(travelTime, vesselSpeed);
        float heading = getHeading();

        float[] primaryBoundry = calculateCoordinates(initialLat, initialLong, heading, distance);

        insertCoord(travelTime, primaryBoundry[0], primaryBoundry[1]);
    }


    public void setOuterBoundryCoordinates() {

        int currentTime = 0;
        float[] currentCoordinates = initialCoordinates;
        float initialHeading = getHeading();
        float currentHeading = getHeading();
        float incrementDistance = getDistance(1, vesselSpeed);
        float lat = currentCoordinates[0];
        float lon = currentCoordinates[1];

        while (currentTime <= travelTime) {
            currentCoordinates = calculateCoordinates(lat, lon, currentHeading, incrementDistance);
            outerBoundryCoordinates.add(currentCoordinates);
            lat = currentCoordinates[0];
            lon = currentCoordinates[1];
            insertCoord(currentTime, lat, lon);
            currentTime++;
            currentHeading += initialHeading;
        }
    }

    public double initial(float lat1, float long1, float lat2, float long2) {
        return (_bearing(lat1, long1, lat2, long2) + 360.0f) % 360;
    }

    public double _bearing(float lat1, float long1, float lat2, float long2) {
        float degToRad = PI / 180.0f;
        float phi1 = lat1 * degToRad;
        float phi2 = lat2 * degToRad;
        float lam1 = long1 * degToRad;
        float lam2 = long2 * degToRad;

        return Math.atan2(Math.sin(lam2 - lam1) * Math.cos(phi2),
                Math.cos(phi1) * Math.sin(phi2) - Math.sin(phi1) * Math.cos(phi2) * Math.cos(lam2 - lam1)
        ) * 180 / Math.PI;
    }

    public float[] calculateCoordinates(float lat, float lon, float heading, float distance) {

        //calculates the destination coordinates given the initial coordinates, heading, and time traveled.

//
//        float[] calculatedCoordinates = new float[2];
//        return calculatedCoordinates;

        float R = 6378.1f; //Radius of the Earth
        //Bearing is 90 degrees converted to radians.
        //Distance in km

        //lat2  52.20444 - the lat result I'm hoping for
        //lon2  0.36056 - the long result I'm hoping for.

        float lat1 = (float) Math.toRadians(lat); //Current lat point converted to radians
        float lon1 = (float) Math.toRadians(lon); //Current long point converted to radians

        float lat2 = (float) Math.asin(Math.sin(lat1) * Math.cos(distance / R) +
                Math.cos(lat1) * Math.sin(distance / R) * Math.cos(heading));

        float lon2 = lon1 +  (float) Math.atan2(Math.sin(heading) * Math.sin(distance / R) * Math.cos(lat1),
                Math.cos(distance / R) - Math.sin(lat1) * Math.sin(lat2));

        lat2 = (float) Math.toDegrees(lat2);
        lon2 = (float) Math.toDegrees(lon2);

        float[] calculatedCoordinates = {lat2, lon2};

        System.out.println(lat2);
        System.out.println(lon2);
        return calculatedCoordinates;
    }

}
