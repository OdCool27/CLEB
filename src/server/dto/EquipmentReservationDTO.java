package dto;

import model.EquipmentReservation;
import model.Reservation;

import java.io.Serializable;
import java.time.LocalDateTime;

public class EquipmentReservationDTO extends ReservationDTO implements Serializable {
    private EquipmentDTO reservedItem;

    public EquipmentReservationDTO() {
        super();
    }

    public EquipmentReservationDTO(int reservationID, String studentID, LocalDateTime dateTime, int durationInHours, Reservation.ReservationStatus approvalStatus, String approvedBy, LocalDateTime lastUpdated, EquipmentDTO reservedItem) {
        super(reservationID, studentID, dateTime, durationInHours, approvalStatus, approvedBy, lastUpdated);
        this.reservedItem = reservedItem;
    }

    public EquipmentReservationDTO(EquipmentReservation equipmentReservation) {
        super(equipmentReservation);
        if (equipmentReservation.getReservedItem() != null) {
            this.reservedItem = new EquipmentDTO(equipmentReservation.getReservedItem());
        }
    }

    public EquipmentDTO getReservedItem() {
        return reservedItem;
    }
}
