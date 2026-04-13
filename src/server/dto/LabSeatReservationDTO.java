package dto;

import model.LabSeatReservation;
import model.Reservation;

import java.io.Serializable;
import java.time.LocalDateTime;

public class LabSeatReservationDTO extends ReservationDTO implements Serializable {
    private LabSeatDTO reservedSeat;

    public LabSeatReservationDTO() {
        super();
    }

    public LabSeatReservationDTO(int reservationID, String studentID, LocalDateTime dateTime, int durationInHours, Reservation.ReservationStatus approvalStatus, String approvedBy, LocalDateTime lastUpdated, LabSeatDTO reservedSeat) {
        super(reservationID, studentID, dateTime, durationInHours, approvalStatus, approvedBy, lastUpdated);
        this.reservedSeat = reservedSeat;
    }

    public LabSeatReservationDTO(LabSeatReservation labSeatReservation) {
        super(labSeatReservation);
        if (labSeatReservation.getReservedSeat() != null) {
            this.reservedSeat = new LabSeatDTO(labSeatReservation.getReservedSeat());
        }
    }

    public LabSeatDTO getReservedSeat() {
        return reservedSeat;
    }
}
