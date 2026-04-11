package server.service;

import server.dao.EquipmentReservationDAO;
import server.model.EquipmentReservation;
import server.model.Reservation;
import java.sql.Connection;
import java.util.List;

public class EquipmentReservationService implements IReservationService<EquipmentReservation> {
    private EquipmentReservationDAO reservationDAO;

    public EquipmentReservationService(Connection connection) {
        this.reservationDAO = new EquipmentReservationDAO(connection);
    }

    @Override
    public boolean createReservation(EquipmentReservation reservation) {
        return reservationDAO.saveEquipmentReservation(reservation);
    }

    @Override
    public boolean cancelReservation(int reservationID) {
        EquipmentReservation reservation = reservationDAO.getEquipmentReservationById(reservationID);
        if (reservation != null) {
            reservation.setApprovalStatus(Reservation.ReservationStatus.CANCELLED);
            return reservationDAO.updateEquipmentReservation(reservation);
        }
        return false;
    }

    @Override
    public EquipmentReservation getReservationById(int reservationID) {
        return reservationDAO.getEquipmentReservationById(reservationID);
    }

    @Override
    public List<EquipmentReservation> getReservationsByStudent(String studentID) {
        return reservationDAO.getReservationsByStudentId(studentID);
    }

    @Override
    public List<EquipmentReservation> getReservationsByStatus(Reservation.ReservationStatus status) {
        return reservationDAO.getReservationsByStatus(status.toString());
    }

    @Override
    public List<EquipmentReservation> getAllReservations() {
        return reservationDAO.getAllEquipmentReservations();
    }

    @Override
    public boolean approveReservation(int reservationID, String approvedBy) {
        EquipmentReservation reservation = reservationDAO.getEquipmentReservationById(reservationID);
        if (reservation != null) {
            reservation.setApprovalStatus(Reservation.ReservationStatus.APPROVED);
            reservation.setApprovedBy(approvedBy);
            return reservationDAO.updateEquipmentReservation(reservation);
        }
        return false;
    }

    @Override
    public boolean denyReservation(int reservationID, String approvedBy) {
        EquipmentReservation reservation = reservationDAO.getEquipmentReservationById(reservationID);
        if (reservation != null) {
            reservation.setApprovalStatus(Reservation.ReservationStatus.REJECTED);
            reservation.setApprovedBy(approvedBy);
            return reservationDAO.updateEquipmentReservation(reservation);
        }
        return false;
    }
}
