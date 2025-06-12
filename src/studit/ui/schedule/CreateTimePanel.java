package studit.ui.schedule;

import studit.domain.*;
import studit.service.Login;
import studit.service.StudyManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 스터디 그룹별 멤버들의 시간 후보 선택과 겹침 현황을 시각화하고,
 * 사용자가 자신의 가능 시간을 직접 선택·저장할 수 있도록 하는 패널 클래스입니다.
 * - 스터디 선택, 시간표(가능 시간대) 표시, 저장 기능을 제공합니다.
 * - 각 시간 슬롯별로 멤버들의 겹침 인원 수를 색상 농도로 표시합니다.
 * - 사용자가 시간 칸을 클릭해 직접 선택/해제할 수 있으며, 저장 시 파일로 persist됩니다.
 * - 스터디 일정 조율, 회의 시간 확정 등 다양한 협업 UI에서 활용됩니다.
 */
public class CreateTimePanel extends JPanel {
    private List<StudyGroup> allGroups;
    private JComboBox<StudyGroup> groupComboBox;
    private JPanel timePanel;
    private JButton saveButton;
    private final Login loginService;
    private final StudyManager studyManager;
    private final Map<TimeSlot, TimeSlotPanel> slotPanelMap = new HashMap<>();
    private static final Map<String, Set<TimeSlot>> userAvailabilityStore = new HashMap<>();
    private final Color startBlue = new Color(173, 216, 230);

    public CreateTimePanel(StudyManager studyManager, Login loginService) { // ✅ 생성자
        this.studyManager = studyManager;
        this.loginService = loginService;
        this.allGroups = studyManager.getAllStudyGroups();
        initComponents();
    }

    public void refreshData() { // ✅ 메서드 추가
        this.allGroups = studyManager.getAllStudyGroups();
        buildTimeSlots();
        revalidate();
        repaint();
    }


    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        topPanel.setBackground(Color.WHITE);

        JLabel selectLabel = new JLabel("스터디 선택:");
        selectLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));

        groupComboBox = new JComboBox<>();
        groupComboBox.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        User currentUser = loginService.getCurrentUser();
        for (StudyGroup group : studyManager.getMyStudies(allGroups, currentUser)) {
            groupComboBox.addItem(group);
        }
        groupComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof StudyGroup) {
                    label.setText(((StudyGroup) value).getSubject());
                }
                return label;
            }
        });

        saveButton = new JButton("가능 시간 저장");
        saveButton.setBackground(new Color(70, 130, 255));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        saveButton.setBorder(BorderFactory.createEmptyBorder(12, 36, 12, 36));
        saveButton.addActionListener(e -> handleSave());

        topPanel.add(selectLabel);
        topPanel.add(groupComboBox);
        topPanel.add(saveButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        timePanel = new JPanel();
        timePanel.setBackground(Color.WHITE);
        timePanel.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(timePanel);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        groupComboBox.addActionListener(e -> buildTimeSlots());

        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 2),
                BorderFactory.createEmptyBorder(32, 32, 32, 32)
        ));
        cardPanel.add(mainPanel, BorderLayout.CENTER);

        add(cardPanel);
        buildTimeSlots();
    }

    private void buildTimeSlots() {
        StudyGroup group = (StudyGroup) groupComboBox.getSelectedItem();
        if (group == null) return;

        timePanel.removeAll();
        slotPanelMap.clear();

        Set<LocalDate> dates = group.getSchedule().getCandidateDates();
        LocalTime start = group.getSchedule().getStartTime();
        LocalTime end = group.getSchedule().getEndTime();
        if (dates == null || dates.isEmpty() || start == null || end == null) return;

        List<LocalDate> sortedDates = new ArrayList<>(dates);
        sortedDates.sort(LocalDate::compareTo);

        int rowCount = (int) ((Duration.between(start, end).toMinutes()) / 30);
        int colCount = sortedDates.size() + 1;

        JPanel grid = new JPanel(new GridLayout(rowCount + 1, colCount, 2, 2));
        grid.setBackground(Color.WHITE);

        // 열 추가
        grid.add(new JLabel("시간/날짜", SwingConstants.CENTER));
        for (LocalDate date : sortedDates) {
            JLabel label = new JLabel("<html><center>"
                    + date.getMonthValue() + "/" + date.getDayOfMonth()
                    + "<br>" + TimeSlotUtils.getDayKor(date.getDayOfWeek())
                    + "</center></html>", SwingConstants.CENTER);
            grid.add(label);
        }

        // 모든 멤버의 시간별 겹치는 인원 수 계산
        Map<TimeSlot, Integer> overlapCount = new HashMap<>();
        List<User> members = group.getMembers().stream()
                .map(StudyMember::getUser)
                .collect(Collectors.toList());
        for (User member : members) {
            Set<TimeSlot> slots = userAvailabilityStore.getOrDefault(member.getStudentId(), Set.of());
            slots.forEach(slot -> overlapCount.put(slot, overlapCount.getOrDefault(slot, 0) + 1));
        }

        // 시간 슬롯 생성
        LocalTime time = start;
        for (int r = 0; r < rowCount; r++) {
            String timeLabel = String.format("%02d:%02d", time.getHour(), time.getMinute());
            grid.add(new JLabel(timeLabel, SwingConstants.CENTER));

            for (LocalDate date : sortedDates) {
                String day = TimeSlotUtils.getDayKor(date.getDayOfWeek());
                TimeSlot slot = new TimeSlot(day, time.toString());

                int count = overlapCount.getOrDefault(slot, 0);
                TimeSlotPanel panel = new TimeSlotPanel(count);
                slotPanelMap.put(slot, panel);
                grid.add(panel);
            }
            time = time.plusMinutes(30);
        }

        timePanel.add(grid, BorderLayout.CENTER);
        timePanel.revalidate();
        timePanel.repaint();
    }

    public void handleSave() {
        User user = loginService.getCurrentUser();
        Set<TimeSlot> selected = new HashSet<>();
        for (Map.Entry<TimeSlot, TimeSlotPanel> entry : slotPanelMap.entrySet()) {
            if (entry.getValue().isSelected()) {
                selected.add(entry.getKey());
            }
        }
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "시간을 최소 1개 이상 선택해주세요.");
            return;
        }
        userAvailabilityStore.put(user.getStudentId(), selected);
        saveToFile();
        JOptionPane.showMessageDialog(this, "내가 선택한 시간이 저장되었습니다!");
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("availability.dat"))) {
            oos.writeObject(userAvailabilityStore);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("availability.dat"))) {
            userAvailabilityStore.putAll((Map<String, Set<TimeSlot>>) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            userAvailabilityStore.clear();
        }
    }

    class TimeSlotPanel extends JPanel {
        private boolean selected = false;
        private final int overlapCount; // 겹치는 인원 수 표시
        private static final Color SELECTED_COLOR = new Color(255, 165, 0, 150);

        // 생성자에 겹치는 인원 수 추가
        public TimeSlotPanel(int overlapCount) {
            this.overlapCount = overlapCount;
            setPreferredSize(new Dimension(60, 40));
            setBackground(calculateBaseColor());
            setBorder(BorderFactory.createLineBorder(Color.GRAY));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    toggle();
                }
            });
        }

        // 배경색 계산 (겹치는 인원 수 반영)
        private Color calculateBaseColor() {
            int alpha = Math.min(255, 50 + overlapCount * 40);
            return new Color(70, 130, 255, alpha);
        }

        // 선택 상태에 따라 배경색 변경
        public void toggle() {
            selected = !selected;
            if (selected) {
                setBackground(SELECTED_COLOR);
            } else {
                setBackground(calculateBaseColor());
            }
            repaint();
        }

        public boolean isSelected() {
            return selected;
        }

    }

}
