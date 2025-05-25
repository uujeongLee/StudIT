package studit.service;

import studit.domain.User;
import studit.domain.Profile;

import java.util.HashMap;
import java.util.Map;

public class Login {
    private Map<String, User> users = new HashMap<>(); // email → User
    private Map<String, String> passwords = new HashMap<>(); // email → password
    private Map<String, String> emails = new HashMap<>(); // studentId → email (ID 중복 방지용)
    private User currentUser;

    // 회원가입 메서드
    public boolean register(String name, String studentId, String department, String email, String password) {
        if (users.containsKey(email)) return false;
        if (emails.containsKey(studentId)) return false;

        User user = new User(name, studentId, department);
        users.put(email, user);
        passwords.put(email, password);
        emails.put(studentId, email);
        return true;
    }

    // 로그인 메서드
    public boolean login(String email, String password) {
        if (!users.containsKey(email)) return false;
        if (!passwords.containsKey(email)) return false;
        if (!passwords.get(email).equals(password)) return false;

        currentUser = users.get(email);
        return true;
    }

    // 현재 로그인된 사용자 반환
    public User getCurrentUser() {
        return currentUser;
    }
}
