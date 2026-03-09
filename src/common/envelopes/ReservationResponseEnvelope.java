package common.envelopes;

import common.dto.ReservationDTO;

import java.io.Serializable;
import java.util.UUID;

public class ReservationResponseEnvelope implements Serializable {
    private UUID correlationID; // tracks request/response pair
    private String status;      // SUCCESS / FAILURE
    private String message;     // optional
    private ReservationDTO payload; // actual data

    //CONSTRUCTOR
    public ReservationResponseEnvelope(UUID correlationID, String status, String message, ReservationDTO payload) {
        this.correlationID = correlationID;
        this.status = status;
        this.message = message;
        this.payload = payload;
    }

    //ACCESSORS & MUTATORS
    public UUID getCorrelationID() {
        return correlationID;
    }

    public void setCorrelationID(UUID correlationID) {
        this.correlationID = correlationID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ReservationDTO getPayload() {
        return payload;
    }

    public void setPayload(ReservationDTO payload) {
        this.payload = payload;
    }
}
