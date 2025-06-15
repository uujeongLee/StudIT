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

public class CreateTimePanel extends JPanel {
    private List<StudyGroup> allGroups;
    private JComboBox<StudyGroup> groupComboBox;
    private JPanel timePanel;
    private JButton confirmButton;
    private final Login loginService;
    private final StudyManager studyManager;
    private final Map<TimeSlot, TimeSlotPanel> slotPanelMap = new HashMap<>();
    private static final Map<String, Set<TimeSlot>> userAvailabilityStore = new HashMap<>();
    private final Color startBlue = new Color(173, 216, 230);

    public CreateTimePanel(List<StudyGroup> allGroups, Login loginService, StudyManager studyManager) {
        this.loginService = loginService;
        this.studyManager = studyManager;
        this.allGroups = allGroups;

        loadFromFile();
        initComponents();
    }


    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("availability.dat"))) {
            Object obj = ois.readObject();
            if (obj instanceof Map<?, ?> map) {
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    if (entry.getKey() instanceof String && entry.getValue() instanceof Set<?> set) {
                        String key = (String) entry.getKey();
                        Set<TimeSlot> value = (Set<TimeSlot>) set;

                        // 기존 store에 해당 사용자의 값이 없을 때만 추가
                        if (!userAvailabilityStore.containsKey(key)) {
                            userAvailabilityStore.put(key, value);
                        }
                    }
                }
            }
            System.out.println("✅ 병합된 데이터: " + userAvailabilityStore);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("⚠️ 저장된 데이터 없음 또는 로딩 실패");
        }

        StudyGroup group = studyManager.getAllStudyGroups().stream().findFirst().orElse(null);
        if(group !=null)
        {
            Schedule schedule = group.getSchedule();
            for (StudyMember member : group.getMembers()) {
                String sid = member.getUser().getStudentId();
                Set<TimeSlot> slots = userAvailabilityStore.getOrDefault(sid, Set.of());
                schedule.setAvailability(member, slots);
            }
        }
    }


    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        topPanel.setBackground(Color.WHITE);

        confirmButton = new JButton("최종 시간 확정");
        confirmButton.setBackground(new Color(88, 101, 242));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        confirmButton.setBorder(BorderFactory.createEmptyBorder(12, 36, 12, 36));

        confirmButton.addActionListener(e -> {
            // 현재 그룹 찾기
            StudyGroup group = studyManager.getAllStudyGroups().stream().findFirst().orElse(null);
            if (group == null) return;

            Schedule schedule = group.getSchedule();

            // 선택된 TimeSlot 저장
            Set<TimeSlot> selectedSlots = slotPanelMap.entrySet().stream()
                    .filter(entry -> entry.getValue().isSelected())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());

            // 확정된 시간 저장
            for (TimeSlot slot : selectedSlots) {
                schedule.confirmTimeSlot(slot); // ✅ 확정 시간에 저장
            }

            JOptionPane.showMessageDialog(this, "최종 시간이 확정되었습니다!");
        });

        topPanel.add(confirmButton);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        timePanel = new JPanel();
        timePanel.setBackground(Color.WHITE);
        timePanel.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(timePanel);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

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
        StudyGroup group = studyManager.getAllStudyGroups().stream().findFirst().orElse(null);
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

        JLabel timeTitle = new JLabel("시간/날짜", SwingConstants.CENTER);
        timeTitle.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        grid.add(timeTitle);

        for (LocalDate date : sortedDates) {
            JLabel label = new JLabel("<html><center>"
                    + date.getMonthValue() + "/" + date.getDayOfMonth()
                    + "<br>" + TimeSlotUtils.getDayKor(date.getDayOfWeek())
                    + "</center></html>", SwingConstants.CENTER);
            label.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
            grid.add(label);
        }

        Map<TimeSlot, Integer> overlapCount = new HashMap<>();
        List<User> members = group.getMembers().stream()
                .map(StudyMember::getUser)
                .collect(Collectors.toList());
        for (User member : members) {
            Set<TimeSlot> slots = userAvailabilityStore.getOrDefault(member.getStudentId(), Set.of());
            slots.forEach(slot -> overlapCount.put(slot, overlapCount.getOrDefault(slot, 0) + 1));
        }

        System.out.println("모든 저장된 slot 비교용:");
        for (TimeSlot slot : overlapCount.keySet()) {
            System.out.println(" - slot: " + slot);
        }

        LocalTime time = start;
        User currentUser = loginService.getCurrentUser(); // 현재 사용자 미리 가져오기
        Set<TimeSlot> myAvailability = userAvailabilityStore.getOrDefault(currentUser.getStudentId(), Set.of());
        Set<TimeSlot> confirmed = group.getSchedule().getConfirmedTimeSlots(); // 확정 시간도 미리 가져오기
        for (int r = 0; r < rowCount; r++) {
            String timeLabel = String.format("%02d:%02d", time.getHour(), time.getMinute());
            JLabel timeCell = new JLabel(timeLabel, SwingConstants.CENTER);
            timeCell.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
            grid.add(timeCell);

            for (LocalDate date : sortedDates) {
                String day = TimeSlotUtils.getDayKor(date.getDayOfWeek());
                String range = String.format("%02d:%02d-%02d:%02d",
                        time.getHour(), time.getMinute(),
                        time.plusMinutes(30).getHour(), time.plusMinutes(30).getMinute());
                TimeSlot slot = new TimeSlot(day, range);


                int count = overlapCount.getOrDefault(slot, 0);
                int totalUsers = members.size();
                TimeSlotPanel panel = new TimeSlotPanel(count, totalUsers);
                panel.setBackground(getPurpleByOverlap(count, totalUsers));

                slotPanelMap.put(slot, panel);
                grid.add(panel);

                System.out.println("Slot " + slot + ": count = " + count);
            }

            time = time.plusMinutes(30);
        }

        timePanel.add(grid, BorderLayout.CENTER);
        timePanel.revalidate();
        timePanel.repaint();

        System.out.println("=== 현재 그룹 멤버들의 저장된 가능 시간 ===");
        for (User member : members) {
            Set<TimeSlot> slots = userAvailabilityStore.get(member.getStudentId());
            System.out.println(member.getStudentId() + " → " + slots);
        }
    }

    class TimeSlotPanel extends JPanel {
        private final int overlapCount;
        private final int totalUsers; // ✅ 추가
        private boolean selected = false;

        public TimeSlotPanel(int overlapCount, int totalUsers) {
            this.overlapCount = overlapCount;
            this.totalUsers = totalUsers;
            setPreferredSize(new Dimension(60, 40));
            setBackground(getPurpleByOverlap(overlapCount, totalUsers));
            setBorder(BorderFactory.createLineBorder(Color.GRAY));
            setOpaque(true);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    toggle();
                }
            });
        }


        private void toggle() {
            selected = !selected;
            if (selected) {
                setBackground(new Color(70, 130, 255)); // 파란색 (불투명)
            } else {
                setBackground(getPurpleByOverlap(overlapCount, totalUsers));
            }
            repaint();
        }

        public boolean isSelected() {
            return selected;
        }
    }

    private Color getPurpleByOverlap(int count, int total) {
        if (total == 0 || count == 0) return Color.WHITE;

        float ratio = (float) count / total;

        // 비율 기준: 10% ~ 100%
        // 연한 보라 (206,170,240) → 진한 보라 (88,0,176)
        int r = interpolate(206, 88, ratio);
        int g = interpolate(170, 0, ratio);
        int b = interpolate(240, 176, ratio);

        return new Color(r, g, b);
    }

    private int interpolate(int start, int end, float ratio) {
        return (int) (start + (end - start) * ratio);
    }

}
