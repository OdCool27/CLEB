package server.service;

import java.sql.Connection;

public class ReservationService {
    private EquipmentReservationService equipmentReservationService;
    private LabSeatReservationService labSeatReservationService;

    public ReservationService(Connection connection) {
        this.equipmentReservationService = new EquipmentReservationService(connection);
        this.labSeatReservationService = new LabSeatReservationService(connection);
    }

    public EquipmentReservationService getEquipmentReservationService() {
        return equipmentReservationService;
    }

    public LabSeatReservationService getLabSeatReservationService() {
        return labSeatReservationService;
    }
}
