package studit.ui.home;

import studit.domain.*;
import studit.service.StudyManager;
import studit.service.StudyRecommender;
import studit.service.StudySearchEngine;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * 홈 화면의 메인 패널을 담당하는 클래스입니다.
 * - 사용자별 스터디 목록, 추천, 검색, 가입 등 주요 스터디 그룹 UI를 모두 관리합니다.
 * - 헤더(검색/필터/정렬/프로필)와 스터디 목록(StudyListPanel)을 포함합니다.
 * - 스터디 가입, 데이터 갱신, 사용자 정보 접근 등의 메서드를 제공합니다.
 * - StudyManager, StudyRecommender, StudySearchEngine 등 서비스와 직접 연동됩니다.
 */
public class HomePanel extends JPanel {
    private final User user;
    private final StudyManager studyManager; // StudyManager 직접 참조
    private final StudyRecommender recommender;
    private final StudySearchEngine searchEngine;
    private StudyListPanel studyListPanel;
    private final Consumer<StudyGroup> onStudyJoined;

    // 기존 생성자 (하위 호환성 유지)
    public HomePanel(User user, List<StudyGroup> studyGroups,
                     StudyRecommender recommender, StudySearchEngine searchEngine,
                     StudyManager studyManager) {
        this(user, studyGroups, recommender, searchEngine, studyManager, null);
    }

    // 기존 생성자 (하위 호환성 유지)
    public HomePanel(User user, List<StudyGroup> studyGroups,
                     StudyRecommender recommender, StudySearchEngine searchEngine,
                     StudyManager studyManager, Consumer<StudyGroup> onStudyJoined) {
        this.user = user;
        this.studyManager = studyManager;
        this.recommender = recommender;
        this.searchEngine = searchEngine;
        this.onStudyJoined = onStudyJoined;
        initComponents();
    }

    // 새로운 생성자 (권장 방식)
    public HomePanel(User user, StudyManager studyManager,
                     StudyRecommender recommender, StudySearchEngine searchEngine,
                     Consumer<StudyGroup> onStudyJoined) {
        this.user = user;
        this.studyManager = studyManager;
        this.recommender = recommender;
        this.searchEngine = searchEngine;
        this.onStudyJoined = onStudyJoined;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel rightPanel = new JPanel(new BorderLayout());
        HeaderPanel headerPanel = new HeaderPanel(user);

        // StudyListPanel에 StudyManager 직접 전달하여 실시간 데이터 사용
        studyListPanel = new StudyListPanel(user, studyManager, recommender, searchEngine);

        // 스터디 가입 콜백 연결
        studyListPanel.setOnStudyJoined(this::handleJoinStudy);

        // 헤더 패널의 검색 및 필터 기능 연결
        headerPanel.setOnSearch(studyListPanel::filterByKeyword);
        headerPanel.setOnCategorySelected(studyListPanel::filterByTag);
        headerPanel.setOnSortSelected(studyListPanel::sortBy);

        // 헤더를 상단에 배치
        rightPanel.add(headerPanel, BorderLayout.NORTH);

        // 스터디 목록을 스크롤 가능한 패널로 감싸기
        JScrollPane scrollPane = new JScrollPane(studyListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // 전체 패널을 중앙에 배치
        add(rightPanel, BorderLayout.CENTER);
    }

    /**
     * 데이터 갱신 메서드
     * 스터디 가입/생성 후 UI를 최신 상태로 갱신
     */
    public void refreshData() {
        if (studyListPanel != null) {
            SwingUtilities.invokeLater(() -> {
                studyListPanel.refreshData();
                revalidate();
                repaint();
            });
        }
    }

    /**
     * 스터디 가입 처리 메서드
     * @param studyGroup 가입할 스터디 그룹
     */
    private void handleJoinStudy(StudyGroup studyGroup) {
        if (studyGroup == null || user == null) {
            JOptionPane.showMessageDialog(this,
                    "스터디 가입 중 오류가 발생했습니다.",
                    "오류",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean success = studyManager.joinStudy(user, studyGroup);

            if (success) {
                // 상위 컴포넌트에 가입 완료 알림
                if (onStudyJoined != null) {
                    onStudyJoined.accept(studyGroup);
                }
            } else {
                // 가입 실패 처리
                String errorMessage;
                if (studyGroup.isMember(user)) {
                    errorMessage = "이미 가입된 스터디입니다.";
                } else if (studyGroup.getWaitlist().contains(user)) {
                    errorMessage = "이미 대기열에 등록되어 있습니다.";
                } else {
                    errorMessage = "스터디 가입에 실패했습니다.";
                }

                JOptionPane.showMessageDialog(this,
                        errorMessage,
                        "가입 실패",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "스터디 가입 중 오류가 발생했습니다: " + e.getMessage(),
                    "시스템 오류",
                    JOptionPane.ERROR_MESSAGE);

            System.err.println("Study join error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 현재 사용자 반환
     * @return 현재 로그인된 사용자
     */
    public User getCurrentUser() {
        return user;
    }

    /**
     * StudyManager 반환
     * @return StudyManager 인스턴스
     */
    public StudyManager getStudyManager() {
        return studyManager;
    }

    /**
     * StudyListPanel 반환
     * @return StudyListPanel 인스턴스
     */
    public StudyListPanel getStudyListPanel() {
        return studyListPanel;
    }
}
