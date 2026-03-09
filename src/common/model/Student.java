package common.model;

public class Student extends User{
    private int studentID;
    private String faculty;

    public Student(){
        super();
        studentID = 0;
        faculty = "";
    }

    public Student(int userID, String firstName, String lastName, String email, String passwordHash,
                   Role role, boolean isActivated, int studentID, String faculty){
        super(userID, firstName, lastName, email, passwordHash, role, isActivated);
        this.studentID = studentID;
        this.faculty = faculty;
    }

    public int getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    @Override
    public String toString() {
        return "Student Information\n====================\n" +
                "Student ID: " + studentID + "\n" +
                "Full Name: " + firstName + " " + lastName + "\n" +
                "Email: " + email + "\n" +
                "Faculty: " + faculty + "\n" +
                "Active: " + isActive + "\n";
    }
}
