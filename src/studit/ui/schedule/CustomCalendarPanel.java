package studit.ui.schedule;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;

/**
 * Swing 기반의 커스텀 달력(캘린더) 패널 클래스입니다.
 * - 월별 달력을 표로 그리며, 날짜 셀을 클릭해 여러 날짜를 선택할 수 있습니다.
 * - 좌우 버튼으로 월 이동, 오늘 날짜/선택 날짜 강조, 선택한 날짜 반환 기능을 제공합니다.
 */
public class CustomCalendarPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private Set<LocalDate> selectedDates = new HashSet<>();
    private LocalDate currentMonth;

    public CustomCalendarPanel() {
        this.currentMonth = LocalDate.now().withDayOfMonth(1);
        initCalendar();
    }

    private void initCalendar() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(createHeader(), BorderLayout.NORTH);
        add(createCalendarTable(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JButton prevBtn = new JButton("◀");
        prevBtn.setBackground(Color.WHITE);
        prevBtn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        prevBtn.addActionListener(e -> changeMonth(-1));

        JButton nextBtn = new JButton("▶");
        nextBtn.setBackground(Color.WHITE);
        nextBtn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        nextBtn.addActionListener(e -> changeMonth(1));

        JLabel monthLabel = new JLabel(currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.KOREAN) + " " + currentMonth.getYear());
        monthLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        monthLabel.setHorizontalAlignment(SwingConstants.CENTER);

        header.add(prevBtn, BorderLayout.WEST);
        header.add(monthLabel, BorderLayout.CENTER);
        header.add(nextBtn, BorderLayout.EAST);

        return header;
    }

    private JPanel createCalendarTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] columns = {"S", "M", "T", "W", "T", "F", "S"};
        model = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(44);
        table.setCellSelectionEnabled(true);
        table.setDefaultRenderer(Object.class, new ModernCalendarCellRenderer());
        table.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        table.setShowGrid(false);
        table.setBackground(Color.WHITE);
        refreshCalendar();

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();
                Object value = table.getValueAt(row, col);
                if (value != null) {
                    int day = (int) value;
                    LocalDate date = currentMonth.withDayOfMonth(day);
                    if (selectedDates.contains(date)) selectedDates.remove(date);
                    else selectedDates.add(date);
                    table.repaint();
                }
            }
        });

        panel.add(table, BorderLayout.CENTER);
        return panel;
    }

    private void changeMonth(int delta) {
        currentMonth = currentMonth.plusMonths(delta);
        refreshCalendar();
        updateHeader();
    }

    private void updateHeader() {
        removeAll();
        add(createHeader(), BorderLayout.NORTH);
        add(createCalendarTable(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void refreshCalendar() {
        model.setRowCount(0);
        LocalDate firstDay = currentMonth;
        int lengthOfMonth = currentMonth.lengthOfMonth();
        int startDay = firstDay.getDayOfWeek().getValue() % 7;

        List<Object[]> rows = new ArrayList<>();
        Object[] week = new Object[7];
        int dayCounter = 1;

        for (int i = 0; i < 7; i++) {
            week[i] = (i >= startDay && dayCounter <= lengthOfMonth) ? dayCounter++ : null;
        }
        rows.add(week);

        while (dayCounter <= lengthOfMonth) {
            week = new Object[7];
            for (int i = 0; i < 7 && dayCounter <= lengthOfMonth; i++) week[i] = dayCounter++;
            rows.add(week);
        }

        for (Object[] row : rows) model.addRow(row);
    }

    public LocalDate getSelectedDate() {
        // 선택된 날짜 하나를 반환하거나 기본값 제공
        if (!selectedDates.isEmpty()) {
            return selectedDates.iterator().next(); // 첫 번째 선택 날짜 반환
        }
        return LocalDate.now(); // 기본값
    }

    public Set<LocalDate> getSelectedDates() { return selectedDates; }

    private class ModernCalendarCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(CENTER);
            setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

            if (value != null) {
                int day = (int) value;
                LocalDate date = currentMonth.withDayOfMonth(day);
                LocalDate today = LocalDate.now();

                if (selectedDates.contains(date)) {
                    c.setBackground(new Color(66, 133, 244));
                    c.setForeground(Color.WHITE);
                    setOpaque(true);
                } else if (date.equals(today)) {
                    c.setBackground(new Color(66, 133, 244, 30));
                    c.setForeground(new Color(66, 133, 244));
                    setOpaque(true);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(new Color(60, 60, 60));
                    setOpaque(false);
                }
            } else {
                c.setBackground(Color.WHITE);
                c.setForeground(new Color(200, 200, 200));
                setOpaque(false);
            }
            return c;
        }
    }
}
