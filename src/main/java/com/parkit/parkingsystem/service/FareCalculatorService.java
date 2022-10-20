package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;
import java.util.Date;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        TicketDAO ticketDAO = new TicketDAO();
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        // Calculate parking duration
        Date inTime = ticket.getInTime();
        Date outTime = ticket.getOutTime();
        ticket.setParkingDurationInHour(((float) (outTime.getTime() - inTime.getTime()))/(1000*60*60));
        float parkingDuration = ticket.getParkingDurationInHour();

        // Check regular customer
        boolean regularCustomer = ticketDAO.checkRegularCustomer(ticket.getVehicleRegNumber());

        // Calculation of parking price according to : parking duration / regular customer / vehicle type
        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                if (regularCustomer) {
                    ticket.setPrice(parkingDuration * (Fare.CAR_RATE_PER_HOUR * 95)/100);
                } else if (parkingDuration > 0.5) {
                    ticket.setPrice(parkingDuration * Fare.CAR_RATE_PER_HOUR);
                } else {
                    ticket.setPrice(0);
                }
                break;
            }
            case BIKE: {
                ticket.setPrice(parkingDuration * Fare.BIKE_RATE_PER_HOUR);
                if (regularCustomer) {
                    ticket.setPrice(parkingDuration * (Fare.BIKE_RATE_PER_HOUR * 95)/100);
                } else if (parkingDuration > 0.5) {
                    ticket.setPrice(parkingDuration * Fare.BIKE_RATE_PER_HOUR);
                } else {
                    ticket.setPrice(0);
                }
                break;
            }
            default: throw new IllegalArgumentException("Unknown Parking Type");
        }
    }
}