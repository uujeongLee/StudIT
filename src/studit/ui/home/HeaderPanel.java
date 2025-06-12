package studit.ui.home;

import studit.domain.User;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

/**
 * 헤더 영역(상단바) UI를 담당하는 JPanel 클래스입니다.
 * - 사용자 이름/학과, 프로필 아이콘, 검색창, 카테고리/정렬 버튼, 태그/정렬 드롭다운 UI를 모두 포함합니다.
 * - 검색, 카테고리, 정렬 등의 이벤트를 받을 수 있습니다.
 * - 태그/정렬 버튼 클릭 시 각각의 드롭다운 패널이 토글됩니다.
 * - 프로필 아이콘 클릭 시 사용자 프로필 다이얼로그가 팝업됩니다.
 */
public class HeaderPanel extends JPanel {
    private final User currentUser;
    private JTextField searchField;
    private JPanel tagAreaPanel, sortAreaPanel;
    private Consumer<String> onSearch;
    private Consumer<String> onCategorySelected;
    private Consumer<String> onSortSelected;
    private String selectedTag = null;

    public HeaderPanel(User currentUser) {
        this.currentUser = currentUser;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setBorder(new EmptyBorder(30, 30, 0, 30));

        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.X_AXIS));
        profilePanel.setOpaque(false);

        JLabel iconLabel = new JLabel("👤", SwingConstants.CENTER);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 36));
        iconLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

        JPanel nameDeptPanel = new JPanel();
        nameDeptPanel.setLayout(new BoxLayout(nameDeptPanel, BoxLayout.Y_AXIS));
        nameDeptPanel.setOpaque(false);
        nameDeptPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel(currentUser.getName());
        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        nameLabel.setForeground(new Color(55, 110, 180));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel deptLabel = new JLabel(currentUser.getDepartment());
        deptLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        deptLabel.setForeground(Color.GRAY);
        deptLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        nameDeptPanel.add(nameLabel);
        nameDeptPanel.add(deptLabel);

        profilePanel.setBorder(new EmptyBorder(0, 30, 0, 0));
        profilePanel.add(iconLabel);
        profilePanel.add(Box.createHorizontalStrut(5));
        profilePanel.add(nameDeptPanel);

        profilePanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        profilePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showProfileDialog();
            }
        });

        topRow.add(profilePanel, BorderLayout.EAST);

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setOpaque(false);

        searchField = new JTextField();
        searchField.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        searchField.setPreferredSize(new Dimension(400, 40));
        searchField.setBorder(new CompoundBorder(
                new LineBorder(new Color(209, 213, 219), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JButton categoryBtn = new JButton("Category");
        styleFilterButton(categoryBtn);
        JButton sortBtn = new JButton("Sort By");
        styleFilterButton(sortBtn);

        searchPanel.add(searchField);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(categoryBtn);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(sortBtn);

        topRow.add(searchPanel, BorderLayout.CENTER);

        createTagAreaPanel();
        createSortAreaPanel();

        JPanel belowRow = new JPanel();
        belowRow.setLayout(new BoxLayout(belowRow, BoxLayout.Y_AXIS));
        belowRow.setBackground(Color.WHITE);
        belowRow.add(tagAreaPanel);
        belowRow.add(sortAreaPanel);

        categoryBtn.addActionListener(e -> {
            tagAreaPanel.setVisible(!tagAreaPanel.isVisible());
            sortAreaPanel.setVisible(false);
            belowRow.revalidate();
            belowRow.repaint();
        });
        sortBtn.addActionListener(e -> {
            sortAreaPanel.setVisible(!sortAreaPanel.isVisible());
            tagAreaPanel.setVisible(false);
            belowRow.revalidate();
            belowRow.repaint();
        });

        tagAreaPanel.setVisible(false);
        sortAreaPanel.setVisible(false);

        searchField.addActionListener(e -> {
            if (onSearch != null) onSearch.accept(searchField.getText());
        });

        add(topRow);
        add(belowRow);

        // 태그 버튼 토글 로직 구현
        for (Component comp : tagAreaPanel.getComponents()) {
            if (comp instanceof JPanel) {
                for (Component btnComp : ((JPanel) comp).getComponents()) {
                    if (btnComp instanceof JButton) {
                        JButton tagBtn = (JButton) btnComp;
                        tagBtn.addActionListener(e -> {
                            String tagText = tagBtn.getText().replace("#", "");
                            if (selectedTag != null && selectedTag.equals(tagText)) {
                                // 해제 로직
                                selectedTag = null;
                                tagBtn.setBackground(Color.WHITE);
                                if (onCategorySelected != null) onCategorySelected.accept("");
                            } else {
                                // 선택 로직
                                resetAllTagButtonBackgrounds();
                                selectedTag = tagText;
                                tagBtn.setBackground(new Color(209, 233, 255));
                                if (onCategorySelected != null) onCategorySelected.accept(tagText);
                            }
                        });
                    }
                }
            }
        }

        Component[] sortComponents = sortAreaPanel.getComponents();
        if (sortComponents.length > 0 && sortComponents[0] instanceof JPanel) {
            JPanel sortBtnRow = (JPanel) sortComponents[0];
            for (Component btnComp : sortBtnRow.getComponents()) {
                if (btnComp instanceof JButton) {
                    JButton sortBtn2 = (JButton) btnComp;
                    if (sortBtn2.getText().equals("요일")) {
                        JPopupMenu dayMenu = new JPopupMenu();
                        String[] days = {"전체", "미확정", "월", "화", "수", "목", "금", "토", "일"};
                        for (String day : days) {
                            JMenuItem item = new JMenuItem(day);
                            item.addActionListener(e -> {
                                if (onSortSelected != null) onSortSelected.accept(day);
                            });
                            dayMenu.add(item);
                        }
                        sortBtn2.addActionListener(e -> dayMenu.show(sortBtn2, 0, sortBtn2.getHeight()));
                    } else if (sortBtn2.getText().equals("방식")) {
                        JPopupMenu modeMenu = new JPopupMenu();
                        String[] modes = {"전체", "온라인", "오프라인"};
                        for (String mode : modes) {
                            JMenuItem item = new JMenuItem(mode);
                            item.addActionListener(e -> {
                                if (onSortSelected != null) onSortSelected.accept(mode);
                            });
                            modeMenu.add(item);
                        }
                        sortBtn2.addActionListener(e -> modeMenu.show(sortBtn2, 0, sortBtn2.getHeight()));
                    }
                }
            }
        }
    }

    private void createTagAreaPanel() {
        tagAreaPanel = new JPanel();
        tagAreaPanel.setLayout(new BoxLayout(tagAreaPanel, BoxLayout.Y_AXIS));
        tagAreaPanel.setBackground(Color.WHITE);
        tagAreaPanel.setBorder(new EmptyBorder(0, 23, 0, 0));

        String[][] tagLines = {
                {"#자바", "#파이썬", "#C", "#C++", "#자바스크립트", "#프로그래밍", "#객체", "#오픈소스", "#소프트웨어"},
                {"#컴퓨터공학", "#운영체제", "#공학수학", "#알고리즘", "#데이터베이스", "#클라우드"},
                {"#인공지능", "#기계학습", "#머신러닝", "#딥러닝", "#데이터", "#데이터분석", "#패턴인식"},
                {"#시각화", "#영상처리", "#컴퓨터비전", "#인터페이스", "#프론트엔드", "#백엔드"}
        };

        for (String[] line : tagLines) {
            JPanel linePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
            linePanel.setBackground(Color.WHITE);
            for (String tag : line) {
                JButton tagBtn = new JButton(tag);
                tagBtn.setFocusPainted(false);
                tagBtn.setBackground(Color.WHITE);
                tagBtn.setBorder(new LineBorder(new Color(200, 200, 255)));
                tagBtn.setForeground(new Color(55, 110, 180));
                tagBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
                linePanel.add(tagBtn);
            }
            tagAreaPanel.add(linePanel);
        }
    }

    private void createSortAreaPanel() {
        sortAreaPanel = new JPanel();
        sortAreaPanel.setLayout(new BoxLayout(sortAreaPanel, BoxLayout.Y_AXIS));
        sortAreaPanel.setBackground(Color.WHITE);
        sortAreaPanel.setBorder(new EmptyBorder(0, 23, 0, 0));

        JPanel sortBtnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        sortBtnRow.setBackground(Color.WHITE);

        JButton dayBtn = new JButton("요일");
        styleFilterButton(dayBtn);
        sortBtnRow.add(dayBtn);

        JButton modeBtn = new JButton("방식");
        styleFilterButton(modeBtn);
        sortBtnRow.add(modeBtn);

        sortAreaPanel.add(sortBtnRow);
    }

    private void styleFilterButton(JButton button) {
        button.setBackground(Color.WHITE);
        button.setBorder(new CompoundBorder(
                new LineBorder(new Color(209, 213, 219), 1),
                new EmptyBorder(8, 16, 8, 16)
        ));
        button.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        button.setFocusPainted(false);
    }

    private void showProfileDialog() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JFrame parentFrame = parentWindow instanceof JFrame ? (JFrame) parentWindow : null;
        new ProfileDialog(parentFrame, currentUser).setVisible(true);
    }

    private void resetAllTagButtonBackgrounds() {
        for (Component comp : tagAreaPanel.getComponents()) {
            if (comp instanceof JPanel) {
                for (Component btnComp : ((JPanel) comp).getComponents()) {
                    if (btnComp instanceof JButton) {
                        btnComp.setBackground(Color.WHITE);
                    }
                }
            }
        }
    }

    public void setOnSearch(Consumer<String> onSearch) {
        this.onSearch = onSearch;
    }

    public void setOnCategorySelected(Consumer<String> onCategorySelected) {
        this.onCategorySelected = onCategorySelected;
    }

    public void setOnSortSelected(Consumer<String> onSortSelected) {
        this.onSortSelected = onSortSelected;
    }
}
