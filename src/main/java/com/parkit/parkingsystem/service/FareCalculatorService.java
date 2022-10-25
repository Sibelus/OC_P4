package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import java.util.Date;

public class FareCalculatorService {

    /**
     * Method that calculate parking price according to : parking duration / regular customer / vehicle type
     * @param ticket
     */
    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        // Calculate parking duration
        Date inTime = ticket.getInTime();
        Date outTime = ticket.getOutTime();
        ticket.setParkingDurationInHour(((float) (outTime.getTime() - inTime.getTime()))/(1000*60*60));
        float parkingDuration = ticket.getParkingDurationInHour();

        // Check regular customer
        boolean regularCustomer = ticket.getRegularCustomer();

        // Calculation of parking price according to : parking duration / regular customer / vehicle type
        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                if (parkingDuration > 0.5) {
                    if (regularCustomer) {
                        ticket.setPrice(parkingDuration * (Fare.CAR_RATE_PER_HOUR * 95) / 100);
                    } else {
                        ticket.setPrice(parkingDuration * Fare.CAR_RATE_PER_HOUR);
                    }
                } else {
                    ticket.setPrice(0);
                }
                break;
            }
            case BIKE: {
                if (parkingDuration > 0.5) {
                    if (regularCustomer) {
                        ticket.setPrice(parkingDuration * (Fare.BIKE_RATE_PER_HOUR * 95) / 100);
                    } else {
                        ticket.setPrice(parkingDuration * Fare.BIKE_RATE_PER_HOUR);
                    }
                } else {
                    ticket.setPrice(0);
                }
                break;
            }
            default: throw new IllegalArgumentException("Unknown Parking Type");
        }
    }
}