package server.service;

import server.dao.LabDAO;
import server.dao.LabSeatDAO;
import server.dao.LabSeatReservationDAO;
import server.model.Lab;
import server.model.LabSeat;
import server.model.LabSeatReservation;
import server.model.Reservation;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LabService {
    private LabDAO labDAO;
    private LabSeatDAO labSeatDAO;
    private LabSeatReservationDAO labSeatReservationDAO;

    public LabService(Connection connection) {
        this.labDAO = new LabDAO(connection);
        this.labSeatDAO = new LabSeatDAO(connection);
        this.labSeatReservationDAO = new LabSeatReservationDAO(connection);
    }

    public boolean addLab(Lab lab) {
        return labDAO.saveLab(lab);
    }

    public boolean updateLab(Lab lab) {
        return labDAO.updateLab(lab);
    }

    public boolean deleteLab(String labID) {
        return labDAO.deleteLab(labID);
    }

    public Lab getLabById(String labID) {
        return labDAO.getLabById(labID);
    }

    public List<Lab> getAllLabs() {
        return labDAO.getAllLabs();
    }

    public boolean addLabSeat(LabSeat labSeat) {
        return labSeatDAO.saveLabSeat(labSeat);
    }

    public boolean updateLabSeat(LabSeat labSeat) {
        return labSeatDAO.updateLabSeat(labSeat);
    }

    public boolean deleteLabSeat(int seatID) {
        return labSeatDAO.deleteLabSeat(seatID);
    }

    public LabSeat getLabSeatById(int seatID) {
        return labSeatDAO.getLabSeatById(seatID);
    }

    public List<LabSeat> getSeatsByLab(String labID) {
        return labSeatDAO.getLabSeatsByLabId(labID);
    }

    public List<LabSeat> getAllLabSeats() {
        return labSeatDAO.getAllLabSeats();
    }

    public List<LabSeat> getAvailableSeatsAtTime(LocalDateTime requestedTime) {
        if (requestedTime.getHour() >= 21) {
            return new ArrayList<>();
        }

        List<LabSeat> allSeats = labSeatDAO.getAllLabSeats();
        List<LabSeat> availableSeats = new ArrayList<>();

        for (LabSeat seat : allSeats) {
            List<LabSeatReservation> reservations = labSeatReservationDAO.getReservationsBySeatIdAndDate(seat.getSeatID(), requestedTime);
            boolean isReserved = false;
            for (LabSeatReservation res : reservations) {
                if (res.getApprovalStatus() == Reservation.ReservationStatus.APPROVED || res.getApprovalStatus() == Reservation.ReservationStatus.PENDING) {
                    LocalDateTime resStart = res.getDateTime();
                    LocalDateTime resEnd = resStart.plusHours(res.getDurationInHours());

                    if ((requestedTime.isEqual(resStart) || requestedTime.isAfter(resStart)) && requestedTime.isBefore(resEnd)) {
                        isReserved = true;
                        break;
                    }
                }
            }
            if (!isReserved) {
                availableSeats.add(seat);
            }
        }
        return availableSeats;
    }
}
