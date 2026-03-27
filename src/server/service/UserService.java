package server.service;

import server.dao.UserDAO;
import server.model.User;

import java.sql.Connection;

public class UserService {
    private UserDAO userDAO;

    public static User login(String id, String password, Connection conn){
        UserDAO userDAO = new UserDAO(conn);
        User user = null;
        // Logic will go here...
        return  user;
    }

}
