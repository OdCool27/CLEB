package model;

public class Lab {
    private String labID;
    private String name;
    private Location location;
    private int numOfSeats;

    public Lab() {
        labID = "";
        name = "";
        location = new Location();
        numOfSeats = 0;
    }

    public Lab(String labID, String name, Location location, int numOfSeats) {
        this.labID = labID;
        this.name = name;
        this.location = location;
        this.numOfSeats = numOfSeats;
    }

    public String getLabID() {
        return labID;
    }

    public void setLabID(String labID) {
        this.labID = labID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getNumOfSeats() {
        return numOfSeats;
    }

    public void setNumOfSeats(int numOfSeats) {
        this.numOfSeats = numOfSeats;
    }

    @Override
    public String toString() {
        return "Lab Information\n====================\n" +
                "Lab ID: " + labID + "\n" +
                "Name: " + name + "\n" +
                "Number of Seats: " + numOfSeats + "\n" +
                location.toString() + "\n";
    }
}
