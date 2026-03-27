package server.handlers.user;

import server.dispatcher.RequestHandler;
import server.dto.UserDTO;
import server.envelopes.RequestEnvelope;
import server.envelopes.ResponseEnvelope;

import java.sql.Connection;

public class UserHandler implements RequestHandler<UserDTO> {
    @Override
    public ResponseEnvelope<?> handleRequest(RequestEnvelope<UserDTO> request, Connection conn) {
        return null;
    }
}
