package studit.ui.home;

import studit.domain.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * 사용자 프로필 정보를 보여주는 모달 다이얼로그 클래스입니다.
 * - 이름, 전공, 학번, 관심 태그 등 User 객체의 정보를 카드 형태로 예쁘게 출력합니다.
 * - 상단에 이모지(❄)와 이름, 각 필드는 makeProfileField()로 꾸밈.
 * - 확인 버튼을 누르면 닫힙니다.
 * - 스터디 그룹 앱의 프로필 보기 기능에 사용됩니다.
 */
public class ProfileDialog extends JDialog {
    public ProfileDialog(JFrame parent, User user) {
        super(parent, "프로필 정보", true);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel iconLabel = new JLabel("❄", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel(user.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setBorder(new EmptyBorder(10, 0, 14, 0));

        mainPanel.add(iconLabel);
        mainPanel.add(nameLabel);

        mainPanel.add(makeProfileField("전공", user.getDepartment()));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(makeProfileField("학번", user.getStudentId()));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(makeProfileField("관심 태그", String.join(", ", user.getProfile().getInterests())));

        JButton closeBtn = new JButton("확인");
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.addActionListener(e -> dispose());
        mainPanel.add(Box.createVerticalStrut(14));
        mainPanel.add(closeBtn);

        setContentPane(mainPanel);
        setBackground(Color.WHITE);
        setUndecorated(false);

        pack();
        setResizable(false);

        // 다이얼로그 크기 강제 제한 (더 작게)
        setMinimumSize(new Dimension(260, getHeight()));
        setMaximumSize(new Dimension(320, getHeight()));
        setPreferredSize(new Dimension(280, getHeight()));

        setLocationRelativeTo(parent);
    }

    private JPanel makeProfileField(String field, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel title = new JLabel(field);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        title.setBorder(new EmptyBorder(0, 0, 3, 0));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel val = new JLabel(value, SwingConstants.CENTER);
        val.setOpaque(true);
        val.setBackground(new Color(230, 242, 255));
        val.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        val.setBorder(new EmptyBorder(6, 8, 6, 8));
        val.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(title);
        panel.add(val);
        return panel;
    }
}
