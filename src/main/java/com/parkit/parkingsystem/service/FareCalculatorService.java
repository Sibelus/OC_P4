package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import java.util.Date;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        //Need complete dates to have a parking duration that can be less than an hour or more than a day
        Date inTime = ticket.getInTime();
        Date outTime = ticket.getOutTime();

        float parkingDurationInSeconds = (float) (outTime.getTime() - inTime.getTime());
        ticket.setParkingDurationInHour(parkingDurationInSeconds/(1000*60*60));
        float parkingDuration = ticket.getParkingDurationInHour();

        // Calculation of parking price according to : parking duration / regular customer / vehicle type
        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                if (parkingDuration > 0.5) {
                    ticket.setPrice(parkingDuration * Fare.CAR_RATE_PER_HOUR);
                } else {
                    ticket.setPrice(0);
                }
                break;
            }
            case BIKE: {
                ticket.setPrice(parkingDuration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unknown Parking Type");
        }
    }
}