package studit.ui.myTimetable;

import studit.domain.*;
import studit.ui.schedule.TimeSlotUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

/**
 * 특정 사용자의 스터디 그룹 내 시간표(가능 시간대)를 표 형태로 보여주는 패널 클래스입니다.
 * - 사용자가 선택한 시간대는 파란색 셀과 체크(✓)로 표시됩니다.
 * - 헤더, 날짜, 시간, 셀 등 UI 스타일과 가독성을 고려한 Swing 컴포넌트로 구현되어 있습니다.
 * - 스터디 멤버가 아닌 경우, 안내 메시지를 표시합니다.
 * - 스터디 그룹 일정 관리, 시간표 UI 등에서 개인별 가능 시간 확인에 사용됩니다.
 */
public class PersonalAvailabilityPanel extends JPanel {

    public PersonalAvailabilityPanel(StudyGroup group, User user) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        Schedule schedule = group.getSchedule();
        Set<LocalDate> dates = schedule.getCandidateDates();
        LocalTime start = schedule.getStartTime();
        LocalTime end = schedule.getEndTime();

        if (dates == null || start == null || end == null) {
            JLabel emptyLabel = new JLabel("후보 날짜/시간대가 아직 설정되지 않았습니다.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
            emptyLabel.setForeground(new Color(108, 117, 125));
            add(emptyLabel, BorderLayout.CENTER);
            return;
        }

        StudyMember me = group.getMembers().stream()
                .filter(m -> m.getUser().equals(user))
                .findFirst()
                .orElse(null);

        if (me == null) {
            JLabel errorLabel = new JLabel(" 해당 유저는 스터디 멤버가 아닙니다.", SwingConstants.CENTER);
            errorLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
            errorLabel.setForeground(new Color(108, 117, 125));
            add(errorLabel, BorderLayout.CENTER);
            return;
        }

        Set<TimeSlot> mySlots = schedule.getAvailabilityOf(me);
        List<LocalDate> sortedDates = new ArrayList<>(dates);
        sortedDates.sort(LocalDate::compareTo);

        JPanel grid = new JPanel(new GridLayout((end.toSecondOfDay() - start.toSecondOfDay()) / 1800 + 1, sortedDates.size() + 1));
        grid.setBackground(Color.WHITE);
        grid.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 헤더 행
        JLabel headerLabel = createCellLabel("시간/날짜", true);
        grid.add(headerLabel);

        // 날짜 헤더
        for (LocalDate date : sortedDates) {
            JLabel dateLabel = createCellLabel(TimeSlotUtils.formatDate(date), false);
            grid.add(dateLabel);
        }

        LocalTime time = start;
        while (!time.isAfter(end.minusMinutes(30))) {
            JLabel timeLabel = createCellLabel(TimeSlotUtils.formatTime(time), false);
            grid.add(timeLabel);

            for (LocalDate date : sortedDates) {
                String dayKor = TimeSlotUtils.getDayKor(date.getDayOfWeek());
                TimeSlot slot = new TimeSlot(dayKor, time + "-" + time.plusMinutes(30));

                JPanel cellPanel = new JPanel(new BorderLayout());
                cellPanel.setBorder(BorderFactory.createLineBorder(new Color(221, 230, 237)));

                if (mySlots.contains(slot)) {
                    cellPanel.setBackground(new Color(70, 130, 255));
                } else {
                    cellPanel.setBackground(Color.WHITE);
                }

                grid.add(cellPanel);
            }
            time = time.plusMinutes(30);
        }

        JScrollPane scrollPane = new JScrollPane(grid);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(221, 230, 237)),
                user.getName() + "님의 시간표",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("맑은 고딕", Font.BOLD, 16),
                new Color(52, 144, 220)
        ));
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
}
