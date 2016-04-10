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

    private int travelTime;
    private float vesselSpeed;


    AreaPredictor(Connection c, String mmsi, String startDate, String startTime, String endDate, String endTime) throws SQLException {
        PreparedStatement get = c.prepareStatement("SELECT * FROM aisData WHERE (MMSI='"
                + mmsi + "' AND DATETIME LIKE '%2016-03-15%') ORDER BY "+DATETIME+" DESC LIMIT 2;");
        ResultSet resultSet = get.executeQuery();
        ArrayList<ResultSet> needTwo = new ArrayList<>();

        while (resultSet.next()) {


            needTwo.add(resultSet);
            if (needTwo.size() == 2) {
                float distance = Float.parseFloat(Double.toString(getDistance(60, needTwo.get(1).getFloat(SPEED)) * Math.pow(10, -3)));
                System.out.println(distance);
                needTwo = new ArrayList<ResultSet>();

            }
        }
    }


    public AreaPredictor(int time, float knots) {
        float travelTime = time;
        float vesselSpeed = knots;
        ArrayList<float[]> outerBoundryCoordinates = new ArrayList<float[]>();
    }


    private double getHeading() {

        // retrieves the second-to-last known coordinates of the vessel
        float[] secondaryCoordinates = new float[2];

        double lat1 = initialCoordinates[0];
        double long1 = initialCoordinates[1];
        double lat2 = secondaryCoordinates[0];
        double long2 = secondaryCoordinates[1];

        double degreeToRadians = Math.PI / 180.0;

        //converts each latitude and longitude to radians to be used in heading calculation
        double lat1Rads = lat1 * degreeToRadians;
        double lat2Rads = lat2 * degreeToRadians;
        double long1Rads = long1 * degreeToRadians;
        double long2Rads = long2 * degreeToRadians;

        //calculates and returns the heading
        return Math.atan2(Math.sin(long2Rads - long1Rads) * Math.cos(lat2Rads),
                Math.cos(lat1Rads) * Math.sin(lat2Rads) - Math.sin(lat1Rads)
                        * Math.cos(lat2Rads) * Math.cos(long2Rads - long1Rads)
        ) * 180 / Math.PI;
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


    private double getDistance(int time, float knots) {

        //
        double knotsToMps = (knots * 0.5144);
        double timeToSeconds = time * 60;

        //the distance traveled by the vessel, in meters.
        double distance = (knotsToMps * timeToSeconds);
        return distance;
    }


    private float[] calculateCoordinates(float[] coordinates, double heading) {
        //calculates the destination coordinates given the initial coordinates, heading, and time traveled.


        float[] calculatedCoordinates = new float[2];
        return calculatedCoordinates;
    }


    private float[] setPrimaryBoundry() {
        //get last known coordinates of vessel
        double distance = getDistance(travelTime, vesselSpeed);
        double heading = getHeading();

        primaryBoundry = calculateCoordinates(initialCoordinates, getHeading());

        return primaryBoundry;
    }


    public void setOuterBoundryCoordinates() {

        int currentTime = 0;
        float[] currentCoordinates = initialCoordinates;
        double initialHeading = getHeading();
        double currentHeading = getHeading();

        while (currentTime <= travelTime) {
            outerBoundryCoordinates.add(calculateCoordinates(initialCoordinates, currentHeading));
            currentTime++;
            currentHeading += initialHeading;
        }
    }

    public double initial(double lat1, double long1, double lat2, double long2) {
        return (_bearing(lat1, long1, lat2, long2) + 360.0) % 360;
    }

    static private double _bearing(double lat1, double long1, double lat2, double long2) {
        double degToRad = Math.PI / 180.0;
        double phi1 = lat1 * degToRad;
        double phi2 = lat2 * degToRad;
        double lam1 = long1 * degToRad;
        double lam2 = long2 * degToRad;

        return Math.atan2(Math.sin(lam2 - lam1) * Math.cos(phi2),
                Math.cos(phi1) * Math.sin(phi2) - Math.sin(phi1) * Math.cos(phi2) * Math.cos(lam2 - lam1)
        ) * 180 / Math.PI;
    }

    public void calculateCoordinates(float lat, float lon, float heading, float distance) {

        //calculates the destination coordinates given the initial coordinates, heading, and time traveled.

//
//        float[] calculatedCoordinates = new float[2];
//        return calculatedCoordinates;

        double R = 6378.1; //Radius of the Earth
        //Bearing is 90 degrees converted to radians.
        //Distance in km

        //lat2  52.20444 - the lat result I'm hoping for
        //lon2  0.36056 - the long result I'm hoping for.

        double lat1 = Math.toRadians(lat); //Current lat point converted to radians
        double lon1 = Math.toRadians(lon); //Current long point converted to radians

        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(distance / R) +
                Math.cos(lat1) * Math.sin(distance / R) * Math.cos(heading));

        double lon2 = lon1 + Math.atan2(Math.sin(heading) * Math.sin(distance / R) * Math.cos(lat1),
                Math.cos(distance / R) - Math.sin(lat1) * Math.sin(lat2));

        lat2 = Math.toDegrees(lat2);
        lon2 = Math.toDegrees(lon2);

        System.out.println(lat2);
        System.out.println(lon2);
    }

}
