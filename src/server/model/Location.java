package model;

public class Location {
    private String roomName;
    private String building;
    private int floor;
    private String campus;

    public Location() {
        roomName = "";
        building = "";
        floor = 0;
        campus = "";
    }

    public Location(String roomName, String building, int floor, String campus) {
        this.roomName = roomName;
        this.building = building;
        this.floor = floor;
        this.campus = campus;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    @Override
    public String toString() {
        return "Location Information\n====================\n" +
                "Room Name: " + roomName + "\n" +
                "Building: " + building + "\n" +
                "Floor: " + floor + "\n" +
                "Campus: " + campus + "\n";
    }
}
