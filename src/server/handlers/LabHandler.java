package handlers;

import dto.LabDTO;
import dto.LabSeatDTO;
import envelopes.RequestEnvelope;
import envelopes.ResponseEnvelope;
import model.Lab;
import model.LabSeat;
import model.Location;
import service.LabService;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
                    return addLab(request, (LabDTO) payload);
                case "UPDATE_LAB":
                    return updateLab(request, (LabDTO) payload);
                case "DELETE_LAB":
                    return deleteLab(request, (String) payload);
                case "ADD_LAB_SEAT":
                    return addLabSeat(request, (LabSeatDTO) payload);
                default:
                    return new ResponseEnvelope<>(request.getCorrelationId(), "Action not supported by LabHandler", "FAIL", null);
            }
        } catch (Exception e) {
            return new ResponseEnvelope<>(request.getCorrelationId(), "Error: " + e.getMessage(), "FAIL", null);
        }
    }

    private ResponseEnvelope<?> getAllLabs(RequestEnvelope<Object> request) {
        List<Lab> list = labService.getAllLabs();
        List<LabDTO> listDTO = new ArrayList<>();
        for (Lab lab : list) {
            listDTO.add(new LabDTO(lab));
        }
        return new ResponseEnvelope<>(request.getCorrelationId(), "Labs retrieved", "SUCCESS", listDTO);
    }

    private ResponseEnvelope<?> getLabById(RequestEnvelope<Object> request, String id) {
        Lab lab = labService.getLabById(id);
        LabDTO labDTO = lab != null ? new LabDTO(lab) : null;
        return new ResponseEnvelope<>(request.getCorrelationId(), lab != null ? "Lab found" : "Lab not found", lab != null ? "SUCCESS" : "FAIL", labDTO);
    }

    private ResponseEnvelope<?> getSeatsByLab(RequestEnvelope<Object> request, String labId) {
        List<LabSeat> list = labService.getSeatsByLab(labId);
        List<LabSeatDTO> listDTO = new ArrayList<>();
        for (LabSeat labSeat : list) {
            listDTO.add(new LabSeatDTO(labSeat));
        }
        return new ResponseEnvelope<>(request.getCorrelationId(), "Seats retrieved", "SUCCESS", listDTO);
    }

    private ResponseEnvelope<?> getAvailableSeatsAtTime(RequestEnvelope<Object> request, LocalDateTime time) {
        List<LabSeat> list = labService.getAvailableSeatsAtTime(time);
        List<LabSeatDTO> listDTO = new ArrayList<>();
        for (LabSeat labSeat : list) {
            listDTO.add(new LabSeatDTO(labSeat));
        }
        return new ResponseEnvelope<>(request.getCorrelationId(), "Available seats retrieved", "SUCCESS", listDTO);
    }

    private ResponseEnvelope<?> addLab(RequestEnvelope<Object> request, LabDTO labDto) {
        boolean success = labService.addLab(convertToModel(labDto));
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Lab added" : "Failed", success ? "SUCCESS" : "FAIL", success);
    }

    private ResponseEnvelope<?> updateLab(RequestEnvelope<Object> request, LabDTO labDto) {
        boolean success = labService.updateLab(convertToModel(labDto));
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Lab updated" : "Failed", success ? "SUCCESS" : "FAIL", success);
    }

    private ResponseEnvelope<?> deleteLab(RequestEnvelope<Object> request, String labId) {
        boolean success = labService.deleteLab(labId);
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Lab deleted" : "Failed", success ? "SUCCESS" : "FAIL", success);
    }

    private ResponseEnvelope<?> addLabSeat(RequestEnvelope<Object> request, LabSeatDTO seatDto) {
        boolean success = labService.addLabSeat(convertToModel(seatDto));
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Seat added" : "Failed", success ? "SUCCESS" : "FAIL", success);
    }

    private Lab convertToModel(LabDTO dto) {
        if (dto == null) return null;
        Lab lab = new Lab();
        lab.setLabID(dto.getLabID());
        lab.setName(dto.getName());
        lab.setNumOfSeats(dto.getNumOfSeats());
        if (dto.getLocation() != null) {
            Location loc = new Location();
            loc.setRoomName(dto.getLocation().getRoomName());
            loc.setBuilding(dto.getLocation().getBuilding());
            loc.setFloor(dto.getLocation().getFloor());
            loc.setCampus(dto.getLocation().getCampus());
            lab.setLocation(loc);
        }
        return lab;
    }

    private LabSeat convertToModel(LabSeatDTO dto) {
        if (dto == null) return null;
        LabSeat seat = new LabSeat();
        seat.setSeatID(dto.getSeatID());
        seat.setSeatCode(dto.getSeatCode());
        if (dto.getSeatLocation() != null) {
            seat.setSeatLocation(convertToModel(dto.getSeatLocation()));
        }
        return seat;
    }
}
