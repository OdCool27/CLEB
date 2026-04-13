package service;

import dao.EquipmentReservationDAO;
import dao.EmployeeDAO;
import dao.StudentDAO;
import model.EquipmentReservation;
import model.Reservation;
import model.User;

import java.sql.Connection;
import java.util.List;

public class EquipmentReservationService implements ReservationService<EquipmentReservation>{
    private EquipmentReservationDAO reservationDAO;
    private StudentDAO studentDAO;
    private EmployeeDAO employeeDAO;

    public EquipmentReservationService(Connection connection) {
        this.reservationDAO = new EquipmentReservationDAO(connection);
        this.studentDAO = new StudentDAO(connection);
        this.employeeDAO = new EmployeeDAO(connection);
    }

    @Override
    public boolean createReservation(EquipmentReservation reservation) {
        boolean success = reservationDAO.saveEquipmentReservation(reservation);
        if (success) {
            notifyReservationOwner(reservation, NotificationType.CREATED);
        }
        return success;
    }

    @Override
    public boolean cancelReservation(int reservationID) {
        return cancelReservation(reservationID, null);
    }

    public boolean cancelReservation(int reservationID, String approvedBy) {
        EquipmentReservation reservation = reservationDAO.getEquipmentReservationById(reservationID);
        if (reservation != null) {
            reservation.setApprovalStatus(Reservation.ReservationStatus.CANCELLED);
            if (approvedBy != null && !approvedBy.isBlank()) {
                reservation.setApprovedBy(approvedBy);
            }
            boolean success = reservationDAO.updateEquipmentReservation(reservation);
            if (success) {
                notifyReservationOwner(reservation, NotificationType.CANCELLED);
            }
            return success;
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
            boolean success = reservationDAO.updateEquipmentReservation(reservation);
            if (success) {
                notifyReservationOwner(reservation, NotificationType.MODIFIED);
            }
            return success;
        }
        return false;
    }

    @Override
    public boolean denyReservation(int reservationID, String approvedBy) {
        EquipmentReservation reservation = reservationDAO.getEquipmentReservationById(reservationID);
        if (reservation != null) {
            reservation.setApprovalStatus(Reservation.ReservationStatus.REJECTED);
            reservation.setApprovedBy(approvedBy);
            boolean success = reservationDAO.updateEquipmentReservation(reservation);
            if (success) {
                notifyReservationOwner(reservation, NotificationType.MODIFIED);
            }
            return success;
        }
        return false;
    }

    @Override
    public boolean completeReservation(int reservationID) {
        EquipmentReservation reservation = reservationDAO.getEquipmentReservationById(reservationID);
        if (reservation != null) {
            reservation.setApprovalStatus(Reservation.ReservationStatus.COMPLETE);
            boolean success = reservationDAO.updateEquipmentReservation(reservation);
            if (success) {
                notifyReservationOwner(reservation, NotificationType.MODIFIED);
            }
            return success;
        }
        return false;
    }

    // Reservation notifications are resolved by identifier so both students and employees can receive them.
    private void notifyReservationOwner(EquipmentReservation reservation, NotificationType type) {
        String email = resolveReservationOwnerEmail(reservation.getStudentID());
        if (email == null || email.isBlank()) {
            return;
        }

        switch (type) {
            case CREATED -> NotificationService.sendReservationCreationNotification(email, reservation);
            case MODIFIED -> NotificationService.sendReservationModificationNotification(email, reservation);
            case CANCELLED -> NotificationService.sendReservationDeletionNotification(email, reservation);
        }
    }

    private String resolveReservationOwnerEmail(String identifier) {
        User owner = studentDAO.getStudentByStudentId(identifier);
        if (owner == null) {
            owner = employeeDAO.getEmployeeById(identifier);
        }
        return owner != null ? owner.getEmail() : null;
    }

    private enum NotificationType {
        CREATED,
        MODIFIED,
        CANCELLED
    }
}
