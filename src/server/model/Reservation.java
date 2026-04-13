package model;

import java.time.LocalDateTime;

public class Reservation {
    protected int reservationID;
    protected String studentID;
    protected LocalDateTime dateTime;
    protected int durationInHours;
    protected ReservationStatus approvalStatus;
    protected String approvedBy;
    protected LocalDateTime lastUpdated;

    public enum ReservationStatus {PENDING, APPROVED, REJECTED, CANCELLED, COMPLETE}
    /*
        Pending -> Reservation awaits approval
        Approved -> Staff has approved reservation request
        Rejected -> Staff has denied reservation request
        Cancelled -> Reservation (pending or approved) has been cancelled by Student or Staff
     */

    public Reservation() {
        reservationID = 0;
        studentID = "";
        dateTime = null;
        durationInHours = 0;
        approvalStatus = ReservationStatus.PENDING;
        approvedBy = "";
    }

    public Reservation(int reservationID, String studentID, LocalDateTime dateTime, int durationInHours, ReservationStatus approvalStatus,
                       String approvedBy) {
        this.reservationID = reservationID;
        this.studentID = studentID;
        this.dateTime = dateTime;
        this.durationInHours = durationInHours;
        this.approvalStatus = approvalStatus;
        this.approvedBy = approvedBy;
        this.lastUpdated = LocalDateTime.now();
    }

    public int getReservationID() {
        return reservationID;
    }

    public void setReservationID(int reservationID) {
        this.reservationID = reservationID;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getDurationInHours() {
        return durationInHours;
    }

    public void setDurationInHours(int durationInHours) {
        this.durationInHours = durationInHours;
    }

    public ReservationStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ReservationStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return  "Reservation ID: " + reservationID + "\n" +
                "Student ID: " + studentID + "\n" +
                "Date & Time: " + dateTime + "\n" +
                "Duration (hrs): " + durationInHours + "\n" +
                "Approval Status: " + approvalStatus.toString() + "\n" +
                "Approved By: " + approvedBy +
                "Last Updated: " + lastUpdated.toString() +
                "\n";
    }
}
