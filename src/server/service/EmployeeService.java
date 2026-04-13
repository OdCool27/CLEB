package service;

import dao.EmployeeDAO;
import exception.AuthenticationException;
import model.Employee;
import model.User;
import util.PasswordHashingUtil;

import java.sql.Connection;

public class EmployeeService implements UserService{
    private EmployeeDAO employeeDAO;

    public EmployeeService(Connection connection) {
        employeeDAO = new EmployeeDAO(connection);
    }

    @Override
    public boolean createUser(User user) {
        if (user instanceof Employee) {
            // Only notify after the account exists successfully in both User and Employee tables.
            boolean success = employeeDAO.saveEmployee((Employee) user);
            if (success) {
                NotificationService.sendAccountCreationNotification((Employee) user);
            }
            return success;
        }
        return false;
    }

    @Override
    public boolean modifyUser(User user) throws AuthenticationException {
        if (user instanceof Employee) {
            boolean success = employeeDAO.updateEmployee((Employee) user);
            if (success) {
                NotificationService.sendAccountModificationNotification((Employee) user);
            }
            return success;
        }
        return false;
    }

    @Override
    public Employee login(String email, String password) throws AuthenticationException {
        Employee e = employeeDAO.getEmployeeByEmail(email);

        if (e == null) {
            throw new AuthenticationException("No Such User found!");
        } else {

            if (PasswordHashingUtil.verifyPassword(password, e.getPasswordHash())) {
                return (Employee) e;
            } else {
                throw new AuthenticationException("Invalid Credentials");
            }
        }
    }

}
