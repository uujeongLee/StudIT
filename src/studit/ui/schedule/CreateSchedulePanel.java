package studit.ui.schedule;

import studit.domain.*;
import studit.service.Login;
import studit.service.StudyManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;

/**
 * 회의(스터디) 일정 추가 및 시간 후보 설정을 위한 패널 클래스입니다.
 * - 스터디 과목/참여 인원/방식/날짜/시간을 선택하고, 일정 생성을 진행할 수 있습니다.
 * - "회의 가능 시간 입력" 버튼을 누르면 CustomTimePanel로 전환되어 각 멤버의 가능 시간 입력이 이어집니다.
 * - StudyManager, Login 등 서비스와 연동하여 실시간 데이터로 동작합니다.
 */
public class CreateSchedulePanel extends JPanel {
    private JComboBox<String> subjectComboBox;
    private JComboBox<String> memberComboBox;
    private CustomCalendarPanel calendarPanel;
    private JComboBox<String> startTimeCombo, endTimeCombo;
    private JButton createButton;
    private JRadioButton onlineRadio;
    private JRadioButton offlineRadio;
    private JRadioButton hybridRadio;
    private final Login loginService;
    private final StudyManager studyManager;
    private JPanel contentPanel;
    private JButton nextButton;
    private CreateTimePanel createTimePanel;
    private List<StudyGroup> allGroups;

    public CreateSchedulePanel(Login loginService, StudyManager studyManager) {
        this.loginService = loginService;
        this.studyManager = studyManager;
        this.allGroups = studyManager.getAllStudyGroups();
        initComponents();
    }


    private void initComponents() {
        setLayout(new BorderLayout());
        add(createMainContent(), BorderLayout.CENTER);
//        subjectComboBox = new JComboBox<>();
//        refreshStudyComboBox();
    }


    private JPanel createMainContent() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(246, 248, 250));

        main.add(createHeader(), BorderLayout.NORTH);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(createFormCardWithScroll(), BorderLayout.CENTER);
        main.add(contentPanel, BorderLayout.CENTER);

        return main;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(246, 248, 250));
        header.setBorder(BorderFactory.createEmptyBorder(24, 32, 16, 32));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);

        JLabel emojiLabel = new JLabel("⏰");
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        JLabel textLabel = new JLabel("일정 추가하기");
        textLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        textLabel.setForeground(new Color(99, 102, 241));
        titlePanel.add(emojiLabel);
        titlePanel.add(Box.createHorizontalStrut(8));
        titlePanel.add(textLabel);

        header.add(titlePanel, BorderLayout.WEST);

        // 버튼 생성 및 이벤트 연결
        nextButton = new JButton("회의 가능 시간 입력  >");
        nextButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        nextButton.setBackground(new Color(66, 133, 244));
        nextButton.setForeground(Color.WHITE);
        nextButton.setFocusPainted(false);
        nextButton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        nextButton.addActionListener(e -> {
            contentPanel.removeAll();

            if (createTimePanel == null) {
                createTimePanel = new CreateTimePanel(studyManager, loginService);
            } else {
                createTimePanel.refreshData();
            }

            contentPanel.add(createTimePanel, BorderLayout.CENTER);

            nextButton.setText(nextButton.getText().contains("입력")
                    ? "최종 시간 확정"
                    : "회의 가능 시간 재입력");

            contentPanel.revalidate();
            contentPanel.repaint();
        });

        // 버튼을 헤더 오른쪽에 추가
        header.add(nextButton, BorderLayout.EAST);

        return header;
    }


    private JScrollPane createFormCardWithScroll() {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // 스터디 선택 섹션
        JPanel studySelectRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        studySelectRow.setBackground(Color.WHITE);

        // 스터디 과목 라벨
        JLabel subjectLabel = new JLabel("스터디 과목:");
        subjectLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));

        // 핵심 수정: 중복 생성 방지
        if (subjectComboBox == null) {
            subjectComboBox = createSubjectComboBox();
            styleComboBox(subjectComboBox);
        }

        // 참여 인원 라벨 (오타 수정: "참원" → "참여 인원")
        JLabel memberLabel = new JLabel("참여 인원:");
        memberLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));


        // 멤버 콤보박스 중복 생성 방지
        if (memberComboBox == null) {
            memberComboBox = new JComboBox<>();
            styleComboBox(memberComboBox);
        }

        // 컴포넌트 배치
        studySelectRow.add(subjectLabel);
        studySelectRow.add(subjectComboBox);
        studySelectRow.add(memberLabel);
        studySelectRow.add(memberComboBox);

        card.add(studySelectRow);

        // 스터디 방식 선택 섹션
        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        modePanel.setBackground(Color.WHITE);

        JLabel modeLabel = new JLabel("  스터디 방식:");
        modeLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));

        // 라디오 버튼 중복 생성 방지
        if (onlineRadio == null) {
            onlineRadio = new JRadioButton("온라인");
            onlineRadio.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
            onlineRadio.setBackground(Color.WHITE);
            onlineRadio.setSelected(true);
        }

        if (offlineRadio == null) {
            offlineRadio = new JRadioButton("오프라인");
            offlineRadio.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
            offlineRadio.setBackground(Color.WHITE);
        }

        if (hybridRadio == null) {
            hybridRadio = new JRadioButton("혼합");
            hybridRadio.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
            hybridRadio.setBackground(Color.WHITE);
        }

        // 버튼 그룹 생성 (중복 방지)
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(onlineRadio);
        modeGroup.add(offlineRadio);
        modeGroup.add(hybridRadio);

        modePanel.add(modeLabel);
        modePanel.add(onlineRadio);
        modePanel.add(offlineRadio);
        modePanel.add(hybridRadio);

        card.add(modePanel);
        card.add(Box.createVerticalStrut(24));

        // 일정 라벨
        JLabel scheduleLabel = new JLabel("날짜 및 시간 선택");
        scheduleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        scheduleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(scheduleLabel);
        card.add(Box.createVerticalStrut(12));

        // 캘린더 및 시간 선택 섹션
        card.add(createCalendarWithTimeSection());
        card.add(Box.createVerticalStrut(24));

        // 스크롤 패널 생성
        JScrollPane scrollPane = new JScrollPane(card,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, // 수정: 필요시에만 표시
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // null 대신 EmptyBorder 사용
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // 핵심 수정: UI 구성 완료 후 이벤트 리스너 등록 및 데이터 갱신
        SwingUtilities.invokeLater(() -> {
            // 이벤트 리스너 등록 (기존 리스너 제거 후 새로 등록)
            removeExistingListeners();
            subjectComboBox.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> updateMemberComboBox());
            });

            // 초기 데이터 갱신
            updateMemberComboBox();

            // UI 강제 갱신
            card.revalidate();
            card.repaint();
        });

        return scrollPane;
    }


    private void removeExistingListeners() {
        if (subjectComboBox != null) {
            // 기존에 등록된 모든 ActionListener 제거
            ActionListener[] listeners = subjectComboBox.getActionListeners();
            for (ActionListener listener : listeners) {
                subjectComboBox.removeActionListener(listener);
            }
        }
    }

    private JComboBox<String> createSubjectComboBox() {
        User currentUser = loginService.getCurrentUser();
        if (currentUser == null) {
            JComboBox<String> comboBox = new JComboBox<>();
            comboBox.addItem("로그인이 필요합니다");
            return comboBox;
        }

        // 실시간으로 StudyManager에서 데이터 가져오기
        List<StudyGroup> allGroups = studyManager.getAllStudyGroups();
        List<StudyGroup> myGroups = studyManager.getMyStudies(allGroups, currentUser);

        JComboBox<String> comboBox = new JComboBox<>();
        Set<String> uniqueSubjects = new LinkedHashSet<>();
        for (StudyGroup group : myGroups) {
            uniqueSubjects.add(group.getSubject());
        }
        for (String subject : uniqueSubjects) {
            comboBox.addItem(subject);
        }

        if (uniqueSubjects.isEmpty()) {
            comboBox.addItem("참여 중인 스터디가 없습니다");
        }

        return comboBox;
    }



    private void updateMemberComboBox() {
        String selectedSubject = (String) subjectComboBox.getSelectedItem();

        // 전체 스터디 목록에서 검색 (allGroups가 아닌 전체 목록 사용)
        User currentUser = loginService.getCurrentUser();
        List<StudyGroup> allGroups = studyManager.getAllStudyGroups(); // 실시간 데이터 가져오기
        List<StudyGroup> myGroups = studyManager.getMyStudies(allGroups, currentUser);

        StudyGroup selectedGroup = myGroups.stream()
                .filter(g -> g.getSubject().equals(selectedSubject))
                .findFirst()
                .orElse(null);

        memberComboBox.removeAllItems();
        if (selectedGroup != null) {
            int memberCount = selectedGroup.getMembers().size();
            for (int i = 2; i <= memberCount; i++) {
                memberComboBox.addItem(i + "명");
            }
        }
    }


    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setBackground(new Color(246, 248, 250));
        comboBox.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        comboBox.setPreferredSize(new Dimension(200, 30));
        comboBox.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230), 1));
    }

    private JPanel createCalendarWithTimeSection() {
        JPanel calendarWithTimePanel = new JPanel();
        calendarWithTimePanel.setLayout(new BoxLayout(calendarWithTimePanel, BoxLayout.Y_AXIS));
        calendarWithTimePanel.setBackground(Color.WHITE);

        calendarPanel = new CustomCalendarPanel();
        calendarPanel.setPreferredSize(new Dimension(600, 300));
        calendarPanel.setBackground(Color.WHITE);
        calendarWithTimePanel.add(calendarPanel);

        JPanel timePanel = new JPanel(new GridBagLayout());
        timePanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel startLabel = new JLabel("가능 시작 시간:");
        startLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        timePanel.add(startLabel, gbc);

        gbc.gridx = 1;
        startTimeCombo = createTimeComboBox();
        timePanel.add(startTimeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel endLabel = new JLabel("가능 종료 시간:");
        endLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        timePanel.add(endLabel, gbc);

        gbc.gridx = 1;
        endTimeCombo = createTimeComboBox();
        endTimeCombo.setSelectedIndex(endTimeCombo.getItemCount() - 1);
        timePanel.add(endTimeCombo, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        createButton = new JButton("일정 생성");
        createButton.setBackground(new Color(66, 133, 244));
        createButton.setForeground(Color.WHITE);
        createButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        createButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        createButton.setPreferredSize(new Dimension(140, 40));
        createButton.addActionListener(e -> handleCreate());
        timePanel.add(createButton, gbc);

        calendarWithTimePanel.add(timePanel);
        return calendarWithTimePanel;
    }

    private JComboBox<String> createTimeComboBox() {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setBackground(new Color(246, 248, 250));
        comboBox.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        comboBox.setPreferredSize(new Dimension(100, 30));
        comboBox.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230), 1));
        for (int hour = 9; hour <= 21; hour++) {
            comboBox.addItem(String.format("%02d:00", hour));
            comboBox.addItem(String.format("%02d:30", hour));
        }
        return comboBox;
    }

    private void handleCreate() {
        String selectedSubject = (String) subjectComboBox.getSelectedItem();

        User currentUser = loginService.getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "로그인이 필요합니다.");
            return;
        }

        // StudyManager에서 직접 최신 데이터 가져오기
        List<StudyGroup> allGroups = studyManager.getAllStudyGroups();
        List<StudyGroup> myGroups = studyManager.getMyStudies(allGroups, currentUser);

        StudyGroup selectedGroup = myGroups.stream()
                .filter(g -> g.getSubject().equals(selectedSubject))
                .findFirst()
                .orElse(null);

        Set<LocalDate> selectedDates = calendarPanel.getSelectedDates();
        String start = (String) startTimeCombo.getSelectedItem();
        String end = (String) endTimeCombo.getSelectedItem();

        if (selectedGroup == null) {
            JOptionPane.showMessageDialog(this, "스터디를 선택해주세요.");
            return;
        }
        if (selectedDates.isEmpty()) {
            JOptionPane.showMessageDialog(this, "날짜를 한 개 이상 선택해주세요.");
            return;
        }
        if (start == null || end == null) {
            JOptionPane.showMessageDialog(this, "시간 범위를 선택해주세요.");
            return;
        }

        LocalTime startTime = LocalTime.parse(start);
        LocalTime endTime = LocalTime.parse(end);
        if (!startTime.isBefore(endTime)) {
            JOptionPane.showMessageDialog(this, "시작 시간은 종료 시간보다 앞서야 합니다.");
            return;
        }

        selectedGroup.getSchedule().setCandidateDates(selectedDates);
        selectedGroup.getSchedule().setTimeRange(startTime, endTime);

        String studyMode = onlineRadio.isSelected() ? "온라인" :
                offlineRadio.isSelected() ? "오프라인" : "혼합";

        JOptionPane.showMessageDialog(this,
                "새 회의 일정이 생성되었습니다!\n" +
                        "과목: " + selectedGroup.getSubject() + "\n" +
                        "참여 인원: " + memberComboBox.getSelectedItem() + "\n" +
                        "진행 방식: " + studyMode);
    }


    public void setPreferredPanelSize(int width, int height) {
        setPreferredSize(new Dimension(width, height));
    }

    public void refreshStudyComboBox() {
        if (subjectComboBox == null) return;

        SwingUtilities.invokeLater(() -> {
            User currentUser = loginService.getCurrentUser();
            if (currentUser == null) return;

            // 최신 데이터로 갱신
            List<StudyGroup> allGroups = studyManager.getAllStudyGroups();
            List<StudyGroup> myGroups = studyManager.getMyStudies(allGroups, currentUser);

            subjectComboBox.removeAllItems();
            Set<String> uniqueSubjects = new LinkedHashSet<>();
            for (StudyGroup group : myGroups) {
                uniqueSubjects.add(group.getSubject());
            }
            for (String subject : uniqueSubjects) {
                subjectComboBox.addItem(subject);
            }

            if (uniqueSubjects.isEmpty()) {
                subjectComboBox.addItem("참여 중인 스터디가 없습니다");
            }

            updateMemberComboBox();

            // 강제 UI 갱신
            subjectComboBox.revalidate();
            subjectComboBox.repaint();

            // 전체 패널 갱신
            this.revalidate();
            this.repaint();
        });
    }

// updateAllGroups 메서드 제거 (더 이상 필요 없음)


    public void forceRefreshStudyComboBox() {
        SwingUtilities.invokeLater(() -> {
            User currentUser = loginService.getCurrentUser();
            if (currentUser == null) return;

            // StudyManager에서 직접 최신 데이터 가져오기
            List<StudyGroup> allGroups = studyManager.getAllStudyGroups();
            List<StudyGroup> myGroups = studyManager.getMyStudies(allGroups, currentUser);

            subjectComboBox.removeAllItems();
            Set<String> uniqueSubjects = new LinkedHashSet<>();
            for (StudyGroup group : myGroups) {
                uniqueSubjects.add(group.getSubject());
            }
            for (String subject : uniqueSubjects) {
                subjectComboBox.addItem(subject);
            }

            if (uniqueSubjects.isEmpty()) {
                subjectComboBox.addItem("참여 중인 스터디가 없습니다");
            }

            updateMemberComboBox();

            // UI 강제 갱신
            subjectComboBox.revalidate();
            subjectComboBox.repaint();
        });
    }

}
