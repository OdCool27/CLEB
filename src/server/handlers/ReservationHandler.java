package handlers;

import dto.EquipmentReservationDTO;
import dto.EquipmentDTO;
import dto.LabSeatReservationDTO;
import dto.LabSeatDTO;
import envelopes.RequestEnvelope;
import envelopes.ResponseEnvelope;
import model.Equipment;
import model.EquipmentReservation;
import model.Lab;
import model.LabSeat;
import model.LabSeatReservation;
import model.Location;
import networking.ClientRegistry;
import service.EquipmentReservationService;
import service.LabSeatReservationService;

import java.sql.Connection;
import java.util.ArrayList;
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
                    return createEquipmentReservation(request, (EquipmentReservationDTO) payload);
                case "CREATE_LAB_SEAT_RESERVATION":
                    return createLabSeatReservation(request, (LabSeatReservationDTO) payload);
                case "CANCEL_EQUIPMENT_RESERVATION":
                    return cancelEquipmentReservation(request, extractReservationId(payload), extractApprover(payload));
                case "CANCEL_LAB_SEAT_RESERVATION":
                    return cancelLabSeatReservation(request, extractReservationId(payload), extractApprover(payload));
                case "GET_EQUIPMENT_RESERVATIONS_BY_STUDENT":
                    return getEquipmentReservationsByStudent(request, (String) payload);
                case "GET_LAB_SEAT_RESERVATIONS_BY_STUDENT":
                    return getLabSeatReservationsByStudent(request, (String) payload);
                case "GET_ALL_EQUIPMENT_RESERVATIONS":
                    return getAllEquipmentReservations(request);
                case "GET_ALL_LAB_SEAT_RESERVATIONS":
                    return getAllLabSeatReservations(request);
                case "APPROVE_EQUIPMENT_RESERVATION":
                    return approveEquipmentReservation(request, extractReservationId(payload), approverOrSystem(payload));
                case "APPROVE_LAB_SEAT_RESERVATION":
                    return approveLabSeatReservation(request, extractReservationId(payload), approverOrSystem(payload));
                case "REJECT_EQUIPMENT_RESERVATION":
                    return rejectEquipmentReservation(request, extractReservationId(payload), approverOrSystem(payload));
                case "REJECT_LAB_SEAT_RESERVATION":
                    return rejectLabSeatReservation(request, extractReservationId(payload), approverOrSystem(payload));
                default:
                    return new ResponseEnvelope<>(request.getCorrelationId(), "Action not supported by ReservationHandler", "FAIL", null);
            }
        } catch (Exception e) {
            return new ResponseEnvelope<>(request.getCorrelationId(), "Error: " + e.getMessage(), "FAIL", null);
        }
    }

    private ResponseEnvelope<?> createEquipmentReservation(RequestEnvelope<Object> request, EquipmentReservationDTO dto) {
        boolean success = equipmentReservationService.createReservation(convertToModel(dto));
        if (success) {
            ClientRegistry.broadcastReservationUpdate();
        }
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Reservation created" : "Failed", success ? "SUCCESS" : "FAIL", success);
    }

    private ResponseEnvelope<?> createLabSeatReservation(RequestEnvelope<Object> request, LabSeatReservationDTO dto) {
        boolean success = labSeatReservationService.createReservation(convertToModel(dto));
        if (success) {
            ClientRegistry.broadcastReservationUpdate();
        }
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Reservation created" : "Failed", success ? "SUCCESS" : "FAIL", success);
    }

    private ResponseEnvelope<?> cancelEquipmentReservation(RequestEnvelope<Object> request, int id, String approver) {
        boolean success = equipmentReservationService.cancelReservation(id, approver);
        if (success) {
            ClientRegistry.broadcastReservationUpdate();
        }
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Reservation cancelled" : "Failed", success ? "SUCCESS" : "FAIL", success);
    }

    private ResponseEnvelope<?> cancelLabSeatReservation(RequestEnvelope<Object> request, int id, String approver) {
        boolean success = labSeatReservationService.cancelReservation(id, approver);
        if (success) {
            ClientRegistry.broadcastReservationUpdate();
        }
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Reservation cancelled" : "Failed", success ? "SUCCESS" : "FAIL", success);
    }

    private ResponseEnvelope<?> getEquipmentReservationsByStudent(RequestEnvelope<Object> request, String studentId) {
        List<EquipmentReservation> list = equipmentReservationService.getReservationsByStudent(studentId);
        List<EquipmentReservationDTO> listDTO = new ArrayList<>();
        for(EquipmentReservation res : list) {
            listDTO.add(new EquipmentReservationDTO(res));
        }
        return new ResponseEnvelope<>(request.getCorrelationId(), "Reservations retrieved", "SUCCESS", listDTO);
    }

    private ResponseEnvelope<?> getLabSeatReservationsByStudent(RequestEnvelope<Object> request, String studentId) {
        List<LabSeatReservation> list = labSeatReservationService.getReservationsByStudent(studentId);
        List<LabSeatReservationDTO> listDTO = new ArrayList<>();
        for(LabSeatReservation res : list) {
            listDTO.add(new LabSeatReservationDTO(res));
        }
        return new ResponseEnvelope<>(request.getCorrelationId(), "Reservations retrieved", "SUCCESS", listDTO);
    }

    private ResponseEnvelope<?> getAllEquipmentReservations(RequestEnvelope<Object> request) {
        List<EquipmentReservation> list = equipmentReservationService.getAllReservations();
        List<EquipmentReservationDTO> listDTO = new ArrayList<>();
        for(EquipmentReservation res : list) {
            listDTO.add(new EquipmentReservationDTO(res));
        }
        return new ResponseEnvelope<>(request.getCorrelationId(), "All reservations retrieved", "SUCCESS", listDTO);
    }

    private ResponseEnvelope<?> getAllLabSeatReservations(RequestEnvelope<Object> request) {
        List<LabSeatReservation> list = labSeatReservationService.getAllReservations();
        List<LabSeatReservationDTO> listDTO = new ArrayList<>();
        for(LabSeatReservation res : list) {
            listDTO.add(new LabSeatReservationDTO(res));
        }
        return new ResponseEnvelope<>(request.getCorrelationId(), "All reservations retrieved", "SUCCESS", listDTO);
    }

    private ResponseEnvelope<?> approveEquipmentReservation(RequestEnvelope<Object> request, int id, String approver) {
        boolean success = equipmentReservationService.approveReservation(id, approver);
        if (success) {
            ClientRegistry.broadcastReservationUpdate();
        }
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Reservation approved" : "Failed", success ? "SUCCESS" : "FAIL", success);
    }

    private ResponseEnvelope<?> approveLabSeatReservation(RequestEnvelope<Object> request, int id, String approver) {
        boolean success = labSeatReservationService.approveReservation(id, approver);
        if (success) {
            ClientRegistry.broadcastReservationUpdate();
        }
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Reservation approved" : "Failed", success ? "SUCCESS" : "FAIL", success);
    }

    private ResponseEnvelope<?> rejectEquipmentReservation(RequestEnvelope<Object> request, int id, String approver) {
        boolean success = equipmentReservationService.denyReservation(id, approver);
        if (success) {
            ClientRegistry.broadcastReservationUpdate();
        }
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Reservation rejected" : "Failed", success ? "SUCCESS" : "FAIL", success);
    }

    private ResponseEnvelope<?> rejectLabSeatReservation(RequestEnvelope<Object> request, int id, String approver) {
        boolean success = labSeatReservationService.denyReservation(id, approver);
        if (success) {
            ClientRegistry.broadcastReservationUpdate();
        }
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Reservation rejected" : "Failed", success ? "SUCCESS" : "FAIL", success);
    }

    private int extractReservationId(Object payload) {
        if (payload instanceof Integer reservationId) {
            return reservationId;
        }
        if (payload instanceof String[] values && values.length > 0) {
            return Integer.parseInt(values[0]);
        }
        throw new IllegalArgumentException("Reservation ID payload missing");
    }

    private String extractApprover(Object payload) {
        if (payload instanceof String[] values && values.length > 1 && values[1] != null && !values[1].isBlank()) {
            return values[1];
        }
        return null;
    }

    private String approverOrSystem(Object payload) {
        String approver = extractApprover(payload);
        return approver != null ? approver : "SYSTEM";
    }

    private EquipmentReservation convertToModel(EquipmentReservationDTO dto) {
        if (dto == null) {
            return null;
        }

        EquipmentReservation reservation = new EquipmentReservation();
        reservation.setReservationID(dto.getReservationID());
        reservation.setStudentID(dto.getStudentID());
        reservation.setDateTime(dto.getDateTime());
        reservation.setDurationInHours(dto.getDurationInHours());
        reservation.setApprovalStatus(dto.getApprovalStatus());
        reservation.setApprovedBy(dto.getApprovedBy());
        reservation.setLastUpdated(dto.getLastUpdated());

        if (dto.getReservedItem() != null) {
            reservation.setReservedItem(convertToModel(dto.getReservedItem()));
        }

        return reservation;
    }

    private LabSeatReservation convertToModel(LabSeatReservationDTO dto) {
        if (dto == null) {
            return null;
        }

        LabSeatReservation reservation = new LabSeatReservation();
        reservation.setReservationID(dto.getReservationID());
        reservation.setStudentID(dto.getStudentID());
        reservation.setDateTime(dto.getDateTime());
        reservation.setDurationInHours(dto.getDurationInHours());
        reservation.setApprovalStatus(dto.getApprovalStatus());
        reservation.setApprovedBy(dto.getApprovedBy());
        reservation.setLastUpdated(dto.getLastUpdated());

        if (dto.getReservedSeat() != null) {
            reservation.setReservedSeat(convertToModel(dto.getReservedSeat()));
        }

        return reservation;
    }

    private Equipment convertToModel(EquipmentDTO dto) {
        if (dto == null) {
            return null;
        }

        Equipment equipment = new Equipment();
        equipment.setEquipmentID(dto.getEquipmentID());
        equipment.setDescription(dto.getDescription());
        equipment.setStatus(dto.getStatus());
        if (dto.getLocation() != null) {
            equipment.setLocation(convertToModel(dto.getLocation()));
        }
        return equipment;
    }

    private LabSeat convertToModel(LabSeatDTO dto) {
        if (dto == null) {
            return null;
        }

        LabSeat seat = new LabSeat();
        seat.setSeatID(dto.getSeatID());
        seat.setSeatCode(dto.getSeatCode());
        if (dto.getSeatLocation() != null) {
            seat.setSeatLocation(convertToModel(dto.getSeatLocation()));
        }
        return seat;
    }

    private Lab convertToModel(dto.LabDTO dto) {
        if (dto == null) {
            return null;
        }

        Lab lab = new Lab();
        lab.setLabID(dto.getLabID());
        lab.setName(dto.getName());
        lab.setNumOfSeats(dto.getNumOfSeats());
        if (dto.getLocation() != null) {
            Location location = new Location();
            location.setRoomName(dto.getLocation().getRoomName());
            location.setBuilding(dto.getLocation().getBuilding());
            location.setFloor(dto.getLocation().getFloor());
            location.setCampus(dto.getLocation().getCampus());
            lab.setLocation(location);
        }
        return lab;
    }
}
