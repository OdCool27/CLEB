package common.model;

import java.time.LocalDateTime;

public class EquipmentReservation extends Reservation {
    private Equipment reservedItem;

    public EquipmentReservation() {
        super();
        reservedItem = null;
    }

    public EquipmentReservation(int reservationID, int studentID, LocalDateTime dateTime,
                                int durationInHours, String approvalStatus, Employee approvedBy,
                                Equipment reservedItem) {
        super(reservationID, studentID, dateTime, durationInHours, approvalStatus, approvedBy);
        this.reservedItem = reservedItem;
    }

    public Equipment getReservedItem() {
        return reservedItem;
    }

    public void setReservedItem(Equipment reservedItem) {
        this.reservedItem = reservedItem;
    }

    @Override
    public String toString() {
        return "Equipment Reservation Information\n====================\n" +
                "Reservation ID: " + reservationID + "\n" +
                "Student ID: " + studentID + "\n" +
                "Date & Time: " + dateTime + "\n" +
                "Duration (hrs): " + durationInHours + "\n" +
                "Approval Status: " + approvalStatus + "\n" +
                "Approved By: " + approvedBy + "\n" +
                "Reserved Item: " + reservedItem + "\n";
    }
}
