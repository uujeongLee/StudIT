package studit.domain;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String studentId;
    private String department;
    private Profile profile;

    public User(String name, String studentId, String department) {
        this.name = name;
        this.studentId = studentId;
        this.department = department;
        this.profile = new Profile();
    }

    public String getName() {
        return name;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getDepartment() {
        return department;
    }

    public Profile getProfile() {
        return profile;
    }

    @Override
    public String toString() {
        return String.format("이름: %s, 학번: %s, 전공: %s\n%s",
                name, studentId, department, profile.toString());
    }
}
