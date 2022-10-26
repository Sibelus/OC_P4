package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseITest {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){
        // GIVEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        //TODO: check that a ticket is actually saved in DB and Parking table is updated with availability

        // WHEN
        Ticket returnedTicket = ticketDAO.getTicket("ABCDEF");
        int nextAvailableParkingSlot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        // THEN
        assertEquals(returnedTicket.getVehicleRegNumber(), "ABCDEF");
        assertEquals(nextAvailableParkingSlot, 2);
    }

    @Test
    public void testParkingLotExit(){
        // GIVEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
        ticket.setOutTime(new Date());
        ticketDAO.updateTicketSuperAdmin(ticket);

        parkingService.processExitingVehicle();
        //TODO: check that the fare generated and out time are populated correctly in the database

        // WHEN
        Ticket returnedTicket = ticketDAO.getTicket("ABCDEF");
        Date outTimeTicket = returnedTicket.getOutTime();

        // THEN
        assertNotNull(returnedTicket.getPrice());
        assertNotNull(outTimeTicket);
    }

    @Test
    public void testParkingLotExitForRegularCustomer(){
        // GIVEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
        ticket.setPrice(0);
        ticket.setOutTime(new Date());
        ticketDAO.updateTicketSuperAdmin(ticket);

        parkingService.processIncomingVehicle();
        Ticket ticket2 = ticketDAO.getTicket("ABCDEF");
        ticket2.setInTime(new Date(System.currentTimeMillis() - (45*60*1000)));
        ticket2.setOutTime(new Date());
        ticketDAO.updateTicketSuperAdmin(ticket2);

        //WHEN
        parkingService.processExitingVehicle();
        ticket2 = ticketDAO.getTicket("ABCDEF");
        ticket2.setRegularCustomer(ticketDAO.checkRegularCustomer("ABCDEF"));

        //THEN
        assertEquals(true, ticket2.getRegularCustomer());
    }
}
