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
    private final OnCompleteListener completeListener;

    private final Map<TimeSlot, TimeSlotPanel> slotPanelMap = new HashMap<>();
    private static final Map<String, Set<TimeSlot>> userAvailabilityStore = new HashMap<>();

    private final Color startBlue = new Color(59, 130, 246);

    public CustomTimePanel(List<StudyGroup> allGroups, Login loginService, StudyManager studyManager, OnCompleteListener listener) {
        this.allGroups = allGroups;
        this.loginService = loginService;
        this.studyManager = studyManager;
        this.completeListener = listener;

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
        groupComboBox.setPreferredSize(new Dimension(250, 35));
        User currentUser = loginService.getCurrentUser();
        List<StudyGroup> myGroups = studyManager.getMyStudies(allGroups, currentUser);
        for (StudyGroup group : myGroups) {
            groupComboBox.addItem(group);
        }

        memberComboBox = new JComboBox<>();
        memberComboBox.setPreferredSize(new Dimension(100, 35));
        updateMemberComboBox();

        // ✅ CustomTimePanel.java (1단계)
        saveButton = new JButton("가능 시간 저장 및 다음");
        saveButton.addActionListener(e -> {
            List<TimeSlot> selected = getSelectedTimeSlots();
            if (selected.isEmpty()) {
                JOptionPane.showMessageDialog(this, "최소 1개 이상 선택해야 합니다");
                return;
            }
            userAvailabilityStore.put(currentUser.getStudentId(), new HashSet<>(selected));
            saveToFile();
            completeListener.onComplete(selected); // ✅ 이 때 다음 화면으로
        });


        groupComboBox.addActionListener(e -> {
            updateMemberComboBox();
            buildTimeSlots();
        });

        topPanel.add(selectLabel);
        topPanel.add(groupComboBox);
        topPanel.add(new JLabel("참여 인원:"));
        topPanel.add(memberComboBox);
        topPanel.add(saveButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        timePanel = new JPanel(new BorderLayout());
        timePanel.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(timePanel);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
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
        timePanel.removeAll();
        slotPanelMap.clear();

        StudyGroup group = (StudyGroup) groupComboBox.getSelectedItem();
        if (group == null) return;

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

        grid.add(new JLabel("시간/날짜", SwingConstants.CENTER));
        for (LocalDate date : sortedDates) {
            grid.add(new JLabel(date.getMonthValue() + "/" + date.getDayOfMonth() + "(" + getDayKorean(date.getDayOfWeek()) + ")", SwingConstants.CENTER));
        }

        LocalTime time = start;
        User currentUser = loginService.getCurrentUser();
        Set<TimeSlot> mySlots = userAvailabilityStore.getOrDefault(currentUser.getStudentId(), new HashSet<>());

        for (int r = 0; r < rowCount; r++) {
            grid.add(new JLabel(time.toString(), SwingConstants.CENTER));
            for (LocalDate date : sortedDates) {
                TimeSlot ts = new TimeSlot(getDayKorean(date.getDayOfWeek()), time + "-" + time.plusMinutes(30));
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

        timePanel.add(grid, BorderLayout.CENTER);
        timePanel.revalidate();
        timePanel.repaint();
    }

    private String getDayKorean(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "월";
            case TUESDAY -> "화";
            case WEDNESDAY -> "수";
            case THURSDAY -> "목";
            case FRIDAY -> "금";
            case SATURDAY -> "토";
            case SUNDAY -> "일";
        };
    }
    private List<TimeSlot> getSelectedTimeSlots() {
        List<TimeSlot> selected = new ArrayList<>();
        for (Map.Entry<TimeSlot, TimeSlotPanel> entry : slotPanelMap.entrySet()) {
            if (entry.getValue().isSelected()) {
                selected.add(entry.getKey());
            }
        }
        return selected;
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
            Object obj = ois.readObject();
            if (obj instanceof Map<?, ?> map) {
                userAvailabilityStore.clear();
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    if (entry.getKey() instanceof String && entry.getValue() instanceof Set<?> set) {
                        userAvailabilityStore.put((String) entry.getKey(), (Set<TimeSlot>) set);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            userAvailabilityStore.clear();
        }
    }

    private class TimeSlotPanel extends JPanel {
        private boolean selected = false;

        public TimeSlotPanel() {
            setPreferredSize(new Dimension(60, 40));
            setBackground(Color.WHITE);
            setOpaque(true);
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