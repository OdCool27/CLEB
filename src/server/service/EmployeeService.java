package server.service;

import server.dao.EmployeeDAO;
import server.exception.AuthenticationException;
import server.model.Employee;
import server.model.User;
import java.sql.Connection;

public class EmployeeService implements UserService {
    private EmployeeDAO employeeDAO;

    public EmployeeService(Connection connection) {
        employeeDAO = new EmployeeDAO(connection);
    }

    @Override
    public boolean createUser(User user) {
        if (user instanceof Employee) {
            NotificationService.sendAccountCreationNotification((Employee) user);
            return employeeDAO.saveEmployee((Employee) user);
        }
        return false;
    }

    @Override
    public boolean modifyUser(User user) throws AuthenticationException {
        if (user instanceof Employee) {
            NotificationService.sendAccountModificationNotification((Employee) user);
            return employeeDAO.updateEmployee((Employee) user);
        }
        return false;
    }

    @Override
    public User login(String id, String password) throws AuthenticationException {
        Employee e = employeeDAO.getEmployeeById(id);

        if (e == null) {
            throw new AuthenticationException("Invalid Credentials");
        } else {
            // Use same hashing logic as StudentService template
            String inputtedPasswordHashed = ""; // Placeholder for actual hashing

            if (inputtedPasswordHashed.equals(e.getPasswordHash())) {
                return (User) e;
            } else {
                throw new AuthenticationException("Invalid Credentials");
            }
        }
    }
}
