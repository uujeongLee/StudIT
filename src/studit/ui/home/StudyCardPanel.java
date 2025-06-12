package studit.ui.home;

import studit.domain.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 스터디 그룹 하나의 정보를 카드 형태로 보여주는 패널 클래스입니다.
 * - 랜덤 이모지, 과목명, 멤버/대기 인원, 태그, 신청 버튼 등을 표시합니다.
 * - 신청 버튼 클릭 시 가입/대기 신청 로직을 처리하며, 카드 클릭 시 상세 정보를 팝업으로 보여줍니다.
 * - 홈/추천 등 여러 곳에서 스터디 그룹 목록의 개별 카드로 사용됩니다.
 */
class StudyCardPanel extends JPanel {
    private static final String[] EMOJIS = {
            "💻", "👩‍💻", "🛠️", "📁", "🗃️", "📚", "📄", "📝", "🧾", "🔍",
            "🔧", "⌛", "📌", "🌐", "🔮", "🎯", "✨"
    };
    private static final Color PRIMARY_BLUE = new Color(59, 130, 246);

    private final StudyGroup group;
    private final User currentUser;
    private final Consumer<StudyGroup> onJoinCallback;
    private final Runnable refreshCallback;

    public StudyCardPanel(StudyGroup group, User currentUser, Consumer<StudyGroup> onJoinCallback) {
        this.group = group;
        this.currentUser = currentUser;
        this.onJoinCallback = onJoinCallback;
        this.refreshCallback = null;
        initializeComponents();
    }

    public StudyCardPanel(StudyGroup group, User currentUser, Runnable refreshCallback) {
        this.group = group;
        this.currentUser = currentUser;
        this.onJoinCallback = null;
        this.refreshCallback = refreshCallback;
        initializeComponents();
    }

    private void initializeComponents() {
        int fixedWidth = 280;
        int fixedHeight = 200;
        setPreferredSize(new Dimension(fixedWidth, fixedHeight));
        setMinimumSize(new Dimension(fixedWidth, fixedHeight));
        setMaximumSize(new Dimension(fixedWidth, fixedHeight));
        setBackground(Color.WHITE);
        setBorder(new CompoundBorder(
                new LineBorder(new Color(229, 231, 235), 1),
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

        JButton joinBtn = new JButton("+ 신청하기");
        joinBtn.setBackground(PRIMARY_BLUE);
        joinBtn.setForeground(Color.WHITE);
        joinBtn.setBorder(new EmptyBorder(6, 12, 6, 12));
        joinBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        joinBtn.setFocusPainted(false);

        if (onJoinCallback != null) {
            joinBtn.addActionListener(e -> handleJoinStudy(group, currentUser));
        } else {
            joinBtn.addActionListener(e -> handleApplication(group, currentUser, refreshCallback));
        }

        topPanel.add(titlePanel, BorderLayout.CENTER);
        topPanel.add(joinBtn, BorderLayout.EAST);

        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
        centerContainer.setOpaque(false);

        JLabel infoLabel = new JLabel(
                "<html><b>멤버:</b> " + group.getMembers().size() +
                        " <b>대기:</b> " + group.getWaitlist().size() + "</html>"
        );
        infoLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        centerContainer.add(infoLabel);

        add(topPanel, BorderLayout.NORTH);
        add(centerContainer, BorderLayout.CENTER);
        add(createTagPanel(group), BorderLayout.SOUTH);

        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showStudyDetails(group);
            }
        });
    }

    private void handleJoinStudy(StudyGroup group, User user) {
        if (user == null) {
            JOptionPane.showMessageDialog(this, "로그인이 필요합니다.", "로그인 필요", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (group.isMember(user)) {
            JOptionPane.showMessageDialog(this, "이미 가입된 스터디입니다.", "중복 가입", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (group.isInWaitlist(user)) {
            JOptionPane.showMessageDialog(this, "이미 대기열에 등록되어 있습니다.", "대기 중", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String confirmMessage = "'" + group.getSubject() + "' 스터디에 가입하시겠습니까?\n\n" +
                "스터디 정보:\n" +
                "- 주제: " + group.getSubject() + "\n" +
                "- 모드: " + group.getMode() + "\n" +
                "- 현재 인원: " + group.getMembers().size() + "/" + group.getMaxSize() + "\n" +
                "- 설명: " + group.getDescription();

        int option = JOptionPane.showConfirmDialog(this, confirmMessage, "스터디 가입 확인", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (option == JOptionPane.YES_OPTION) {
            if (onJoinCallback != null) {
                onJoinCallback.accept(group);
            }
        }
    }

    private JPanel createTagPanel(StudyGroup group) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        JLabel tagLabel = new JLabel("태그: " + String.join(", ", group.getTags()));
        tagLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        tagLabel.setForeground(new Color(107, 114, 128));
        panel.add(tagLabel);
        return panel;
    }

    private void handleApplication(StudyGroup group, User user, Runnable refresh) {
        int option = JOptionPane.showConfirmDialog(
                this,
                "'" + group.getSubject() + "' 스터디 가입을 신청하시겠습니까?",
                "확인",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            if (group.isMember(user) || group.isInWaitlist(user)) {
                JOptionPane.showMessageDialog(
                        this,
                        "이미 신청한 스터디입니다.",
                        "오류",
                        JOptionPane.ERROR_MESSAGE
                );
            } else {
                boolean success = group.apply(user);
                String message = success ?
                        "'" + group.getSubject() + "' 스터디 가입이 완료되었습니다!" :
                        "'" + group.getSubject() + "' 대기열에 추가되었습니다.";
                JOptionPane.showMessageDialog(
                        this,
                        message,
                        "알림",
                        success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE
                );
                refresh.run();
            }
        }
    }

    private void showStudyDetails(StudyGroup group) {
        StringBuilder details = new StringBuilder();
        details.append("📚 과목: ").append(group.getSubject()).append("\n");
        details.append("🖥️ 방식: ").append(group.getMode()).append("\n");
        details.append("👥 정원: ").append(group.getMembers().size()).append("/").append(group.getMaxSize()).append("\n");
        details.append("👑 리더: ").append(group.getLeader().getName()).append("\n");
        details.append("🏷️ 태그: ").append(String.join(", ", group.getTags())).append("\n");
        details.append("⏰ 확정 시간대:\n");

        if (group.getSchedule() != null && !group.getSchedule().getConfirmedTimeSlots().isEmpty()) {
            group.getSchedule().getConfirmedTimeSlots().forEach(slot ->
                    details.append("  - ").append(slot.toString()).append("\n")
            );
        } else {
            details.append("  미확정\n");
        }

        JOptionPane.showMessageDialog(
                this,
                details.toString(),
                "📋 스터디 상세 정보",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private String getRandomEmoji() {
        return EMOJIS[new Random().nextInt(EMOJIS.length)];
    }
}
