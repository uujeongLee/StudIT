package studit.ui.side;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * 스터디 그룹 앱의 사이드바(왼쪽 메뉴)를 담당하는 패널 클래스입니다.
 * - 홈 화면, 스터디 개설, 일정 추가, 나의 시간표 등 주요 메뉴를 나열합니다.
 * - 선택된 메뉴는 색상/테두리/텍스트 강조로 표시되며, 메뉴 클릭 시 리스너를 통해 상위 패널에 알립니다.
 */
public class SidebarPanel extends JPanel {
    public interface MenuSelectionListener {
        void onMenuSelected(int index);
    }

    private List<JPanel> menuItems;
    private List<JLabel> menuLabels;
    private int selectedIndex = 0;
    private MenuSelectionListener listener;

    private static final Color SELECTED_BG = new Color(238, 242, 255);
    private static final Color NORMAL_BG = new Color(245, 246, 250);
    private static final Color SELECTED_BORDER = new Color(99, 102, 241);
    private static final Color SELECTED_TEXT = new Color(99, 102, 241);
    private static final Color NORMAL_TEXT = new Color(75, 85, 99);

    public SidebarPanel() {
        setBackground(new Color(249, 250, 251));
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(220, 0));
        setBorder(new EmptyBorder(20, 0, 20, 0));

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setOpaque(false);
        JLabel logo = new JLabel("STUDIT");
        logo.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        logo.setForeground(new Color(99, 102, 241));
        logoPanel.add(logo);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.add(Box.createVerticalStrut(40));

        menuItems = new ArrayList<>();
        menuLabels = new ArrayList<>();
        String[] menuTexts = {"홈 화면", "스터디 개설", "일정 추가", "나의 시간표"};

        for (int i = 0; i < menuTexts.length; i++) {
            JPanel menuItem = createMenuItem(menuTexts[i], i);
            menuItems.add(menuItem);
            menuPanel.add(menuItem);
        }

        updateSelection(0);
        add(logoPanel, BorderLayout.NORTH);
        add(menuPanel, BorderLayout.CENTER);
    }

    private JPanel createMenuItem(String text, int index) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setMaximumSize(new Dimension(200, 45));
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(text);
        label.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        panel.add(label);
        menuLabels.add(label);

        MouseAdapter clickListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectMenuItem(index);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                if (selectedIndex != index) panel.setBackground(new Color(240, 241, 243));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (selectedIndex != index) panel.setBackground(NORMAL_BG);
            }
        };

        panel.addMouseListener(clickListener);
        label.addMouseListener(clickListener);
        return panel;
    }

    public void selectMenuItem(int index) {
        if (index >= 0 && index < menuItems.size()) {
            selectedIndex = index;
            updateSelection(index);
            if (listener != null) listener.onMenuSelected(index);
        }
    }

    private void updateSelection(int selectedIndex) {
        for (int i = 0; i < menuItems.size(); i++) {
            JPanel panel = menuItems.get(i);
            JLabel label = menuLabels.get(i);
            if (i == selectedIndex) {
                panel.setBackground(SELECTED_BG);
                panel.setBorder(new LineBorder(SELECTED_BORDER, 1));
                label.setForeground(SELECTED_TEXT);
            } else {
                panel.setBackground(NORMAL_BG);
                panel.setBorder(null);
                label.setForeground(NORMAL_TEXT);
            }
        }
        repaint();
    }

    public void setMenuSelectionListener(MenuSelectionListener listener) {
        this.listener = listener;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }
}
