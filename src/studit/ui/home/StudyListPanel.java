package studit.ui.home;

import studit.domain.*;
import studit.service.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 스터디 그룹 목록(신규, 추천, 나의 스터디)을 카드 형태로 보여주고
 * 검색, 태그, 정렬 등 다양한 필터링/정렬 기능과 스터디 가입 처리를 담당하는 메인 패널 클래스입니다.
 * - 각 섹션(신규/추천/나의 스터디)을 구분하여 동적으로 카드 패널을 생성합니다.
 * - 검색/필터/정렬 결과에 따라 실시간으로 목록을 갱신합니다.
 * - 스터디 가입, 대기열 처리, UI 갱신, 상위 패널 콜백 연동 등 주요 스터디 리스트 로직을 모두 포함합니다.
 */
public class StudyListPanel extends JPanel {
    private final User currentUser;
    private List<StudyGroup> allGroups;
    private final ParallelStudyProcessor parallelProcessor;
    private final StudyManager studyManager;
    private Consumer<StudyGroup> onStudyJoined;

    private JPanel firstSectionContent, recommendedStudiesContent, myStudiesContent;
    private JLabel firstSectionLabel;
    private String currentKeyword = "";
    private String currentTag = "";
    private String currentSort = "";

    public StudyListPanel(User currentUser, StudyManager studyManager,
                          StudyRecommender recommender, StudySearchEngine searchEngine) {
        this.currentUser = currentUser;
        this.studyManager = studyManager;
        this.parallelProcessor = new ParallelStudyProcessor(recommender, searchEngine);

        // 생성 시점에 최신 데이터 로드
        this.allGroups = new ArrayList<>(studyManager.getAllStudyGroups());

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);

        firstSectionContent = createDynamicSectionPanel();
        recommendedStudiesContent = createSectionPanel("추천 스터디그룹");
        myStudiesContent = createSectionPanel("나의 스터디그룹");

        loadStudyData();
    }

    private JPanel createDynamicSectionPanel() {
        JPanel section = new JPanel(new BorderLayout());
        section.setOpaque(false);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setOpaque(false);
        firstSectionLabel = new JLabel("신규 스터디그룹");
        firstSectionLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        firstSectionLabel.setBackground(new Color(66, 133, 244));
        header.add(firstSectionLabel);

        JPanel content = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(0, 30, 0, 30));

        section.add(header, BorderLayout.NORTH);
        section.add(content, BorderLayout.CENTER);
        add(section);
        add(Box.createVerticalStrut(20));

        return content;
    }

    private JPanel createSectionPanel(String title) {
        JPanel section = new JPanel(new BorderLayout());
        section.setOpaque(false);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setOpaque(false);
        JLabel label = new JLabel(title);
        label.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        header.add(label);

        JPanel content = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(0, 30, 0, 30));

        section.add(header, BorderLayout.NORTH);
        section.add(content, BorderLayout.CENTER);
        add(section);
        add(Box.createVerticalStrut(20));

        return content;
    }

    private void loadStudyData() {
        boolean hasSearchCriteria = !currentKeyword.isEmpty() || !currentTag.isEmpty() || !currentSort.isEmpty();
        SwingUtilities.invokeLater(() ->
                firstSectionLabel.setText(hasSearchCriteria ? "검색 결과" : "신규 스터디그룹")
        );

        if (hasSearchCriteria) {
            SwingWorker<ParallelStudyProcessor.ParallelResults, Void> worker =
                    new SwingWorker<>() {
                        @Override
                        protected ParallelStudyProcessor.ParallelResults doInBackground() {
                            if (currentSort.equals("온라인") || currentSort.equals("오프라인")) {
                                List<StudyGroup> filtered = new StudySearchEngine().searchByMode(allGroups, currentSort);
                                List<StudyGroup> recommended = new StudyRecommender().recommendByTags(currentUser, allGroups)
                                        .stream()
                                        .limit(3)
                                        .collect(Collectors.toList());
                                return new ParallelStudyProcessor.ParallelResults(
                                        recommended,
                                        filtered,
                                        Collections.emptyList(),
                                        Collections.emptyList()
                                );
                            } else {
                                return parallelProcessor.runParallelSearch(
                                        currentUser,
                                        allGroups,
                                        currentKeyword,
                                        currentTag,
                                        currentSort
                                );
                            }
                        }

                        @Override
                        protected void done() {
                            try {
                                processSearchResults(get());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
            worker.execute();
        } else {
            loadDefaultData();
        }
    }

    private void processSearchResults(ParallelStudyProcessor.ParallelResults results) {
        List<StudyGroup> searchResults = new ArrayList<>();
        searchResults.addAll(results.subjectMatches());
        searchResults.addAll(results.tagMatches());
        searchResults.addAll(results.dayMatches());

        List<StudyGroup> recommendations = results.recommended().stream()
                .limit(3)
                .collect(Collectors.toList());

        List<StudyGroup> myStudies = studyManager.getMyStudies(allGroups, currentUser);

        SwingUtilities.invokeLater(() -> {
            updatePanel(firstSectionContent, searchResults, false);
            updatePanel(recommendedStudiesContent, recommendations, false);
            updatePanel(myStudiesContent, myStudies, true);
            revalidate();
            repaint();
        });
    }

    private void loadDefaultData() {
        List<StudyGroup> newStudies = allGroups.stream()
                .skip(Math.max(0, allGroups.size() - 5))
                .collect(Collectors.toList());
        Collections.reverse(newStudies);

        List<StudyGroup> recommendedStudies = new StudyRecommender().recommendByTags(currentUser, allGroups)
                .stream()
                .limit(3)
                .collect(Collectors.toList());

        List<StudyGroup> myStudies = studyManager.getMyStudies(allGroups, currentUser);

        SwingUtilities.invokeLater(() -> {
            updatePanel(firstSectionContent, newStudies, false);
            updatePanel(recommendedStudiesContent, recommendedStudies, false);
            updatePanel(myStudiesContent, myStudies, true);
            revalidate();
            repaint();
        });
    }

    private void handleJoinStudy(StudyGroup studyGroup) {
        if (studyGroup == null || currentUser == null) {
            JOptionPane.showMessageDialog(this,
                    "스터디 가입 중 오류가 발생했습니다.",
                    "오류",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // 1. 모든 중복 체크 제거 후 바로 신청 처리
            boolean success = studyManager.joinStudy(currentUser, studyGroup);

            // 2. 결과 메시지 간소화
            String message = success ?
                    "'" + studyGroup.getSubject() + "' 스터디 가입 완료!" :
                    "정원 초과로 대기열에 추가되었습니다.";

            JOptionPane.showMessageDialog(this,
                    message,
                    success ? "가입 성공" : "대기열 추가",
                    success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);

            // 3. 성공 시 추가 액션
            if (success && onStudyJoined != null) {
                onStudyJoined.accept(studyGroup);
            }

            refreshData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "처리 중 오류 발생: " + e.getMessage(),
                    "시스템 오류",
                    JOptionPane.ERROR_MESSAGE);
        }
    }



    private void updatePanel(JPanel content, List<StudyGroup> groups, boolean isMine) {
        content.removeAll();
        for (StudyGroup group : groups) {
            if (isMine) {
                content.add(new MyStudyCardPanel(group, currentUser));
            } else {
                // 기존: content.add(new StudyCardPanel(group, currentUser, this::loadStudyData));
                // 수정: 스터디 가입 콜백으로 변경
                content.add(new StudyCardPanel(group, currentUser, this::handleJoinStudy));
            }
            content.add(Box.createHorizontalStrut(20));
        }
        content.revalidate();
        content.repaint();
    }


    public void filterByKeyword(String keyword) {
        currentKeyword = keyword;
        loadStudyData();
    }

    public void filterByTag(String tag) {
        currentTag = tag.isEmpty() ? "" : tag.equals("전체") ? "" : tag;
        loadStudyData();
    }

    public void sortBy(String sortType) {
        currentSort = sortType.equals("전체") ? "" : sortType;
        loadStudyData();
    }

    public void setOnStudyJoined(Consumer<StudyGroup> onStudyJoined) {
        this.onStudyJoined = onStudyJoined;
    }

    public void refreshData() {
        SwingUtilities.invokeLater(() -> {
            // StudyManager에서 최신 데이터를 가져와서 allGroups 업데이트
            this.allGroups = new ArrayList<>(studyManager.getAllStudyGroups());

            // 데이터 다시 로드
            loadStudyData();

            // UI 강제 갱신
            revalidate();
            repaint();
        });
    }

}
