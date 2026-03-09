package client.cli;

import common.model.Student;
import common.model.User;

public class Main {
    public static void main(String[] args) {
        Student user = new Student(1, "Odane", "Collins", "odanecollins@gmail.com", "*************", User.Role.STUDENT, true, 2103490, "FENC");
        System.out.println(user);
    }
}
