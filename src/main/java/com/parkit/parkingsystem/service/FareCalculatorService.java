package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import java.util.Date;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        //Need complete dates to have a parcking duration that can be less than an hour or more than a day
        Date inTime = ticket.getInTime();
        Date outTime = ticket.getOutTime();

        float parckingDurationInSecondes = (float) (outTime.getTime() - inTime.getTime());
        float parckingDurationInHour = parckingDurationInSecondes/(1000*60*60);

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(parckingDurationInHour * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(parckingDurationInHour * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}