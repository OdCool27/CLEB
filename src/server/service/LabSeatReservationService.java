package server.service;

import server.dao.LabSeatReservationDAO;
import server.model.LabSeatReservation;
import server.model.Reservation;
import java.sql.Connection;
import java.util.List;

public class LabSeatReservationService implements IReservationService<LabSeatReservation> {
    private LabSeatReservationDAO reservationDAO;

    public LabSeatReservationService(Connection connection) {
        this.reservationDAO = new LabSeatReservationDAO(connection);
    }

    @Override
    public boolean createReservation(LabSeatReservation reservation) {
        return reservationDAO.saveLabSeatReservation(reservation);
    }

    @Override
    public boolean cancelReservation(int reservationID) {
        LabSeatReservation reservation = reservationDAO.getLabSeatReservationById(reservationID);
        if (reservation != null) {
            reservation.setApprovalStatus(Reservation.ReservationStatus.CANCELLED);
            return reservationDAO.updateLabSeatReservation(reservation);
        }
        return false;
    }

    @Override
    public LabSeatReservation getReservationById(int reservationID) {
        return reservationDAO.getLabSeatReservationById(reservationID);
    }

    @Override
    public List<LabSeatReservation> getReservationsByStudent(String studentID) {
        return reservationDAO.getReservationsByStudentId(studentID);
    }

    @Override
    public List<LabSeatReservation> getReservationsByStatus(Reservation.ReservationStatus status) {
        return reservationDAO.getReservationsByStatus(status.toString());
    }

    @Override
    public List<LabSeatReservation> getAllReservations() {
        return reservationDAO.getAllLabSeatReservations();
    }

    @Override
    public boolean approveReservation(int reservationID, String approvedBy) {
        LabSeatReservation reservation = reservationDAO.getLabSeatReservationById(reservationID);
        if (reservation != null) {
            reservation.setApprovalStatus(Reservation.ReservationStatus.APPROVED);
            reservation.setApprovedBy(approvedBy);
            return reservationDAO.updateLabSeatReservation(reservation);
        }
        return false;
    }

    @Override
    public boolean denyReservation(int reservationID, String approvedBy) {
        LabSeatReservation reservation = reservationDAO.getLabSeatReservationById(reservationID);
        if (reservation != null) {
            reservation.setApprovalStatus(Reservation.ReservationStatus.REJECTED);
            reservation.setApprovedBy(approvedBy);
            return reservationDAO.updateLabSeatReservation(reservation);
        }
        return false;
    }
}
