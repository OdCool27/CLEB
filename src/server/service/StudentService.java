package server.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.dao.StudentDAO;
import server.exception.AuthenticationException;
import server.model.Student;
import server.model.User;

import java.sql.Connection;

public class StudentService implements UserService {
    private static final Logger logger = LogManager.getLogger(StudentService.class);
    private StudentDAO studentDAO;

    public StudentService(Connection conn) {
        this.studentDAO = new StudentDAO(conn);
    }


    @Override
    public boolean createUser(User user) {
        Student s = (Student) user;
        logger.info("Creating user: {} (ID: {})", s.getFirstName() + " " + s.getLastName(), s.getStudentID());
        NotificationService.sendAccountCreationNotification(s);
        return studentDAO.saveStudent(s);
    }


    @Override
    public boolean modifyUser(User user) throws AuthenticationException{
        Student s = (Student) user;
        NotificationService.sendAccountModificationNotification(s);
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
