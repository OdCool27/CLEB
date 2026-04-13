package model;

import java.time.LocalDateTime;public class Student extends User {
    private String studentID;
    private String faculty;
    private String school;

    public Student(){
        super();
        studentID = "";
        faculty = "";
        school = "";
    }

    public Student(int userID, String firstName, String lastName, String email, String passwordHash,
                   Role role, boolean isActivated, LocalDateTime lastUpdated, String studentID, String faculty, String school) {
        super(userID, firstName, lastName, email, passwordHash, role, isActivated, lastUpdated);
        this.studentID = studentID;
        this.faculty = faculty;
        this.school = school;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    @Override
    public String toString() {
        return "Student Information\n====================\n" +
                "Student ID: " + studentID + "\n" +
                "Full Name: " + firstName + " " + lastName + "\n" +
                "Email: " + email + "\n" +
                "Faculty: " + faculty + "\n" +
                "School: " + school + "\n" +
                "Active: " + isActive + "\n";
    }
}

