package studit.ui.login;


import studit.domain.User;
import studit.service.Login;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * 로그인 화면 UI를 담당하는 JPanel 클래스입니다.
 * - 이메일/비밀번호 입력 필드, 로그인 버튼, 오류 메시지 라벨 등으로 구성됩니다.
 * - 로그인 성공 시 onLoginSuccess 콜백(Consumer<User>)을 통해 상위 패널에 사용자 정보를 전달합니다.
 * - 파란색 계열 메인 컬러와 마우스 오버 효과, 입력 검증, 오류 메시지 표시 등 사용자 경험을 개선합니다.
 */
public class LoginPanel extends JPanel {
    private final Login loginService;
    private final Consumer<User> onLoginSuccess;

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel errorLabel;

    // 파란색 메인 컬러
    private static final Color MAIN_BLUE = new Color(33, 150, 243); // Material Blue 500
    private static final Color BUTTON_BLUE = new Color(33, 150, 243);
    private static final Color BUTTON_BLUE_HOVER = new Color(25, 118, 210);

    public LoginPanel(Login loginService, Consumer<User> onLoginSuccess) {
        this.loginService = loginService;
        this.onLoginSuccess = onLoginSuccess;
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);

        // 타이틀
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel title = new JLabel("STUDIT");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 32));
        title.setForeground(MAIN_BLUE);
        add(title, gbc);

        // "로그인" 소제목
        gbc.gridy++;
        JLabel subtitle = new JLabel("로그인");
        subtitle.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        subtitle.setForeground(Color.DARK_GRAY);
        add(subtitle, gbc);

        // 이메일
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy++;
        JLabel emailLabel = new JLabel("이메일");
        emailLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        add(emailLabel, gbc);

        gbc.gridx = 1;
        emailField = new JTextField(18);
        emailField.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        emailField.setBorder(BorderFactory.createLineBorder(MAIN_BLUE, 1));
        add(emailField, gbc);

        // 비밀번호
        gbc.gridx = 0; gbc.gridy++;
        JLabel pwdLabel = new JLabel("비밀번호");
        pwdLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        add(pwdLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(18);
        passwordField.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        passwordField.setBorder(BorderFactory.createLineBorder(MAIN_BLUE, 1));
        add(passwordField, gbc);

        // 오류 라벨
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        add(errorLabel, gbc);

        // 로그인 버튼
        loginButton = new JButton("로그인");
        loginButton.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(BUTTON_BLUE);
        loginButton.setPreferredSize(new Dimension(140, 40));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BUTTON_BLUE, 1),
                BorderFactory.createEmptyBorder(8, 0, 8, 0)
        ));
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // 마우스 오버 효과
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(BUTTON_BLUE_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(BUTTON_BLUE);
            }
        });

        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
        add(loginButton, gbc);

        // 로그인 버튼 동작
        loginButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String pwd = new String(passwordField.getPassword());
            if (email.isEmpty() || pwd.isEmpty()) {
                errorLabel.setText("이메일과 비밀번호를 모두 입력하세요.");
                return;
            }
            if (!loginService.login(email, pwd)) {
                errorLabel.setText("로그인 정보가 맞지 않습니다.");
            } else {
                errorLabel.setText(" ");
                onLoginSuccess.accept(loginService.getCurrentUser());
            }
        });
    }
}
