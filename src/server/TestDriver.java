package server;

import server.envelopes.RequestEnvelope;
import server.model.LabSeat;
import server.model.LabSeatReservation;
import server.model.Student;
import server.model.User;
import server.service.NotificationService;

import java.util.UUID;

public class TestDriver {
    public static void main(String[] args) {
        NotificationService notificationService = new NotificationService();
        notificationService.sendEmail("odanecollins@live.com", "Test", "Testing this Email Notification");
    }
}
