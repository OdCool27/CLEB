package server.handlers.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.dispatcher.RequestHandler;
import server.dto.LoginRequestDTO;
import server.envelopes.RequestEnvelope;
import server.envelopes.ResponseEnvelope;
import server.exception.AuthenticationException;
import server.model.Employee;
import server.model.Student;
import server.model.User;
import server.service.EmployeeService;
import server.service.StudentService;

import java.sql.Connection;

public class LoginHandler implements RequestHandler<LoginRequestDTO> {
    private static final Logger logger = LogManager.getLogger(LoginHandler.class);
    EmployeeService employeeService;
    StudentService studentService;

    public LoginHandler(Connection conn) {
        this.employeeService =  new EmployeeService(conn);
        this.studentService =  new StudentService(conn);
    }

    public User loginBasedOnUserType(String id, String password) throws AuthenticationException {
        logger.debug("Attempting login for ID: {}", id);
        User user = null;

        if (id==null || (id.length()!=5 && id.length()!=7)) {
            logger.warn("Login failed: Invalid ID length for ID: {}", id);
            throw new AuthenticationException("Invalid ID Length");
        }

        if (id.length() == 5) {
            user = employeeService.login(id, password);
        }

        if (id.length() == 7) {
            user = studentService.login(id, password);
        }

        if (user != null) {
            logger.info("Login successful for user: {} (ID: {})", user.getFirstName() + " " + user.getLastName(), id);
        } else {
            logger.warn("Login failed for ID: {}", id);
        }

        return user;
    }

    @Override
    public ResponseEnvelope<User> handleRequest(RequestEnvelope<LoginRequestDTO> request, Connection conn) {

        // Cast payload safely
        LoginRequestDTO dto = (LoginRequestDTO) request.getPayload();


        // Call service layer
        try {
          User user = loginBasedOnUserType(dto.getId(), dto.getPassword());

            return new ResponseEnvelope<User>(
                    request.getCorrelationId(),
                    "Login successful",
                    "SUCCESS",
                    user
            );

        }catch (AuthenticationException e){
            return new ResponseEnvelope<User>(
                    request.getCorrelationId(),
                    e.getMessage(),
                    "FAIL",
                    null
            );
        }




    }
}
