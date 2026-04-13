package service;

import dao.EquipmentDAO;
import dao.EquipmentReservationDAO;
import model.Equipment;
import model.EquipmentReservation;
import model.Reservation;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EquipmentService {
    private EquipmentDAO equipmentDAO;
    private EquipmentReservationDAO equipmentReservationDAO;

    public EquipmentService(Connection connection) {
        this.equipmentDAO = new EquipmentDAO(connection);
        this.equipmentReservationDAO = new EquipmentReservationDAO(connection);
    }

    public boolean addEquipment(Equipment equipment) {
        return equipmentDAO.saveEquipment(equipment);
    }

    public boolean updateEquipment(Equipment equipment) {
        return equipmentDAO.updateEquipment(equipment);
    }

    public boolean deleteEquipment(String equipmentID) {
        return equipmentDAO.deleteEquipment(equipmentID);
    }

    public Equipment getEquipmentById(String equipmentID) {
        return equipmentDAO.getEquipmentById(equipmentID);
    }

    public List<Equipment> getAllEquipment() {
        return equipmentDAO.getAllEquipment();
    }

    public List<Equipment> getAvailableEquipment() {
        return equipmentDAO.getEquipmentByStatus("AVAILABLE");
    }

    public List<Equipment> getEquipmentByLab(String labID) {
        return equipmentDAO.getEquipmentByLabId(labID);
    }

    public boolean markMaintenance(String equipmentID) {
        Equipment equipment = equipmentDAO.getEquipmentById(equipmentID);
        if (equipment != null) {
            equipment.setStatus(Equipment.EquipStatus.MAINTENANCE);
            return equipmentDAO.updateEquipment(equipment);
        }
        return false;
    }

    public boolean restoreAvailability(String equipmentID) {
        Equipment equipment = equipmentDAO.getEquipmentById(equipmentID);
        if (equipment != null) {
            equipment.setStatus(Equipment.EquipStatus.AVAILABLE);
            return equipmentDAO.updateEquipment(equipment);
        }
        return false;
    }

    public List<Equipment> getAvailableEquipmentAtTime(LocalDateTime requestedTime) {
        if (requestedTime.getHour() >= 21) {
            return new ArrayList<>();
        }

        List<Equipment> allEquipment = equipmentDAO.getAllEquipment();
        List<Equipment> availableItems = new ArrayList<>();

        for (Equipment item : allEquipment) {
            if (item.getStatus() == Equipment.EquipStatus.AVAILABLE) {
                List<EquipmentReservation> reservations = equipmentReservationDAO.getReservationsByEquipmentIdAndDate(item.getEquipmentID(), requestedTime);
                boolean isReserved = false;
                for (EquipmentReservation res : reservations) {
                    if (res.getApprovalStatus() == Reservation.ReservationStatus.APPROVED || res.getApprovalStatus() == Reservation.ReservationStatus.PENDING) {
                        LocalDateTime resStart = res.getDateTime();
                        LocalDateTime resEnd = resStart.plusHours(res.getDurationInHours());

                        // Check if requestedTime falls within [resStart, resEnd)
                        if ((requestedTime.isEqual(resStart) || requestedTime.isAfter(resStart)) && requestedTime.isBefore(resEnd)) {
                            isReserved = true;
                            break;
                        }
                    }
                }
                if (!isReserved) {
                    availableItems.add(item);
                }
            }
        }
        return availableItems;
    }
}
