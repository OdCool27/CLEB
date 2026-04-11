package server.model;

public class Equipment {
    private String equipmentID;
    private String description;
    private Lab location;
    private String status;

    public Equipment() {
        equipmentID = "";
        description = "";
        location = new Lab();
        status = "";
    }

    public Equipment(String equipmentID, String description, Lab location, String status) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Equipment Information\n====================\n" +
                "Equipment ID: " + equipmentID + "\n" +
                "Description: " + description + "\n" +
                "Location: " + location + "\n" +
                "Status: " + status + "\n";
    }
}
