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

    public AreaPredictor(Connection c, String mmsi, String startDate, String startTime, String endDate, String endTime) {
    }

   /* public AreaPredictor(int time, float knots) {
        float travelTime = time;
        float vesselSpeed = knots;
        ArrayList<float[]> outerBoundryCoordinates = new ArrayList<float[]>();
    }

    private float getHeading() {
        //Get last known coordinates
        //Get second-to-last known coordinates
        //Use math function to determine the degree between the two latitude points
        //Return heading in degrees

    }


    private int getDistance(int time, float knots) {
        //determines the distance in meters that the vessel would travel at
        // the given speed(in knots) for the given time(in minutes)
    }


    private float[] calculateCoordinates(float[] coordinates, float heading) {
        //calculates the destination coordinates given the initial coordinates, heading, and time traveled.
    }


    private float[] setPrimaryBoundry() {
        //get last known coordinates
        int distance = getDistance(travelTime, vesselSpeed);
        float heading = getHeading();

        primaryBoundry = calculateCoordinates(initialCoordinates, getHeading());

        primaryBoundry;
    }


    public void setOuterBoundryCoordinates() {

        int currentTime = 0;
        float[] currentCoordinates = initialCoordinates;
        float initialHeading = getHeading();
        float currentHeading = getHeading();

        while (currentTime <= travelTime) {
            outerPoints.add(calculateCoordinates(initialCoordinates, currentHeading))
            currentTime++;
            currentHeading += initialHeading;
        }


    }*/
}
