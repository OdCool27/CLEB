package server.service;

import server.model.Reservation;
import java.util.List;

public interface IReservationService<T extends Reservation> {
    boolean createReservation(T reservation);
    boolean cancelReservation(int reservationID);
    T getReservationById(int reservationID);
    List<T> getReservationsByStudent(String studentID);
    List<T> getReservationsByStatus(Reservation.ReservationStatus status);
    List<T> getAllReservations();
    boolean approveReservation(int reservationID, String approvedBy);
    boolean denyReservation(int reservationID, String approvedBy);
}
