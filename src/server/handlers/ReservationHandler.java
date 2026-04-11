package server.handlers;

import server.dispatcher.RequestHandler;
import server.envelopes.RequestEnvelope;
import server.envelopes.ResponseEnvelope;
import server.model.EquipmentReservation;
import server.model.LabSeatReservation;
import server.model.Reservation;
import server.service.EquipmentReservationService;
import server.service.LabSeatReservationService;

import java.sql.Connection;
import java.util.List;

public class ReservationHandler implements RequestHandler<Object> {
    private EquipmentReservationService equipmentReservationService;
    private LabSeatReservationService labSeatReservationService;

    public ReservationHandler(Connection conn) {
        this.equipmentReservationService = new EquipmentReservationService(conn);
        this.labSeatReservationService = new LabSeatReservationService(conn);
    }

    @Override
    public ResponseEnvelope<?> handleRequest(RequestEnvelope<Object> request, Connection conn) {
        String action = request.getAction();
        Object payload = request.getPayload();

        try {
            switch (action) {
                case "CREATE_EQUIPMENT_RESERVATION":
                    return createEquipmentReservation(request, (EquipmentReservation) payload);
                case "CREATE_LAB_SEAT_RESERVATION":
                    return createLabSeatReservation(request, (LabSeatReservation) payload);
                case "CANCEL_EQUIPMENT_RESERVATION":
                    return cancelEquipmentReservation(request, (Integer) payload);
                case "CANCEL_LAB_SEAT_RESERVATION":
                    return cancelLabSeatReservation(request, (Integer) payload);
                case "GET_EQUIPMENT_RESERVATIONS_BY_STUDENT":
                    return getEquipmentReservationsByStudent(request, (String) payload);
                case "GET_LAB_SEAT_RESERVATIONS_BY_STUDENT":
                    return getLabSeatReservationsByStudent(request, (String) payload);
                case "GET_ALL_EQUIPMENT_RESERVATIONS":
                    return getAllEquipmentReservations(request);
                case "GET_ALL_LAB_SEAT_RESERVATIONS":
                    return getAllLabSeatReservations(request);
                case "APPROVE_EQUIPMENT_RESERVATION":
                    return approveEquipmentReservation(request, (Integer) payload, "SYSTEM"); // Approver ID should ideally come from request context
                case "APPROVE_LAB_SEAT_RESERVATION":
                    return approveLabSeatReservation(request, (Integer) payload, "SYSTEM");
                default:
                    return new ResponseEnvelope<>(request.getCorrelationId(), "Action not supported by ReservationHandler", "FAIL", null);
            }
        } catch (Exception e) {
            return new ResponseEnvelope<>(request.getCorrelationId(), "Error: " + e.getMessage(), "FAIL", null);
        }
    }

    private ResponseEnvelope<?> createEquipmentReservation(RequestEnvelope<Object> request, EquipmentReservation res) {
        boolean success = equipmentReservationService.createReservation(res);
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Reservation created" : "Failed", success ? "SUCCESS" : "FAIL", success);
    }

    private ResponseEnvelope<?> createLabSeatReservation(RequestEnvelope<Object> request, LabSeatReservation res) {
        boolean success = labSeatReservationService.createReservation(res);
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Reservation created" : "Failed", success ? "SUCCESS" : "FAIL", success);
    }

    private ResponseEnvelope<?> cancelEquipmentReservation(RequestEnvelope<Object> request, int id) {
        boolean success = equipmentReservationService.cancelReservation(id);
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Reservation cancelled" : "Failed", success ? "SUCCESS" : "FAIL", success);
    }

    private ResponseEnvelope<?> cancelLabSeatReservation(RequestEnvelope<Object> request, int id) {
        boolean success = labSeatReservationService.cancelReservation(id);
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Reservation cancelled" : "Failed", success ? "SUCCESS" : "FAIL", success);
    }

    private ResponseEnvelope<?> getEquipmentReservationsByStudent(RequestEnvelope<Object> request, String studentId) {
        List<EquipmentReservation> list = equipmentReservationService.getReservationsByStudent(studentId);
        return new ResponseEnvelope<>(request.getCorrelationId(), "Reservations retrieved", "SUCCESS", list);
    }

    private ResponseEnvelope<?> getLabSeatReservationsByStudent(RequestEnvelope<Object> request, String studentId) {
        List<LabSeatReservation> list = labSeatReservationService.getReservationsByStudent(studentId);
        return new ResponseEnvelope<>(request.getCorrelationId(), "Reservations retrieved", "SUCCESS", list);
    }

    private ResponseEnvelope<?> getAllEquipmentReservations(RequestEnvelope<Object> request) {
        List<EquipmentReservation> list = equipmentReservationService.getAllReservations();
        return new ResponseEnvelope<>(request.getCorrelationId(), "All reservations retrieved", "SUCCESS", list);
    }

    private ResponseEnvelope<?> getAllLabSeatReservations(RequestEnvelope<Object> request) {
        List<LabSeatReservation> list = labSeatReservationService.getAllReservations();
        return new ResponseEnvelope<>(request.getCorrelationId(), "All reservations retrieved", "SUCCESS", list);
    }

    private ResponseEnvelope<?> approveEquipmentReservation(RequestEnvelope<Object> request, int id, String approver) {
        boolean success = equipmentReservationService.approveReservation(id, approver);
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Reservation approved" : "Failed", success ? "SUCCESS" : "FAIL", success);
    }

    private ResponseEnvelope<?> approveLabSeatReservation(RequestEnvelope<Object> request, int id, String approver) {
        boolean success = labSeatReservationService.approveReservation(id, approver);
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Reservation approved" : "Failed", success ? "SUCCESS" : "FAIL", success);
    }
}
