package server.service;

import server.dao.EmployeeDAO;
import server.exception.AuthenticationException;
import server.model.User;

import java.sql.Connection;

public class EmployeeService implements UserService {
    private EmployeeDAO employeeDAO;

    public EmployeeService(Connection connection) {
        employeeDAO = new EmployeeDAO(connection);
    }

    @Override
    public boolean createUser(User user) {
        return false;
    }

    @Override
    public boolean modifyUser(User user) throws AuthenticationException {
        return false;
    }

    @Override
    public User login(String id, String password) throws AuthenticationException {
        return null;
    }
}
