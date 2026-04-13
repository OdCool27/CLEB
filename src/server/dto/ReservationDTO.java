package dto;

import model.Reservation;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ReservationDTO implements Serializable {
    protected int reservationID;
    protected String studentID;
    protected LocalDateTime dateTime;
    protected int durationInHours;
    protected Reservation.ReservationStatus approvalStatus;
    protected String approvedBy;
    protected LocalDateTime lastUpdated;

    public ReservationDTO() {
    }

    public ReservationDTO(int reservationID, String studentID, LocalDateTime dateTime, int durationInHours, Reservation.ReservationStatus approvalStatus, String approvedBy, LocalDateTime lastUpdated) {
        this.reservationID = reservationID;
        this.studentID = studentID;
        this.dateTime = dateTime;
        this.durationInHours = durationInHours;
        this.approvalStatus = approvalStatus;
        this.approvedBy = approvedBy;
        this.lastUpdated = lastUpdated;
    }

    public ReservationDTO(Reservation reservation) {
        this.reservationID = reservation.getReservationID();
        this.studentID = reservation.getStudentID();
        this.dateTime = reservation.getDateTime();
        this.durationInHours = reservation.getDurationInHours();
        this.approvalStatus = reservation.getApprovalStatus();
        this.approvedBy = reservation.getApprovedBy();
        this.lastUpdated = reservation.getLastUpdated();
    }

    public int getReservationID() {
        return reservationID;
    }

    public String getStudentID() {
        return studentID;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public int getDurationInHours() {
        return durationInHours;
    }

    public Reservation.ReservationStatus getApprovalStatus() {
        return approvalStatus;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
}
