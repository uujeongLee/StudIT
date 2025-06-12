package studitest.ui;

import studit.domain.User;
import studit.service.Login;
import studit.ui.login.LoginPanel;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class LoginPanelTest {
    public static void main(String[] args) {
        // 테스트용 더미 Login 서비스
        Login dummyLogin = new Login() {
            private User currentUser;
            @Override
            public boolean login(String email, String password) {
                if (email.equals("test@studit.com") && password.equals("1234")) {
                    currentUser = new User(email, "테스트유저", "dummyPassword");
                    return true;
                }
                return false;
            }
            @Override
            public User getCurrentUser() {
                return currentUser;
            }
        };

        Consumer<User> onLoginSuccess = user ->
                JOptionPane.showMessageDialog(null, "로그인 성공: " + user.getName());

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("LoginPanel 테스트");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(new LoginPanel(dummyLogin, onLoginSuccess));
            frame.setSize(400, 420);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

