package common.envelopes;

import common.dto.UserDTO;

import java.io.Serializable;
import java.util.UUID;

public class ReservationRequestEnvelope implements Serializable {
    private UUID correlationID;
    private String action;  //CREATE, UPDATE, CANCEL, FETCH
    private UserDTO payload;
}
