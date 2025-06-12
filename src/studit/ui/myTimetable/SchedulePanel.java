package studit.ui.myTimetable;

import studit.domain.*;
import studit.service.Login;
import studit.service.StudyManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 사용자의 개인 및 그룹 시간표를 표시하는 메인 패널 클래스입니다.
 * - 스터디 선택 콤보박스, 조회 버튼, 개인/그룹 시간표 패널로 구성됩니다.
 * - 선택한 스터디 그룹에 따라 PersonalAvailabilityPanel과 GroupAvailabilityPanel을 동적으로 로드합니다.
 * - UI 갱신(refresh) 기능을 통해 실시간 데이터 반영이 가능합니다.
 * - 스터디 그룹 앱의 시간표 관리 메인 화면으로 사용됩니다.
 */
public class SchedulePanel extends JPanel {
    private JComboBox<StudyGroup> groupComboBox;
    private JPanel viewPanel;

    private final List<StudyGroup> allGroups;
    private final Login loginService;
    private final StudyManager studyManager;

    public SchedulePanel(List<StudyGroup> allGroups, Login loginService, StudyManager studyManager) {
        this.allGroups = allGroups;
        this.loginService = loginService;
        this.studyManager = studyManager;

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 헤더 패널
        JPanel headerPanel = createHeaderPanel();

        // 컨트롤 패널 (스터디 선택)
        JPanel controlPanel = createControlPanel();

        // 뷰 패널 (개인/그룹 시간표)
        viewPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        viewPanel.setBackground(Color.WHITE);

        // 전체 레이아웃 구성
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(Color.WHITE);
        topSection.add(headerPanel, BorderLayout.NORTH);
        topSection.add(controlPanel, BorderLayout.CENTER);

        add(topSection, BorderLayout.NORTH);
        add(viewPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // 제목
        JPanel emojiTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        emojiTitlePanel.setOpaque(false);

        JLabel emojiLabel = new JLabel("📅");
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28)); // 이모지용 폰트

        JLabel titleLabel = new JLabel(" 나의 시간표");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        titleLabel.setForeground(new Color(99, 102, 241));

        emojiTitlePanel.add(emojiLabel);
        emojiTitlePanel.add(titleLabel);

        headerPanel.add(emojiTitlePanel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        // 스터디 선택 영역
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        selectionPanel.setBackground(Color.WHITE);

        JLabel selectLabel = new JLabel("스터디 선택:");
        selectLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        selectLabel.setForeground(new Color(102, 102, 102));
        selectLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        // 콤보박스 스타일링
        groupComboBox = new JComboBox<>();
        groupComboBox.setPreferredSize(new Dimension(200, 35));
        groupComboBox.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        groupComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        User currentUser = loginService.getCurrentUser();
        List<StudyGroup> myGroups = studyManager.getMyStudies(allGroups, currentUser);
        for (StudyGroup group : myGroups) {
            groupComboBox.addItem(group);
        }

        // 조회 버튼 스타일링
        JButton refreshBtn = new JButton("조회");
        refreshBtn.setPreferredSize(new Dimension(80, 35));
        refreshBtn.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        refreshBtn.setBackground(new Color(66, 133, 244));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setBorder(BorderFactory.createEmptyBorder());
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 버튼 호버 효과
        refreshBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                refreshBtn.setBackground(new Color(64, 134, 216));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                refreshBtn.setBackground(new Color(74, 144, 226));
            }
        });

        refreshBtn.addActionListener(e -> updateView());

        selectionPanel.add(selectLabel);
        selectionPanel.add(groupComboBox);
        selectionPanel.add(Box.createHorizontalStrut(15));
        selectionPanel.add(refreshBtn);

        controlPanel.add(selectionPanel, BorderLayout.WEST);

        return controlPanel;
    }

    private void updateView() {
        StudyGroup selected = (StudyGroup) groupComboBox.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                    "스터디를 선택해주세요.",
                    "알림",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        User currentUser = loginService.getCurrentUser();

        viewPanel.removeAll();

        // 개인 시간표 패널
        JPanel personalPanel = createTimeTablePanel("개인 가능 시간",
                new PersonalAvailabilityPanel(selected, currentUser));

        // 그룹 시간표 패널
        JPanel groupPanel = createTimeTablePanel("그룹 가능 시간",
                new GroupAvailabilityPanel(selected));

        viewPanel.add(personalPanel);
        viewPanel.add(groupPanel);

        viewPanel.revalidate();
        viewPanel.repaint();
    }

    private JPanel createTimeTablePanel(String title, JPanel contentPanel) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // 제목
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        titleLabel.setForeground(new Color(64, 64, 64));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        container.add(titleLabel, BorderLayout.NORTH);
        container.add(contentPanel, BorderLayout.CENTER);

        return container;
    }

    /**
     * 데이터 갱신 메서드
     */
    public void refreshStudyComboBox() {
        SwingUtilities.invokeLater(() -> {
            User currentUser = loginService.getCurrentUser();
            if (currentUser == null) return;

            // 기존 아이템 제거
            groupComboBox.removeAllItems();

            // StudyManager에서 최신 데이터 가져오기
            List<StudyGroup> myGroups = studyManager.getMyStudies(allGroups, currentUser);

            // 새 데이터로 콤보박스 채우기
            for (StudyGroup group : myGroups) {
                groupComboBox.addItem(group);
            }

            // 강제 UI 갱신
            groupComboBox.revalidate();
            groupComboBox.repaint();
        });
    }



    public void refreshData() {
        SwingUtilities.invokeLater(() -> {
            updateView();
            revalidate();
            repaint();
        });
    }

    /**
     * 현재 사용자 반환
     */
    public User getCurrentUser() {
        return loginService.getCurrentUser();
    }

    /**
     * StudyManager 반환
     */
    public StudyManager getStudyManager() {
        return studyManager;
    }
}
