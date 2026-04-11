package server.handlers;

import server.dispatcher.RequestHandler;
import server.envelopes.RequestEnvelope;
import server.envelopes.ResponseEnvelope;
import server.model.Lab;
import server.model.LabSeat;
import server.service.LabService;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

public class LabHandler implements RequestHandler<Object> {
    private LabService labService;

    public LabHandler(Connection conn) {
        this.labService = new LabService(conn);
    }

    @Override
    public ResponseEnvelope<?> handleRequest(RequestEnvelope<Object> request, Connection conn) {
        String action = request.getAction();
        Object payload = request.getPayload();

        try {
            switch (action) {
                case "GET_ALL_LABS":
                    return getAllLabs(request);
                case "GET_LAB_BY_ID":
                    return getLabById(request, (String) payload);
                case "GET_SEATS_BY_LAB":
                    return getSeatsByLab(request, (String) payload);
                case "GET_AVAILABLE_SEATS_AT_TIME":
                    return getAvailableSeatsAtTime(request, (LocalDateTime) payload);
                case "ADD_LAB":
                    return addLab(request, (Lab) payload);
                case "ADD_LAB_SEAT":
                    return addLabSeat(request, (LabSeat) payload);
                default:
                    return new ResponseEnvelope<>(request.getCorrelationId(), "Action not supported by LabHandler", "FAIL", null);
            }
        } catch (Exception e) {
            return new ResponseEnvelope<>(request.getCorrelationId(), "Error: " + e.getMessage(), "FAIL", null);
        }
    }

    private ResponseEnvelope<?> getAllLabs(RequestEnvelope<Object> request) {
        List<Lab> list = labService.getAllLabs();
        return new ResponseEnvelope<>(request.getCorrelationId(), "Labs retrieved", "SUCCESS", list);
    }

    private ResponseEnvelope<?> getLabById(RequestEnvelope<Object> request, String id) {
        Lab lab = labService.getLabById(id);
        return new ResponseEnvelope<>(request.getCorrelationId(), lab != null ? "Lab found" : "Lab not found", lab != null ? "SUCCESS" : "FAIL", lab);
    }

    private ResponseEnvelope<?> getSeatsByLab(RequestEnvelope<Object> request, String labId) {
        List<LabSeat> list = labService.getSeatsByLab(labId);
        return new ResponseEnvelope<>(request.getCorrelationId(), "Seats retrieved", "SUCCESS", list);
    }

    private ResponseEnvelope<?> getAvailableSeatsAtTime(RequestEnvelope<Object> request, LocalDateTime time) {
        List<LabSeat> list = labService.getAvailableSeatsAtTime(time);
        return new ResponseEnvelope<>(request.getCorrelationId(), "Available seats retrieved", "SUCCESS", list);
    }

    private ResponseEnvelope<?> addLab(RequestEnvelope<Object> request, Lab lab) {
        boolean success = labService.addLab(lab);
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Lab added" : "Failed", success ? "SUCCESS" : "FAIL", success);
    }

    private ResponseEnvelope<?> addLabSeat(RequestEnvelope<Object> request, LabSeat seat) {
        boolean success = labService.addLabSeat(seat);
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Seat added" : "Failed", success ? "SUCCESS" : "FAIL", success);
    }
}
