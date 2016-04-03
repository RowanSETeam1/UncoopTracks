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

    private float getHeading(){

        float[] firstCoordSet = initialCoordinates;

        float heading = 0;

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



  return heading;
    }


    private double getDistance(int time,float knots){

        //
        double knotsToMps = (knots * 0.5144);
        double timeToSeconds = time * 60;

        //the distance traveled by the vessel, in meters.
        double distance = (knotsToMps * timeToSeconds);
        return distance;
    }


    private float[] calculateCoordinates(float[] coordinates, float heading){
        //calculates the destination coordinates given the initial coordinates, heading, and time traveled.


        float[] calculatedCoordinates = new float[2];
        return calculatedCoordinates;
    }


    private float[] setPrimaryBoundry(){
        //get last known coordinates
        double distance = getDistance(travelTime,vesselSpeed);
        float heading = getHeading();

        primaryBoundry = calculateCoordinates(initialCoordinates, getHeading());

        return primaryBoundry;
    }



   public void setOuterBoundryCoordinates(){

       int currentTime = 0;
       float[] currentCoordinates = initialCoordinates;
       float initialHeading = getHeading();
       float currentHeading = getHeading();

        while(currentTime <= travelTime){
            outerBoundryCoordinates.add(calculateCoordinates(initialCoordinates, currentHeading));
            currentTime++;
           currentHeading+=initialHeading;
        }
   }

}
