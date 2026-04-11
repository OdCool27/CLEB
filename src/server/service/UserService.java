package server.service;

import server.exception.AuthenticationException;
import server.model.User;

public interface UserService {
    public boolean createUser(User user);
    public boolean modifyUser(User user) throws AuthenticationException;
    public User login(String id, String password) throws AuthenticationException;

}
