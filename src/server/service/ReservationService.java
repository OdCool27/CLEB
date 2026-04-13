package service;

import model.Reservation;

import java.util.List;

public interface ReservationService<T extends Reservation> {
    boolean createReservation(T reservation);
    boolean cancelReservation(int reservationID);
    T getReservationById(int reservationID);
    List<T> getReservationsByStudent(String studentID);
    List<T> getReservationsByStatus(Reservation.ReservationStatus status);
    List<T> getAllReservations();
    boolean approveReservation(int reservationID, String approvedBy);
    boolean denyReservation(int reservationID, String approvedBy);
    boolean completeReservation(int reservationID);

}
