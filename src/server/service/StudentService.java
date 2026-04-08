package server.service;

import server.dao.StudentDAO;
import server.exception.AuthenticationException;
import server.model.Student;
import server.model.User;

import java.sql.Connection;

public class StudentService implements UserService {
    private StudentDAO studentDAO;

    public StudentService(Connection conn) {
        this.studentDAO = new StudentDAO(conn);
    }


    @Override
    public boolean createUser(User user) {
        Student s = (Student) user;
        return studentDAO.saveStudent(s);
    }


    @Override
    public boolean modifyUser(User user) throws AuthenticationException{
        Student s = (Student) user;
        return studentDAO.updateStudent(s);
    }


    @Override
    public User login(String id, String password) throws AuthenticationException{
        Student s = studentDAO.getStudentByStudentId(id);

        if(s==null){
            throw new AuthenticationException("Invalid Credentials"); //If user is not found

        }else{
            String inputtedPasswordHashed = ""; //Use of Hashing here

            if(inputtedPasswordHashed.equals(s.getPasswordHash())){//Compares passwords
                return s;

            }else{
                throw new AuthenticationException("Invalid Credentials"); //If passwords do not match
            }
        }
    }


}
