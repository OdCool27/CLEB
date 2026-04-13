package service;

import dao.StudentDAO;
import exception.AuthenticationException;
import model.Student;
import model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.PasswordHashingUtil;

import java.sql.Connection;

public class StudentService implements UserService{
    private static final Logger logger = LogManager.getLogger(StudentService.class);
    private StudentDAO studentDAO;

    public StudentService(Connection conn) {
        this.studentDAO = new StudentDAO(conn);
    }


    @Override
    public boolean createUser(User user) {
        Student s = (Student) user;
        logger.info("Creating user: {} (ID: {})", s.getFirstName() + " " + s.getLastName(), s.getStudentID());
        boolean success = studentDAO.saveStudent(s);
        if (success) {
            NotificationService.sendAccountCreationNotification(s);
        }
        return success;
    }


    @Override
    public boolean modifyUser(User user) throws AuthenticationException {
        Student s = (Student) user;
        boolean success = studentDAO.updateStudent(s);
        if (success) {
            NotificationService.sendAccountModificationNotification(s);
        }
        return success;
    }


    @Override
    public Student login(String email, String password) throws AuthenticationException{
        Student s = studentDAO.getStudentByEmail(email);

        if(s==null){
            throw new AuthenticationException("Invalid Credentials"); //If user is not found

        }else{


            if(PasswordHashingUtil.verifyPassword(password, s.getPasswordHash())){//Compares passwords hash
                return s;

            }else{
                throw new AuthenticationException("Invalid Credentials"); //If passwords do not match
            }
        }
    }
}
