package io.evolution;
import java.sql.Connection;
import java.util.ArrayList;



/**
 * Created by michael on 4/3/2016.
 */
public class AreaPredictor {

    private float[] initialCoordinates;
    private float[] primaryBoundry;
    private ArrayList<float[]> outerBoundryCoordinates;

    private int travelTime;
    private float vesselSpeed;


    AreaPredictor(Connection c, int MMSI, String startDate, String startTime, String endDate, String endTime){}


    public AreaPredictor(int time,float knots) {
        float travelTime = time;
        float vesselSpeed = knots;
        ArrayList<float[]> outerBoundryCoordinates = new ArrayList<float[]>();
    }



    private double getHeading()
    {
        float[] secondaryCoordinates = new float[2];

        double lat1 = initialCoordinates[0];
        double long1 = initialCoordinates[1];
        double lat2 = secondaryCoordinates[0];
        double long2 = secondaryCoordinates[1];

        double degreeToRadians = Math.PI / 180.0;

        double lat1Rads = lat1 * degreeToRadians;
        double lat2Rads = lat2 * degreeToRadians;
        double long1Rads = long1 * degreeToRadians;
        double long2Rads = long2 * degreeToRadians;

        return Math.atan2(Math.sin(long2Rads-long1Rads)*Math.cos(lat2Rads),
                Math.cos(lat1Rads)*Math.sin(lat2Rads) - Math.sin(lat1Rads)
                        *Math.cos(lat2Rads)*Math.cos(long2Rads-long1Rads)
        ) * 180/Math.PI;
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


    private double getDistance(int time,float knots){

        //
        double knotsToMps = (knots * 0.5144);
        double timeToSeconds = time * 60;

        //the distance traveled by the vessel, in meters.
        double distance = (knotsToMps * timeToSeconds);
        return distance;
    }


    private float[] calculateCoordinates(float[] coordinates, double heading){
        //calculates the destination coordinates given the initial coordinates, heading, and time traveled.


        float[] calculatedCoordinates = new float[2];
        return calculatedCoordinates;
    }


    private float[] setPrimaryBoundry(){
        //get last known coordinates
        double distance = getDistance(travelTime,vesselSpeed);
        double heading = getHeading();

        primaryBoundry = calculateCoordinates(initialCoordinates, getHeading());

        return primaryBoundry;
    }



   public void setOuterBoundryCoordinates(){

       int currentTime = 0;
       float[] currentCoordinates = initialCoordinates;
       double initialHeading = getHeading();
       double currentHeading = getHeading();

        while(currentTime <= travelTime){
            outerBoundryCoordinates.add(calculateCoordinates(initialCoordinates, currentHeading));
            currentTime++;
           currentHeading+=initialHeading;
        }
   }

}
