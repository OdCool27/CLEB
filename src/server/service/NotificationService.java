package service;

import model.Reservation;
import model.User;
import util.SMTPUtil;

public class NotificationService {
    public static void sendReservationCreationNotification(String to, Reservation reservationDetails) {
        String subject = "CLEB Reservation Created Successfully";
        String content = "<h1>Reservation Confirmation</h1>" +
                "<p>Your reservation has been created with the following details:</p>" +
                "<p>" + reservationDetails + "</p>";
        SMTPUtil.sendEmail(to, subject, content);
    }

    public static void sendReservationModificationNotification(String to, Reservation reservationDetails) {
        String subject = "CLEB Reservation Modified Successfully";
        String content = "<h1>Reservation Modification</h1>" +
                "<p>Your reservation has been updated. The new details are:</p>" +
                "<p>" + reservationDetails + "</p>";
        SMTPUtil.sendEmail(to, subject, content);
    }

    public static void sendReservationDeletionNotification(String to, Reservation reservationId) {
        String subject = "CLEB Reservation Cancelled";
        String content = "<h1>Reservation Cancellation</h1>" +
                "<p>Your reservation with ID: " + reservationId + " has been cancelled.</p>";
        SMTPUtil.sendEmail(to, subject, content);
    }

    public static void sendAccountCreationNotification(User modificationDetails) {
        String subject = "CLEB Account Creation";
        String content = "<h1>Account Created</h1>" +
                "<p>Your account information has been added:</p>" +
                "<p>" + modificationDetails + "</p>" +
                "<p>If you did not request this action, please contact support immediately.</p>";
        SMTPUtil.sendEmail(modificationDetails.getEmail(), subject, content);
    }

    public static void sendAccountModificationNotification(User modificationDetails) {
        String subject = "CLEB Account Details Updated";
        String content = "<h1>Account Update</h1>" +
                "<p>Your account information has been modified:</p>" +
                "<p>" + modificationDetails + "</p>" +
                "<p>If you did not perform this action, please contact support immediately.</p>";
        SMTPUtil.sendEmail(modificationDetails.getEmail(), subject, content);
    }
}
