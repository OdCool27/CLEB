package model;

public class Equipment {
    private String equipmentID;
    private String description;
    private Lab location;
    private EquipStatus status;

    public enum EquipStatus{
        AVAILABLE, MAINTENANCE, BOOKED
    }

    public Equipment() {
        equipmentID = "";
        description = "";
        location = new Lab();
        status = EquipStatus.AVAILABLE;
    }

    public Equipment(String equipmentID, String description, Lab location, EquipStatus status) {
        this.equipmentID = equipmentID;
        this.description = description;
        this.location = location;
        this.status = status;
    }

    public String getEquipmentID() {
        return equipmentID;
    }

    public void setEquipmentID(String equipmentID) {
        this.equipmentID = equipmentID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Lab getLocation() {
        return location;
    }

    public void setLocation(Lab location) {
        this.location = location;
    }

    public EquipStatus getStatus() {
        return status;
    }

    public void setStatus(EquipStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "\n\nEquipment Information\n====================\n" +
                "Equipment ID: " + equipmentID + "\n" +
                "Description: " + description + "\n" +
                "Location: " + location + "\n" +
                "Status: " + status.toString() + "\n";
    }
}
