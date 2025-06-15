package studit.ui.myTimetable;

import studit.domain.*;
import studit.ui.schedule.TimeSlotUtils;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

/**
 * 스터디 그룹의 전체 멤버들의 시간대별 가능 여부(겹침 정도/확정 시간)를 표 형태로 보여주는 패널 클래스입니다.
 * - 각 셀은 시간/날짜별로 멤버 중 몇 명이 가능한지 색상 농도와 숫자로 시각화합니다.
 * - 확정된 시간은 진한 보라색과 체크 표시로 구분됩니다.
 * - 헤더, 날짜, 시간, 태그 등 UI 스타일과 가독성을 고려한 Swing 컴포넌트로 구현되어 있습니다.
 * - 스터디 그룹 일정 관리, 시간표 UI 등에서 사용됩니다.
 */
public class GroupAvailabilityPanel extends JPanel {
    private final StudyGroup group;
    private final Map<TimeSlot, JCheckBox> checkBoxMap = new HashMap<>();

    public GroupAvailabilityPanel(StudyGroup group) {
        this.group = group;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        renderAvailabilityTable();
    }

    private void renderAvailabilityTable() {
        Set<LocalDate> candidateDates = group.getSchedule().getCandidateDates();
        LocalTime start = group.getSchedule().getStartTime();
        LocalTime end = group.getSchedule().getEndTime();

        if (candidateDates == null || start == null || end == null) {
            JLabel emptyLabel = new JLabel("일정이 아직 설정되지 않았습니다.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
            emptyLabel.setForeground(new Color(108, 117, 125));
            add(emptyLabel, BorderLayout.CENTER);
            return;
        }

        List<LocalDate> sortedDates = new ArrayList<>(candidateDates);
        sortedDates.sort(LocalDate::compareTo);

        JPanel gridPanel = new JPanel(new GridLayout(0, sortedDates.size() + 1));
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 헤더 행
        JLabel headerLabel = createCellLabel("시간/날짜", true);
        gridPanel.add(headerLabel);

        // 날짜 헤더
        for (LocalDate date : sortedDates) {
            JLabel dateLabel = createCellLabel(TimeSlotUtils.formatDate(date), false);
            gridPanel.add(dateLabel);
        }

        int memberCount = group.getMembers().size();
        Map<TimeSlot, Integer> freq = getSlotFrequency();
        Set<TimeSlot> confirmedSlots = group.getSchedule().getConfirmedTimeSlots();

        // 시간대 행
        for (LocalTime time = start; time.isBefore(end); time = time.plusMinutes(30)) {
            JLabel timeLabel = createCellLabel(TimeSlotUtils.formatTime(time), false);
            gridPanel.add(timeLabel);

            for (LocalDate date : sortedDates) {
                String dayKor = TimeSlotUtils.getDayKor(date.getDayOfWeek());
                String timeStr = time + "-" + time.plusMinutes(30);
                TimeSlot slot = new TimeSlot(dayKor, timeStr);

                JPanel cellPanel = new JPanel(new BorderLayout());
                cellPanel.setBorder(BorderFactory.createLineBorder(new Color(221, 230, 237)));
                cellPanel.setBackground(Color.WHITE);

                if (confirmedSlots.contains(slot)) {
                    // 확정된 시간: 진한 파란색
                    cellPanel.setBackground(new Color(70, 130, 255));

                    JLabel confirmLabel = new JLabel("확정", SwingConstants.CENTER); // ✅ 이 줄 삭제
                    confirmLabel.setForeground(Color.WHITE);                       // ✅ 삭제
                    confirmLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));     // ✅ 삭제
                    cellPanel.add(confirmLabel);
                } else {
                    int availability = freq.getOrDefault(slot, 0);
                    if (availability > 0) {
                        float ratio = (float) availability / memberCount;
                        cellPanel.setBackground(calculatePurpleColor(ratio));
                        JLabel ratioLabel = new JLabel(availability + "/" + memberCount, SwingConstants.CENTER);
                        ratioLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
                        ratioLabel.setForeground(ratio > 0.5f ? Color.WHITE : new Color(108, 46, 181));
                        cellPanel.add(ratioLabel);
                    }
                }

                gridPanel.add(cellPanel);
            }
        }

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JLabel createCellLabel(String text, boolean isHeader) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("맑은 고딕", isHeader ? Font.BOLD : Font.PLAIN, 13));
        label.setForeground(new Color(52, 58, 64));
        label.setBorder(BorderFactory.createLineBorder(new Color(221, 230, 237)));
        label.setBackground(isHeader ? new Color(248, 249, 250) : Color.WHITE);
        label.setOpaque(true);
        return label;
    }

    private Color calculatePurpleColor(float ratio) {
        if (ratio <= 0.33f) return new Color(225, 213, 246); // 연보라
        if (ratio <= 0.66f) return new Color(191, 162, 229); // 중간 보라
        return new Color(143, 95, 215); // 진한 보라
    }

    private Map<TimeSlot, Integer> getSlotFrequency() {
        Map<TimeSlot, Integer> freq = new HashMap<>();
        for (StudyMember member : group.getMembers()) {
            for (TimeSlot slot : group.getSchedule().getAvailabilityOf(member)) {
                freq.put(slot, freq.getOrDefault(slot, 0) + 1);
            }
        }
        return freq;
    }
}
