package server.handlers;

import server.dispatcher.RequestHandler;
import server.envelopes.RequestEnvelope;
import server.envelopes.ResponseEnvelope;
import server.model.Equipment;
import server.service.EquipmentService;

import java.sql.Connection;
import java.time.LocalDateTime;
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
                    return addEquipment(request, (Equipment) payload);
                case "UPDATE_EQUIPMENT":
                    return updateEquipment(request, (Equipment) payload);
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

    private ResponseEnvelope<?> addEquipment(RequestEnvelope<Object> request, Equipment equipment) {
        boolean success = equipmentService.addEquipment(equipment);
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Equipment added" : "Failed to add equipment", success ? "SUCCESS" : "FAIL", success);
    }

    private ResponseEnvelope<?> updateEquipment(RequestEnvelope<Object> request, Equipment equipment) {
        boolean success = equipmentService.updateEquipment(equipment);
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Equipment updated" : "Failed to update equipment", success ? "SUCCESS" : "FAIL", success);
    }

    private ResponseEnvelope<?> deleteEquipment(RequestEnvelope<Object> request, String id) {
        boolean success = equipmentService.deleteEquipment(id);
        return new ResponseEnvelope<>(request.getCorrelationId(), success ? "Equipment deleted" : "Failed to delete equipment", success ? "SUCCESS" : "FAIL", success);
    }

    private ResponseEnvelope<?> getEquipment(RequestEnvelope<Object> request, String id) {
        Equipment eq = equipmentService.getEquipmentById(id);
        return new ResponseEnvelope<>(request.getCorrelationId(), eq != null ? "Equipment found" : "Equipment not found", eq != null ? "SUCCESS" : "FAIL", eq);
    }

    private ResponseEnvelope<?> getAllEquipment(RequestEnvelope<Object> request) {
        List<Equipment> list = equipmentService.getAllEquipment();
        return new ResponseEnvelope<>(request.getCorrelationId(), "Equipment list retrieved", "SUCCESS", list);
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
        return new ResponseEnvelope<>(request.getCorrelationId(), "Available equipment retrieved", "SUCCESS", list);
    }
}
