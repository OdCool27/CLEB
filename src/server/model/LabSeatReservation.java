package model;

import java.time.LocalDateTime;

public class LabSeatReservation extends Reservation {
    private LabSeat reservedSeat;

    public LabSeatReservation() {
        super();
        reservedSeat = new LabSeat();
    }

    public LabSeatReservation(int reservationID, String studentID, LocalDateTime dateTime,
                              int durationInHours, ReservationStatus approvalStatus, String approvedBy,
                              LabSeat reservedSeat) {
        super(reservationID, studentID, dateTime, durationInHours, approvalStatus, approvedBy);
        this.reservedSeat = reservedSeat;
    }

    public LabSeat getReservedSeat() {
        return reservedSeat;
    }

    public void setReservedSeat(LabSeat reservedSeat) {
        this.reservedSeat = reservedSeat;
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
                "Reserved Seat: " + reservedSeat.getSeatID() + "\n" +
                "Lab: " + reservedSeat.getSeatLocation().getLabID() + "\n" +
                "Campus: " + reservedSeat.getSeatLocation().getLocation().getCampus() + "\n";

    }
}
