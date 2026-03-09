package common.envelopes;

import common.dto.UserDTO;

import java.io.Serializable;
import java.util.UUID;

public class UserRequestEnvelope implements Serializable {
    private UUID correlationID;
    private String action;  //CREATE, UPDATE, CANCEL, FETCH
    private UserDTO payload;

    public UserRequestEnvelope(UUID correlationID, String action, UserDTO payload) {
        this.correlationID = correlationID;
        this.action = action;
        this.payload = payload;
    }

}
