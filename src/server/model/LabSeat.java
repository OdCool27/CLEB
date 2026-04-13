package model;

public class LabSeat {
    private int seatID;
    private String seatCode;
    private Lab seatLocation;

    public LabSeat(){
        seatID = -1;
        seatCode = "";
        seatLocation = new Lab();
    }

    public LabSeat(int seatID, Lab seatLocation) {
        this.seatID = seatID;
        this.seatLocation = seatLocation;
    }

    public int getSeatID() {
        return seatID;
    }

    public void setSeatID(int seatID) {
        this.seatID = seatID;
    }

    public String getSeatCode() {
        return seatCode;
    }

    public void setSeatCode(String seatCode) {
        this.seatCode = seatCode;
    }

    public Lab getSeatLocation() {
        return seatLocation;
    }

    public void setSeatLocation(Lab seatLocation) {
        this.seatLocation = seatLocation;
    }

    public String toString() {
        return "Seat Information\n" + "======================\n" +
                "Seat ID: " + seatID + "\n" +
                seatLocation.toString() + "\n";
    }
}
