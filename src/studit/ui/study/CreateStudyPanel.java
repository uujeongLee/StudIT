package studit.ui.study;

import studit.domain.StudyGroup;
import studit.domain.TimeSlot;
import studit.domain.User;
import studit.service.StudyManager;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 새로운 스터디 그룹을 생성할 수 있는 입력/설정 패널 클래스입니다.
 * - 과목명, 정원, 방식(온라인/오프라인), 관심 태그, 설명, 가능 시간대 등 모든 정보를 입력받아 스터디를 생성합니다.
 * - 시간표(가능 시간대)는 클릭으로 선택/해제할 수 있으며, 태그도 다중 선택 가능합니다.
 * - "생성" 버튼 클릭 시 StudyManager를 통해 실제 스터디 그룹이 만들어지고 콜백으로 알립니다.
 */
public class CreateStudyPanel extends JPanel {
    private JTextField subjectField;
    private JComboBox<Integer> maxSizeCombo;
    private JToggleButton onlineButton;
    private JToggleButton offlineButton;
    private ButtonGroup modeGroup;
    private JTextArea descriptionArea;
    private JPanel tagAreaPanel;
    private Set<String> selectedTags = new LinkedHashSet<>();
    private Map<TimeSlot, JToggleButton> timeslotButtons = new LinkedHashMap<>();
    private JButton createButton;
    private final StudyManager studyManager;
    private final User currentUser;
    private final Consumer<StudyGroup> onCreateCallback;

    private static final String[][] TAG_LINES = {
            {"#자바", "#파이썬", "#C", "#C++", "#자바스크립트"},
            {"#프로그래밍", "#객체", "#오픈소스", "#소프트웨어"},
            {"#컴퓨터공학", "#운영체제", "#공학수학", "#알고리즘"},
            {"#데이터베이스", "#클라우드", "#데이터", "#데이터분석", "#패턴인식"},
            {"#인공지능", "#기계학습", "#머신러닝", "#딥러닝"},
            {"#시각화", "#영상처리", "#컴퓨터비전", "#인터페이스"},
            {"#프론트엔드", "#백엔드"}
    };

    public CreateStudyPanel(StudyManager studyManager, User currentUser, Consumer<StudyGroup> onCreateCallback) {
        this.studyManager = studyManager;
        this.currentUser = currentUser;
        this.onCreateCallback = onCreateCallback;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel leftPanel = buildLeftPanel();
        JPanel rightPanel = buildRightPanel();

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);

        JPanel emojiTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        emojiTitlePanel.setOpaque(false);

        JLabel emojiLabel = new JLabel("\uD83D\uDCBB");
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        emojiLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JLabel textLabel = new JLabel(" 스터디 개설하기");
        textLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        textLabel.setForeground(new Color(99, 102, 241));
        textLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0));

        emojiTitlePanel.add(emojiLabel);
        emojiTitlePanel.add(textLabel);

        panel.add(emojiTitlePanel);
        panel.add(Box.createVerticalStrut(40));

        JPanel labelRow = new JPanel(new GridLayout(1, 3, 0, 0));
        labelRow.setOpaque(false);

        JLabel subjectLabel = new JLabel("  스터디 과목");
        subjectLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        subjectLabel.setForeground(new Color(51, 51, 51));
        subjectLabel.setHorizontalAlignment(SwingConstants.LEFT);
        labelRow.add(subjectLabel);

        JLabel sizeLabel = new JLabel("   정원");
        sizeLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        sizeLabel.setForeground(new Color(51, 51, 51));
        sizeLabel.setHorizontalAlignment(SwingConstants.LEFT);
        labelRow.add(sizeLabel);

        JLabel typeLabel = new JLabel(" 종류");
        typeLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        typeLabel.setForeground(new Color(51, 51, 51));
        typeLabel.setHorizontalAlignment(SwingConstants.LEFT);
        labelRow.add(typeLabel);

        labelRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        panel.add(labelRow);
        panel.add(Box.createVerticalStrut(4));

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        infoPanel.setOpaque(false);

        subjectField = new JTextField(7);
        subjectField.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        subjectField.setBorder(new CompoundBorder(
                new LineBorder(new Color(220,220,220), 1),
                new EmptyBorder(4, 6, 4, 6)
        ));
        subjectField.setPreferredSize(new Dimension(100, 24));
        infoPanel.add(subjectField);

        infoPanel.add(Box.createHorizontalStrut(30));
        Integer[] sizes = {2,3,4,5,6,7,8,9,10};
        maxSizeCombo = new JComboBox<>(sizes);
        maxSizeCombo.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        maxSizeCombo.setPreferredSize(new Dimension(60, 24));
        infoPanel.add(maxSizeCombo);

        infoPanel.add(Box.createHorizontalStrut(30));
        onlineButton = new JToggleButton("Online");
        onlineButton.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        onlineButton.setFocusPainted(false);
        onlineButton.setBackground(new Color(180, 190, 255));
        onlineButton.setBorder(new LineBorder(new Color(180, 190, 255), 1));
        onlineButton.setPreferredSize(new Dimension(60, 22));
        onlineButton.setSelected(true);
        offlineButton = new JToggleButton("Offline");
        offlineButton.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        offlineButton.setFocusPainted(false);
        offlineButton.setBackground(Color.WHITE);
        offlineButton.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        offlineButton.setPreferredSize(new Dimension(60, 22));
        modeGroup = new ButtonGroup();
        modeGroup.add(onlineButton);
        modeGroup.add(offlineButton);
        onlineButton.addActionListener(e -> updateModeButtons());
        offlineButton.addActionListener(e -> updateModeButtons());
        infoPanel.add(onlineButton);
        infoPanel.add(offlineButton);

        panel.add(infoPanel);
        panel.add(Box.createVerticalStrut(18));

        JPanel interestPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        interestPanel.setOpaque(false);
        JLabel interestLabel = new JLabel("관심 분야");
        interestLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        interestLabel.setForeground(new Color(51, 51, 51));
        interestLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        interestPanel.add(interestLabel);
        panel.add(interestPanel);
        panel.add(Box.createVerticalStrut(4));

        createTagAreaPanel();
        panel.add(tagAreaPanel);
        panel.add(Box.createVerticalStrut(20));

        JPanel descPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        descPanel.setOpaque(false);
        JLabel descLabel = new JLabel("설명");
        descLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        descLabel.setForeground(new Color(51, 51, 51));
        descLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        descPanel.add(descLabel);
        panel.add(descPanel);
        panel.add(Box.createVerticalStrut(4));

        panel.add(buildDescriptionPanel());
        panel.add(Box.createVerticalStrut(20));

        createButton = new JButton("생성");
        createButton.setPreferredSize(new Dimension(140, 38));
        createButton.setBackground(new Color(66, 133, 244));
        createButton.setForeground(Color.WHITE);
        createButton.setFont(new Font("맑은 고딕", Font.BOLD, 17));
        createButton.setFocusPainted(false);
        createButton.setBorder(null);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(createButton);
        panel.add(btnPanel);

        createButton.addActionListener(e -> handleCreate());

        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 180, 0));
        timePanel.setOpaque(false);
        JLabel timeLabel = new JLabel("                     스터디 가능 시간대", SwingConstants.CENTER);
        timeLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        timeLabel.setForeground(new Color(51, 51, 51));
        timePanel.add(timeLabel);
        panel.add(timePanel);
        panel.add(Box.createVerticalStrut(6));

        JPanel gridContainer = new JPanel(new GridBagLayout());
        gridContainer.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gridContainer.add(buildTimeSlotGrid(), gbc);
        panel.add(gridContainer);

        return panel;
    }

    private JPanel buildDescriptionPanel() {
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setOpaque(false);
        descriptionArea = new JTextArea(1, 18);
        descriptionArea.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(new CompoundBorder(
                new LineBorder(new Color(220,220,220), 1),
                new EmptyBorder(4, 4, 4, 4)
        ));
        JScrollPane scroll = new JScrollPane(descriptionArea);
        scroll.setPreferredSize(new Dimension(200, 26));
        descPanel.add(scroll, BorderLayout.CENTER);
        return descPanel;
    }

    private void updateModeButtons() {
        if (onlineButton.isSelected()) {
            onlineButton.setBackground(new Color(180, 190, 255));
            onlineButton.setForeground(Color.BLACK);
            offlineButton.setBackground(Color.WHITE);
            offlineButton.setForeground(Color.BLACK);
        } else {
            offlineButton.setBackground(new Color(180, 190, 255));
            offlineButton.setForeground(Color.BLACK);
            onlineButton.setBackground(Color.WHITE);
            onlineButton.setForeground(Color.BLACK);
        }
    }

    private void createTagAreaPanel() {
        tagAreaPanel = new JPanel();
        tagAreaPanel.setLayout(new BoxLayout(tagAreaPanel, BoxLayout.Y_AXIS));
        tagAreaPanel.setBackground(Color.WHITE);

        for (String[] line : TAG_LINES) {
            JPanel linePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 0));
            linePanel.setOpaque(false);
            linePanel.setBorder(new EmptyBorder(4, 0, 4, 0));
            for (String tag : line) {
                JToggleButton tagBtn = new JToggleButton(tag);
                tagBtn.setFocusPainted(false);
                tagBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
                tagBtn.setBorder(new LineBorder(new Color(200, 200, 255)));
                tagBtn.setBackground(Color.WHITE);
                tagBtn.setForeground(new Color(55, 110, 180));
                tagBtn.setPreferredSize(new Dimension(70, 20));
                tagBtn.setMargin(new Insets(0, 0, 0, 0));
                tagBtn.addItemListener(e -> {
                    String tagText = tagBtn.getText().replace("#", "");
                    if (tagBtn.isSelected()) {
                        tagBtn.setBackground(new Color(180, 190, 255));
                        tagBtn.setForeground(Color.BLACK);
                        selectedTags.add(tagText);
                    } else {
                        tagBtn.setBackground(Color.WHITE);
                        tagBtn.setForeground(new Color(55, 110, 180));
                        selectedTags.remove(tagText);
                    }
                });
                linePanel.add(tagBtn);
            }

            tagAreaPanel.add(linePanel);
        }
    }

    private JPanel buildTimeSlotGrid() {
        String[] days = {"M", "T", "W", "T", "F", "S", "S"};
        String[] times = {"10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00"};
        JPanel gridPanel = new JPanel(new GridLayout(times.length + 1, days.length + 1, 2, 2));
        gridPanel.setBackground(Color.WHITE);

        gridPanel.add(new JLabel(""));
        for (String day : days) {
            JLabel lbl = new JLabel(day, SwingConstants.CENTER);
            lbl.setFont(new Font("맑은 고딕", Font.BOLD, 12));
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            gridPanel.add(lbl);
        }

        for (int t = 0; t < times.length; t++) {
            JLabel timeLabel = new JLabel(times[t], SwingConstants.CENTER);
            timeLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
            timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
            gridPanel.add(timeLabel);

            for (int d = 0; d < days.length; d++) {
                TimeSlot slot = new TimeSlot(days[d], times[t]);
                JToggleButton btn = new JToggleButton();
                btn.setPreferredSize(new Dimension(70, 32));
                btn.setBackground(new Color(248, 249, 250));
                btn.setFocusPainted(false);
                btn.setBorder(new LineBorder(new Color(220, 220, 220), 1));
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.addChangeListener(e -> {
                    if (btn.isSelected()) {
                        btn.setBackground(new Color(173, 216, 230));
                        btn.setBorder(new LineBorder(new Color(99, 102, 241), 2));
                    } else {
                        btn.setBackground(new Color(248, 249, 250));
                        btn.setBorder(new LineBorder(new Color(220, 220, 220), 1));
                    }
                });
                timeslotButtons.put(slot, btn);
                gridPanel.add(btn);
            }
        }
        return gridPanel;
    }

    private void handleCreate() {
        String subject = subjectField.getText().trim();
        if (subject.isEmpty()) {
            JOptionPane.showMessageDialog(this, "스터디 과목을 입력하세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedTags.isEmpty()) {
            JOptionPane.showMessageDialog(this, "관심 분야를 하나 이상 선택하세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Set<TimeSlot> selectedSlots = timeslotButtons.entrySet().stream()
                .filter(entry -> entry.getValue().isSelected())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        if (selectedSlots.isEmpty()) {
            JOptionPane.showMessageDialog(this, "시간대를 하나 이상 선택하세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StudyGroup newStudyGroup = studyManager.createStudyGroup(
                subject,
                onlineButton.isSelected() ? "온라인" : "오프라인",
                selectedTags,
                (Integer) maxSizeCombo.getSelectedItem(),
                currentUser,
                descriptionArea.getText().trim(),
                selectedSlots
        );

        JOptionPane.showMessageDialog(this, "스터디가 개설되었습니다!", "알림", JOptionPane.INFORMATION_MESSAGE);
        onCreateCallback.accept(newStudyGroup);
    }
}
