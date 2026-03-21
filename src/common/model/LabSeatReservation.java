package common.model;

import java.time.LocalDateTime;

public class LabSeatReservation extends Reservation {
    private Lab reservedAt;

    public LabSeatReservation() {
        super();
        reservedAt = null;
    }

    public LabSeatReservation(int reservationID, int studentID, LocalDateTime dateTime,
                           int durationInHours, String approvalStatus, Employee approvedBy,
                           Lab reservedAt) {
        super(reservationID, studentID, dateTime, durationInHours, approvalStatus, approvedBy);
        this.reservedAt = reservedAt;
    }

    public Lab getReservedAt() {
        return reservedAt;
    }

    public void setReservedAt(Lab reservedAt) {
        this.reservedAt = reservedAt;
    }

    @Override
    public String toString() {
        return "Seat Reservation Information\n====================\n" +
                "Reservation ID: " + reservationID + "\n" +
                "Student ID: " + studentID + "\n" +
                "Date & Time: " + dateTime + "\n" +
                "Duration (hrs): " + durationInHours + "\n" +
                "Approval Status: " + approvalStatus + "\n" +
                "Approved By: " + approvedBy + "\n" +
                "Reserved At (Lab): " + reservedAt + "\n";
    }
}
