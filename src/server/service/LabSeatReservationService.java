package service;

import dao.LabSeatReservationDAO;
import dao.EmployeeDAO;
import dao.StudentDAO;
import model.LabSeatReservation;
import model.Reservation;
import model.User;

import java.sql.Connection;
import java.util.List;

public class LabSeatReservationService implements ReservationService<LabSeatReservation> {
    private LabSeatReservationDAO reservationDAO;
    private StudentDAO studentDAO;
    private EmployeeDAO employeeDAO;

    public LabSeatReservationService(Connection connection) {
        this.reservationDAO = new LabSeatReservationDAO(connection);
        this.studentDAO = new StudentDAO(connection);
        this.employeeDAO = new EmployeeDAO(connection);
    }

    @Override
    public boolean createReservation(LabSeatReservation reservation) {
        boolean success = reservationDAO.saveLabSeatReservation(reservation);
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
        LabSeatReservation reservation = reservationDAO.getLabSeatReservationById(reservationID);
        if (reservation != null) {
            reservation.setApprovalStatus(Reservation.ReservationStatus.CANCELLED);
            if (approvedBy != null && !approvedBy.isBlank()) {
                reservation.setApprovedBy(approvedBy);
            }
            boolean success = reservationDAO.updateLabSeatReservation(reservation);
            if (success) {
                notifyReservationOwner(reservation, NotificationType.CANCELLED);
            }
            return success;
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
            boolean success = reservationDAO.updateLabSeatReservation(reservation);
            if (success) {
                notifyReservationOwner(reservation, NotificationType.MODIFIED);
            }
            return success;
        }
        return false;
    }

    @Override
    public boolean denyReservation(int reservationID, String approvedBy) {
        LabSeatReservation reservation = reservationDAO.getLabSeatReservationById(reservationID);
        if (reservation != null) {
            reservation.setApprovalStatus(Reservation.ReservationStatus.REJECTED);
            reservation.setApprovedBy(approvedBy);
            boolean success = reservationDAO.updateLabSeatReservation(reservation);
            if (success) {
                notifyReservationOwner(reservation, NotificationType.MODIFIED);
            }
            return success;
        }
        return false;
    }

    @Override
    public boolean completeReservation(int reservationID) {
        LabSeatReservation reservation = reservationDAO.getLabSeatReservationById(reservationID);
        if (reservation != null) {
            reservation.setApprovalStatus(Reservation.ReservationStatus.COMPLETE);
            boolean success = reservationDAO.updateLabSeatReservation(reservation);
            if (success) {
                notifyReservationOwner(reservation, NotificationType.MODIFIED);
            }
            return success;
        }
        return false;
    }

    // Resolve the reservation owner's email from either studentID or empID before notifying.
    private void notifyReservationOwner(LabSeatReservation reservation, NotificationType type) {
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
