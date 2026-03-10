package common.model;

import java.time.LocalDateTime;

public class Reservation {
    protected int reservationID;
    protected int studentID;
    protected LocalDateTime dateTime;
    protected int durationInHours;
    protected String approvalStatus;
    protected Employee approvedBy;

    public Reservation() {
        reservationID = 0;
        studentID = 0;
        dateTime = null;
        durationInHours = 0;
        approvalStatus = "";
        approvedBy = null;
    }

    public Reservation(int reservationID, int studentID, LocalDateTime dateTime, int durationInHours, String approvalStatus, Employee approvedBy) {
        this.reservationID = reservationID;
        this.studentID = studentID;
        this.dateTime = dateTime;
        this.durationInHours = durationInHours;
        this.approvalStatus = approvalStatus;
        this.approvedBy = approvedBy;
    }

    public int getReservationID() {
        return reservationID;
    }

    public void setReservationID(int reservationID) {
        this.reservationID = reservationID;
    }

    public int getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
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

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Employee getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Employee approvedBy) {
        this.approvedBy = approvedBy;
    }

    @Override
    public String toString() {
        return "Reservation Information\n====================\n" +
                "Reservation ID: " + reservationID + "\n" +
                "Student ID: " + studentID + "\n" +
                "Date & Time: " + dateTime + "\n" +
                "Duration (hrs): " + durationInHours + "\n" +
                "Approval Status: " + approvalStatus + "\n" +
                "Approved By: " + approvedBy + "\n";
    }
}
