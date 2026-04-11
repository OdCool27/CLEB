package server.handlers.user;

import server.dispatcher.RequestHandler;
import server.dto.EmployeeDTO;
import server.dto.StudentDTO;
import server.dto.UserDTO;
import server.envelopes.RequestEnvelope;
import server.envelopes.ResponseEnvelope;
import server.model.Employee;
import server.model.Student;
import server.model.User;
import server.service.EmployeeService;
import server.service.StudentService;

import java.sql.Connection;

public class UserHandler implements RequestHandler<UserDTO> {
    private EmployeeService employeeService;
    private StudentService studentService;

    public UserHandler(Connection conn) {
        this.employeeService = new EmployeeService(conn);
        this.studentService = new StudentService(conn);
    }

    @Override
    public ResponseEnvelope<?> handleRequest(RequestEnvelope<UserDTO> request, Connection conn) {
        String action = request.getAction();
        UserDTO dto = request.getPayload();

        try {
            switch (action) {
                case "CREATE_USER":
                    return createUser(request, dto);
                case "UPDATE_USER":
                    return updateUser(request, dto);
                case "GET_USER":
                    // This might need a different DTO or just an ID
                    return getUser(request, dto);
                default:
                    return new ResponseEnvelope<>(request.getCorrelationId(), "Action not supported by UserHandler", "FAIL", null);
            }
        } catch (Exception e) {
            return new ResponseEnvelope<>(request.getCorrelationId(), "Error: " + e.getMessage(), "FAIL", null);
        }
    }

    private ResponseEnvelope<?> createUser(RequestEnvelope<UserDTO> request, UserDTO dto) {
        boolean success;
        if (dto instanceof StudentDTO) {
            StudentDTO sDto = (StudentDTO) dto;
            Student student = new Student();
            // Map DTO to Model - Note: In a real app we'd need more mapping logic and password handling
            student.setFirstName(sDto.getFirstName());
            student.setLastName(sDto.getLastName());
            student.setEmail(sDto.getEmail());
            student.setRole(User.Role.STUDENT);
            student.setStudentID(sDto.getStudentID());
            student.setFaculty(sDto.getFaculty());
            student.setSchool(sDto.getSchool());
            success = studentService.createUser(student);
        } else {
            Employee employee = new Employee();
            employee.setFirstName(dto.getFirstName());
            employee.setLastName(dto.getLastName());
            employee.setEmail(dto.getEmail());
            employee.setRole(dto.getRole());
            // Need to handle EmpID for Employee
            success = employeeService.createUser(employee);
        }

        if (success) {
            return new ResponseEnvelope<>(request.getCorrelationId(), "User created successfully", "SUCCESS", true);
        } else {
            return new ResponseEnvelope<>(request.getCorrelationId(), "Failed to create user", "FAIL", false);
        }
    }

    private ResponseEnvelope<?> updateUser(RequestEnvelope<UserDTO> request, UserDTO dto) throws Exception {
        boolean success;
        if (dto instanceof StudentDTO) {
            StudentDTO sDto = (StudentDTO) dto;
            Student student = new Student();
            student.setUserID(sDto.getUserID());
            student.setFirstName(sDto.getFirstName());
            student.setLastName(sDto.getLastName());
            student.setEmail(sDto.getEmail());
            student.setStudentID(sDto.getStudentID());
            student.setFaculty(sDto.getFaculty());
            student.setSchool(sDto.getSchool());
            success = studentService.modifyUser(student);
        } else {
            Employee employee = new Employee();
            employee.setUserID(dto.getUserID());
            employee.setFirstName(dto.getFirstName());
            employee.setLastName(dto.getLastName());
            employee.setEmail(dto.getEmail());
            success = employeeService.modifyUser(employee);
        }

        if (success) {
            return new ResponseEnvelope<>(request.getCorrelationId(), "User updated successfully", "SUCCESS", true);
        } else {
            return new ResponseEnvelope<>(request.getCorrelationId(), "Failed to update user", "FAIL", false);
        }
    }

    private ResponseEnvelope<?> getUser(RequestEnvelope<UserDTO> request, UserDTO dto) {
        // Implementation for getting a user - usually by ID
        return new ResponseEnvelope<>(request.getCorrelationId(), "Get user not yet fully implemented", "FAIL", null);
    }
}
