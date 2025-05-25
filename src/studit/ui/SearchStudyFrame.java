package studit.ui;

import studit.domain.StudyGroup;
import studit.domain.User;
import studit.service.StudySearchEngine;
import studit.service.StudyManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 사용자가 과목명, 요일, 태그 등으로 스터디를 검색하고 신청/취소할 수 있는 화면
 */
public class SearchStudyFrame extends JFrame {
    private JTextField subjectField;
    private JTextField dayField;
    private JTextField tagField;
    private JButton searchButton;
    private JButton viewMyApplicationsButton;
    private JPanel resultPanel;

    private List<StudyGroup> allGroups;
    private StudyManager studyManager;
    private StudySearchEngine searchEngine;
    private User currentUser;

    public SearchStudyFrame(List<StudyGroup> allGroups, StudyManager studyManager, User currentUser) {
        this.allGroups = allGroups;
        this.studyManager = studyManager;
        this.currentUser = currentUser;
        this.searchEngine = new StudySearchEngine();

        setTitle("스터디 검색");
        setSize(750, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        subjectField = new JTextField(15);
        dayField = new JTextField(5);
        tagField = new JTextField(10);
        searchButton = new JButton("검색하기");
        viewMyApplicationsButton = new JButton("신청 내역 보기");

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("과목:"));
        inputPanel.add(subjectField);
        inputPanel.add(new JLabel("요일:"));
        inputPanel.add(dayField);
        inputPanel.add(new JLabel("태그:"));
        inputPanel.add(tagField);
        inputPanel.add(searchButton);
        inputPanel.add(viewMyApplicationsButton);

        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(resultPanel);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(e -> performSearch());
        viewMyApplicationsButton.addActionListener(e -> displayMyApplications());
    }

    private void performSearch() {
        String subject = subjectField.getText();
        String day = dayField.getText();
        String tag = tagField.getText();

        List<StudyGroup> filtered = allGroups;

        if (!subject.isBlank()) {
            filtered = searchEngine.searchBySubject(filtered, subject);
        }
        if (!day.isBlank()) {
            filtered = searchEngine.searchByDay(filtered, day);
        }
        if (!tag.isBlank()) {
            filtered = searchEngine.searchByTag(filtered, tag);
        }

        displayResults(filtered, true);
    }

    private void displayMyApplications() {
        List<StudyGroup> myGroups = studyManager.getMyStudies(allGroups, currentUser);
        displayResults(myGroups, false);
    }

    private void displayResults(List<StudyGroup> groups, boolean isSearchMode) {
        resultPanel.removeAll();
        if (groups.isEmpty()) {
            resultPanel.add(new JLabel("결과가 없습니다."));
        } else {
            for (StudyGroup group : groups) {
                JPanel card = new JPanel(new BorderLayout());
                JTextArea info = new JTextArea(group.toString());
                info.setEditable(false);
                JButton actionBtn;

                if (group.isMember(currentUser)) {
                    actionBtn = new JButton("신청 취소");
                    actionBtn.addActionListener(e -> {
                        boolean removed = group.getMembers().removeIf(m -> m.getUser().getStudentId().equals(currentUser.getStudentId()));
                        JOptionPane.showMessageDialog(this, removed ? "신청이 취소되었습니다." : "신청 내역이 없습니다.");
                        displayResults(groups, isSearchMode);
                    });
                } else {
                    actionBtn = new JButton("신청");
                    actionBtn.addActionListener(e -> {
                        boolean success = group.apply(currentUser);
                        String message = success ? "신청 성공!" : "이미 신청했거나 정원이 초과되었습니다.";
                        JOptionPane.showMessageDialog(this, message);
                        displayResults(groups, isSearchMode);
                    });
                }

                card.add(info, BorderLayout.CENTER);
                card.add(actionBtn, BorderLayout.EAST);
                card.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                resultPanel.add(card);
            }
        }
        resultPanel.revalidate();
        resultPanel.repaint();
    }
}