package studit.ui.home;

import studit.domain.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Random;

/**
 * 내 스터디 카드 UI를 담당하는 JPanel 클래스입니다.
 * - 각 스터디 그룹(StudyGroup)의 정보를 카드 형태로 보여줍니다.
 * - 랜덤 이모지, 과목명, 멤버 수, 대기 수, 태그, 참여 상태(참여중/대기중/신청중) 버튼을 표시합니다.
 * - 스터디 그룹 목록(마이페이지, 홈 등)에서 내 참여/신청/대기 스터디를 시각적으로 구분해줍니다.
 */
public class MyStudyCardPanel extends JPanel {
    private static final String[] EMOJIS = {
            "💻", "👩‍💻", "🛠️", "📁", "🗃️", "📚", "📄", "📝", "🧾", "🔍",
            "🔧", "⌛", "📌", "🌐", "🔮", "🎯", "✨"
    };
    private static final Color PARTICIPATE_PURPLE = new Color(128, 64, 255);
    private static final Color WAIT_BLUE = new Color(37, 64, 143);
    private static final Color PRIMARY_BLUE = new Color(59, 130, 246);

    public MyStudyCardPanel(StudyGroup group, User currentUser) {
        setPreferredSize(new Dimension(280, 200));
        setBackground(new Color(230, 242, 255));
        setBorder(new CompoundBorder(
                new LineBorder(PRIMARY_BLUE, 2),
                new EmptyBorder(10, 10, 10, 10)
        ));
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);

        JLabel emojiLabel = new JLabel(getRandomEmoji());
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        JLabel subjectLabel = new JLabel(" " + group.getSubject());
        subjectLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        titlePanel.add(emojiLabel);
        titlePanel.add(subjectLabel);

        boolean isMember = group.isMember(currentUser);
        boolean isInWaitlist = group.isInWaitlist(currentUser);
        Color btnColor = isMember ? PARTICIPATE_PURPLE : WAIT_BLUE;
        String btnText = isMember ? "참여중" : (isInWaitlist ? "대기중" : "신청중");

        JButton statusBtn = new JButton(btnText);
        statusBtn.setBackground(btnColor);
        statusBtn.setForeground(Color.WHITE);
        statusBtn.setOpaque(true);
        statusBtn.setBorder(new EmptyBorder(6, 18, 6, 18));
        statusBtn.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        statusBtn.setFocusPainted(false);
        statusBtn.setEnabled(false);

        topPanel.add(titlePanel, BorderLayout.CENTER);
        topPanel.add(statusBtn, BorderLayout.EAST);

        JPanel midPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        midPanel.setOpaque(false);
        midPanel.add(new JLabel("멤버 수: " + group.getMembers().size()));
        midPanel.add(new JLabel("대기 수: " + group.getWaitlist().size()));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottomPanel.setOpaque(false);
        JLabel tagLabel = new JLabel("태그: " + String.join(", ", group.getTags()));
        tagLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        tagLabel.setForeground(PRIMARY_BLUE);
        bottomPanel.add(tagLabel);

        add(topPanel, BorderLayout.NORTH);
        add(midPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private String getRandomEmoji() {
        return EMOJIS[new Random().nextInt(EMOJIS.length)];
    }
}
