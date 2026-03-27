package server.handlers.user;

import server.dispatcher.RequestHandler;
import server.dto.LoginRequestDTO;
import server.envelopes.RequestEnvelope;
import server.envelopes.ResponseEnvelope;
import server.model.User;
import server.service.UserService;

import java.sql.Connection;

public class LoginHandler implements RequestHandler<User> {

    @Override
    public ResponseEnvelope<User> handleRequest(RequestEnvelope<?> request) {

        // Cast payload safely
        LoginRequestDTO dto = (LoginRequestDTO) request.getPayload();

        // Call service layer
        User user = UserService.login(dto.getId(), dto.getPassword());

        if (user == null) {
            return new ResponseEnvelope<>(
                    request.getCorrelationId(),
                    "Invalid ID or password",
                    "FAIL",
                    null
            );
        }

        return new ResponseEnvelope<>(
                request.getCorrelationId(),
                "Login successful",
                "SUCCESS",
                user
        );
    }
}
