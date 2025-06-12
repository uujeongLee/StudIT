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

/**
 * 스터디 그룹의 시간표(가능 시간대)를 입력·저장할 수 있는 커스텀 패널 클래스입니다.
 * - 스터디/인원 선택, 시간표(가능 시간대) 클릭 선택, 저장 기능을 제공합니다.
 * - 각 시간 슬롯을 클릭해 선택/해제할 수 있으며, 선택된 시간은 파일로 저장됩니다.
 * - 저장된 시간표는 다음 실행 시 불러와서 자동 반영됩니다.
 * - 스터디 일정 조율, 회의 시간 확정 등에서 멤버별 가능 시간 입력 UI로 활용됩니다.
 */
public class CustomTimePanel extends JPanel {
    private JComboBox<StudyGroup> groupComboBox;
    private JComboBox<String> memberComboBox;
    private JPanel timePanel;
    private JButton saveButton;

    private final List<StudyGroup> allGroups;
    private final Login loginService;
    private final StudyManager studyManager;

    private final Map<TimeSlot, TimeSlotPanel> slotPanelMap = new HashMap<>();
    private static final Map<String, Set<TimeSlot>> userAvailabilityStore = new HashMap<>();

    private final Color startBlue = new Color(59, 130, 246);

    public CustomTimePanel(List<StudyGroup> allGroups, Login loginService, StudyManager studyManager) {
        this.allGroups = allGroups;
        this.loginService = loginService;
        this.studyManager = studyManager;

        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        loadFromFile();
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        topPanel.setBackground(Color.WHITE);

        JLabel selectLabel = new JLabel("스터디 선택:");
        selectLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        selectLabel.setForeground(new Color(40, 40, 40));

        groupComboBox = new JComboBox<>();
        groupComboBox.setBackground(new Color(246, 248, 250));
        groupComboBox.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        groupComboBox.setForeground(new Color(40, 40, 40));
        groupComboBox.setPreferredSize(new Dimension(250, 35));
        groupComboBox.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230), 1));
        groupComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof StudyGroup) {
                    label.setText(((StudyGroup) value).getSubject());
                }
                label.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
                label.setForeground(new Color(40, 40, 40));
                return label;
            }
        });

        User currentUser = loginService.getCurrentUser();
        List<StudyGroup> myGroups = studyManager.getMyStudies(studyManager.getAllStudyGroups(), currentUser);
        for (StudyGroup group : myGroups) {
            groupComboBox.addItem(group);
        }

        JLabel memberLabel = new JLabel("참여 인원:");
        memberLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        memberLabel.setForeground(new Color(40, 40, 40));

        memberComboBox = new JComboBox<>();
        memberComboBox.setBackground(new Color(246, 248, 250));
        memberComboBox.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        memberComboBox.setForeground(new Color(40, 40, 40));
        memberComboBox.setPreferredSize(new Dimension(100, 35));
        memberComboBox.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230), 1));

        groupComboBox.addActionListener(e -> updateMemberComboBox());
        updateMemberComboBox();

        saveButton = new JButton("가능 시간 저장");
        saveButton.setBackground(new Color(59, 130, 246));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        saveButton.addActionListener(e -> saveAvailability());

        topPanel.add(selectLabel);
        topPanel.add(groupComboBox);
        topPanel.add(memberLabel);
        topPanel.add(memberComboBox);
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

        add(cardPanel, BorderLayout.CENTER);
        buildTimeSlots();
    }

    private void updateMemberComboBox() {
        StudyGroup selectedGroup = (StudyGroup) groupComboBox.getSelectedItem();
        memberComboBox.removeAllItems();
        if (selectedGroup != null) {
            int memberCount = selectedGroup.getMembers().size();
            for (int i = 2; i <= memberCount; i++) {
                memberComboBox.addItem(i + "명");
            }
        }
    }

    private void buildTimeSlots() {
        Component[] components = timePanel.getComponents();
        for (Component comp : components) {
            timePanel.remove(comp);
        }

        slotPanelMap.clear();

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

        JLabel headerLabel = new JLabel("시간/날짜");
        headerLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        headerLabel.setForeground(new Color(40, 40, 40));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        headerLabel.setBackground(new Color(248, 249, 250));
        headerLabel.setOpaque(true);
        grid.add(headerLabel);

        for (LocalDate date : sortedDates) {
            JLabel label = new JLabel(
                    "<html><center>" + date.getMonthValue() + "/" + date.getDayOfMonth() +
                            "<br>" + getDayKorean(date.getDayOfWeek()) + "</center></html>"
            );
            label.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
            label.setForeground(new Color(40, 40, 40));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
            label.setBackground(new Color(248, 249, 250));
            label.setOpaque(true);
            grid.add(label);
        }

        LocalTime time = start;
        User currentUser = loginService.getCurrentUser();
        Set<TimeSlot> mySlots = userAvailabilityStore.getOrDefault(currentUser.getStudentId(), new HashSet<>());

        for (int r = 0; r < rowCount; r++) {
            String timeLabel = String.format("%02d:%02d", time.getHour(), time.getMinute());
            JLabel tLabel = new JLabel(timeLabel);
            tLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
            tLabel.setForeground(new Color(40, 40, 40));
            tLabel.setHorizontalAlignment(SwingConstants.CENTER);
            tLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
            tLabel.setBackground(new Color(248, 249, 250));
            tLabel.setOpaque(true);
            grid.add(tLabel);

            for (LocalDate date : sortedDates) {
                String day = getDayKorean(date.getDayOfWeek());
                String slot = time + "-" + time.plusMinutes(30);
                TimeSlot ts = new TimeSlot(day, slot);
                TimeSlotPanel panel = new TimeSlotPanel();
                if (mySlots.contains(ts)) {
                    panel.selected = true;
                    panel.setBackground(startBlue);
                }
                slotPanelMap.put(ts, panel);
                grid.add(panel);
            }
            time = time.plusMinutes(30);
        }
        timePanel.removeAll();
        JScrollPane scrollPane = (JScrollPane) timePanel.getParent().getParent();
        scrollPane.setViewportView(null);
        timePanel.add(grid, BorderLayout.CENTER);
        timePanel.revalidate();
        timePanel.repaint();
    }

    private String getDayKorean(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY: return "월";
            case TUESDAY: return "화";
            case WEDNESDAY: return "수";
            case THURSDAY: return "목";
            case FRIDAY: return "금";
            case SATURDAY: return "토";
            case SUNDAY: return "일";
            default: return "";
        }
    }

    private void saveAvailability() {
        StudyGroup group = (StudyGroup) groupComboBox.getSelectedItem();
        User user = loginService.getCurrentUser();
        if (group == null || user == null) return;

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

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        public TimeSlotPanel() {
            setPreferredSize(new Dimension(60, 40));
            setBackground(Color.WHITE);
            setOpaque(true); // ★ 이 줄을 반드시 추가!
            setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    toggle();
                }
            });
        }


        public boolean isSelected() {
            return selected;
        }

        public void toggle() {
            selected = !selected;
            setBackground(selected ? startBlue : Color.WHITE);
            repaint();
        }
    }
}
