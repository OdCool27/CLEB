package handlers;

import dto.EquipmentDTO;
import envelopes.RequestEnvelope;
import envelopes.ResponseEnvelope;
import model.Equipment;
import model.Lab;
import model.Location;
import service.EquipmentService;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EquipmentHandler implements RequestHandler<Object> {
    private EquipmentService equipmentService;

    public EquipmentHandler(Connection conn) {
        this.equipmentService = new EquipmentService(conn);
    }

    @Override
    public ResponseEnvelope<?> handleRequest(RequestEnvelope<Object> request, Connection conn) {
        String action = request.getAction();
        Object payload = request.getPayload();

        try {
            switch (action) {
                case "ADD_EQUIPMENT":
                    return addEquipment(request, (EquipmentDTO) payload);
                case "UPDATE_EQUIPMENT":
                    return updateEquipment(request, (EquipmentDTO) payload);
                case "DELETE_EQUIPMENT":
                    return deleteEquipment(request, (String) payload);
                case "GET_EQUIPMENT":
                    return getEquipment(request, (String) payload);
                case "GET_ALL_EQUIPMENT":
                    return getAllEquipment(request);
                case "MARK_MAINTENANCE":
                    return markMaintenance(request, (String) payload);
                case "RESTORE_AVAILABILITY":
                    return restoreAvailability(request, (String) payload);
                case "GET_AVAILABLE_EQUIPMENT_AT_TIME":
                    return getAvailableAtTime(request, (LocalDateTime) payload);
                default:
                    return new ResponseEnvelope<>(request.getCorrelationId(), "Action not supported by EquipmentHandler", "FAIL", null);
            }
        } catch (Exception e) {
            return new ResponseEnvelope<>(request.getCorrelationId(), "Error: " + e.getMessage(), "FAIL", null);
        }
    }

    private ResponseEnvelope<?> addEquipment(RequestEnvelope<Object> request, EquipmentDTO e) {
        boolean success = equipmentService.addEquipment(convertToModel(e));
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Equipment added" : "Failed to add equipment", success ? "SUCCESS" : "FAIL", success);
    }

    private ResponseEnvelope<?> updateEquipment(RequestEnvelope<Object> request, EquipmentDTO equipment) {
        boolean success = equipmentService.updateEquipment(convertToModel(equipment));
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Equipment updated" : "Failed to update equipment", success ? "SUCCESS" : "FAIL", success);
    }

    private ResponseEnvelope<?> deleteEquipment(RequestEnvelope<Object> request, String id) {
        boolean success = equipmentService.deleteEquipment(id);
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Equipment deleted" : "Failed to delete equipment", success ? "SUCCESS" : "FAIL", success);
    }

    private ResponseEnvelope<?> getEquipment(RequestEnvelope<Object> request, String id) {
        Equipment eq = equipmentService.getEquipmentById(id);
        EquipmentDTO equipmentDTO = eq != null ? new EquipmentDTO(eq) : null;
        return new ResponseEnvelope<>(request.getCorrelationId(), eq != null ? "Equipment found" : "Equipment not found", eq != null ? "SUCCESS" : "FAIL", equipmentDTO);
    }

    private ResponseEnvelope<?> getAllEquipment(RequestEnvelope<Object> request) {
        List<Equipment> list = equipmentService.getAllEquipment();
        List<EquipmentDTO> listDTO = new ArrayList<>();
        for (Equipment equipment : list) {
            listDTO.add(new EquipmentDTO(equipment));
        }
        return new ResponseEnvelope<>(request.getCorrelationId(), "Equipment list retrieved", "SUCCESS", listDTO);
    }

    private ResponseEnvelope<?> markMaintenance(RequestEnvelope<Object> request, String id) {
        boolean success = equipmentService.markMaintenance(id);
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Marked as maintenance" : "Failed", success ? "SUCCESS" : "FAIL", success);
    }

    private ResponseEnvelope<?> restoreAvailability(RequestEnvelope<Object> request, String id) {
        boolean success = equipmentService.restoreAvailability(id);
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Restored availability" : "Failed", success ? "SUCCESS" : "FAIL", success);
    }

    private ResponseEnvelope<?> getAvailableAtTime(RequestEnvelope<Object> request, LocalDateTime time) {
        List<Equipment> list = equipmentService.getAvailableEquipmentAtTime(time);
        List<EquipmentDTO> listDTO = new ArrayList<>();
        for (Equipment equipment : list) {
            listDTO.add(new EquipmentDTO(equipment));
        }
        return new ResponseEnvelope<>(request.getCorrelationId(), "Available equipment retrieved", "SUCCESS", listDTO);
    }
    
    
    private Equipment convertToModel(EquipmentDTO equipment) {
        Equipment e = new Equipment();
        e.setEquipmentID(equipment.getEquipmentID());
        e.setDescription(equipment.getDescription());
        if (equipment.getLocation() != null) {
            Lab lab = new Lab();
            lab.setLabID(equipment.getLocation().getLabID());
            lab.setName(equipment.getLocation().getName());
            lab.setNumOfSeats(equipment.getLocation().getNumOfSeats());
            if (equipment.getLocation().getLocation() != null) {
                Location loc = new Location();
                loc.setRoomName(equipment.getLocation().getLocation().getRoomName());
                loc.setBuilding(equipment.getLocation().getLocation().getBuilding());
                loc.setFloor(equipment.getLocation().getLocation().getFloor());
                loc.setCampus(equipment.getLocation().getLocation().getCampus());
                lab.setLocation(loc);
            }
            e.setLocation(lab);
        }
        e.setStatus(equipment.getStatus());
        return e;
    }
}
