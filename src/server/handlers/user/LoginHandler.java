package server.handlers.user;

import server.dispatcher.RequestHandler;
import server.dto.LoginRequestDTO;
import server.dto.UserDTO;
import server.envelopes.RequestEnvelope;
import server.envelopes.ResponseEnvelope;
import server.model.User;
import server.service.UserService;

import java.sql.Connection;

public class LoginHandler implements RequestHandler<LoginRequestDTO> {

    @Override
    public ResponseEnvelope<User> handleRequest(RequestEnvelope<LoginRequestDTO> request, Connection conn) {

        // Cast payload safely
        LoginRequestDTO dto = (LoginRequestDTO) request.getPayload();

        // Call service layer
        User user = UserService.login(dto.getId(), dto.getPassword(), conn);

        if (user == null) {
            return new ResponseEnvelope<User>(
                    request.getCorrelationId(),
                    "Invalid ID or password",
                    "FAIL",
                    null
            );
        }

        return new ResponseEnvelope<User>(
                request.getCorrelationId(),
                "Login successful",
                "SUCCESS",
                user
        );
    }
}
