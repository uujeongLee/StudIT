package studit;


import studit.domain.*;
import studit.service.*;
import studit.ui.home.HomePanel;
import studit.ui.login.LoginPanel;
import studit.ui.schedule.CreateSchedulePanel;
import studit.ui.myTimetable.SchedulePanel;
import studit.ui.side.SidebarPanel;
import studit.ui.study.CreateStudyPanel;
import javax.swing.*;
import java.awt.*;
import java.util.Set;
import java.util.function.Consumer;

public class Main extends JFrame implements SidebarPanel.MenuSelectionListener{
    private final Login loginService;
    private final StudyManager studyManager;
    private final StudyRecommender recommender;
    private final StudySearchEngine searchEngine;
    private JPanel centerContent;
    private CardLayout cardLayout;
    private CreateStudyPanel createStudyPanel;
    private CreateSchedulePanel createSchedulePanel;
    private User currentUser;
    private HomePanel homePanel;
    private SchedulePanel schedulePanel;

    public Main(Login loginService) {
        this.loginService = loginService;
        this.studyManager = new StudyManager();
        this.recommender = new StudyRecommender();
        this.searchEngine = new StudySearchEngine();
        initializeTestData();
        initUI();
    }

    private void initializeTestData() {
        loginService.register("눈송이", "20231234", "IT공학전공", "studit@gmail.com", "password");
        User snowUser = loginService.getUserByEmail("studit@gmail.com");
        if (snowUser != null) snowUser.getProfile().getInterests().addAll(Set.of("자바", "객체", "백엔드"));

        studyManager.createStudyGroup(
                "자바 스터디",
                "온라인",
                Set.of("프로그래밍", "자바", "객체지향"),
                5,
                new User("김순헌", "20231111", "컴퓨터공학"),
                "자바 기초 스터디 그룹",
                Set.of(
                        new TimeSlot("월", "10:00"),
                        new TimeSlot("월", "11:00"),
                        new TimeSlot("수", "10:00"),
                        new TimeSlot("수", "11:00")
                )
        );

        // 알고리즘 스터디: 정원 5명, 멤버 4명(리더+3)
        StudyGroup algoGroup = studyManager.createStudyGroup(
                "알고리즘 스터디",
                "오프라인",
                Set.of("컴퓨터과학", "코딩테스트"),
                5, // 정원 5명
                new User("박진리", "20232222", "소프트웨어"),
                "알고리즘 문제 풀이",
                Set.of(
                        new TimeSlot("화", "14:00"),
                        new TimeSlot("화", "15:00"),
                        new TimeSlot("목", "14:00"),
                        new TimeSlot("목", "15:00")
                )
        );
        // 멤버 3명 추가
        algoGroup.apply(new User("멤버A", "20232223", "소프트웨어"));
        algoGroup.apply(new User("멤버B", "20232224", "소프트웨어"));
        algoGroup.apply(new User("멤버C", "20232225", "소프트웨어"));

        // 백엔드 스터디: 정원 4명, 멤버 4명(리더+3)
        StudyGroup backendGroup = studyManager.createStudyGroup(
                "백엔드 스터디",
                "온라인",
                Set.of("백엔드", "서버", "자바"),
                4, // 정원 4명
                new User("이새힘", "20233333", "인공지능공학"),
                "백엔드 개발 심화 학습",
                Set.of(
                        new TimeSlot("금", "13:00"),
                        new TimeSlot("금", "14:00")
                )
        );
        // 멤버 3명 추가
        backendGroup.apply(new User("멤버D", "20233334", "인공지능공학"));
        backendGroup.apply(new User("멤버E", "20233335", "인공지능공학"));
        backendGroup.apply(new User("멤버F", "20233336", "인공지능공학"));
    }


    private void initUI() {
        setTitle("STUDIT 메인");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1200, 800));
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);

        SidebarPanel sidebar = new SidebarPanel();
        sidebar.setMenuSelectionListener(this);
        add(sidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        centerContent = new JPanel(cardLayout);

        Consumer<User> onLoginSuccess = this::showHomeScreen;
        LoginPanel loginPanel = new LoginPanel(loginService, onLoginSuccess);
        centerContent.add(loginPanel, "login");
        add(centerContent, BorderLayout.CENTER);

        cardLayout.show(centerContent, "login");
    }

    @Override
    public void onMenuSelected(int index) {
        switch (index) {
            case 0:
                cardLayout.show(centerContent, "home");
                // 홈 화면 전환 시 데이터 갱신
                if (homePanel != null) {
                    SwingUtilities.invokeLater(() -> {
                        homePanel.refreshData();
                    });
                }
                break;
            case 1:
                cardLayout.show(centerContent, "create_study");
                break;
            case 2:
                cardLayout.show(centerContent, "create_schedule");
                // 일정 추가 화면 진입 시 항상 최신 데이터로 갱신
                if (createSchedulePanel != null) {
                    SwingUtilities.invokeLater(() -> {
                        createSchedulePanel.refreshStudyComboBox();
                    });
                }
                break;
            case 3: // 나의 시간표
                cardLayout.show(centerContent, "schedule");
                if (schedulePanel != null) {
                    schedulePanel.refreshStudyComboBox(); // ✅ 콤보박스 갱신
                    schedulePanel.refreshData();
                }
                break;
        }
    }

    private void showHomeScreen(User user) {
        this.currentUser = user;
        centerContent.removeAll();

        // HomePanel
        homePanel = new HomePanel(
                user,
                studyManager,
                recommender,
                searchEngine,
                this::handleStudyJoined
        );
        centerContent.add(homePanel, "home");

        // CreateStudyPanel
        centerContent.add(
                new CreateStudyPanel(
                        studyManager,
                        user,
                        this::handleStudyCreated
                ),
                "create_study"
        );

        // CreateSchedulePanel
        if (createSchedulePanel == null) {
            createSchedulePanel = new CreateSchedulePanel(
                    loginService,
                    studyManager
            );
        }
        centerContent.add(createSchedulePanel, "create_schedule");

        // **SchedulePanel 추가 (여기가 중요!)**
        schedulePanel = new SchedulePanel(
                studyManager.getAllStudyGroups(), // 모든 스터디 그룹 리스트
                loginService,
                studyManager
        );
        centerContent.add(schedulePanel, "schedule");

        cardLayout.show(centerContent, "home");

        SwingUtilities.invokeLater(() -> {
            validate();
            repaint();
            setVisible(true);
            if (createSchedulePanel != null) {
                createSchedulePanel.validate();
                createSchedulePanel.repaint();
            }
        });
    }

    private void refreshAllPanels() {
        SwingUtilities.invokeLater(() -> {
            // HomePanel 데이터 갱신
            if (homePanel != null) {
                homePanel.refreshData();
            }

            // CreateSchedulePanel 갱신
            if (createSchedulePanel != null) {
                createSchedulePanel.refreshStudyComboBox();
            }

            // 전체 UI 강제 갱신
            centerContent.revalidate();
            centerContent.repaint();
            validate();
            repaint();
        });
    }

    private void handleStudyCreated(StudyGroup newGroup) {
        JOptionPane.showMessageDialog(this, newGroup.getSubject() + " 스터디 생성 완료!");

        // 갱신 후 홈 화면으로 이동
        refreshAllPanels();

        SwingUtilities.invokeLater(() -> {
            cardLayout.show(centerContent, "home");
            if (homePanel != null) {
                homePanel.refreshData();
            }
            if (createSchedulePanel != null) {
                createSchedulePanel.refreshStudyComboBox();
            }
        });
    }

    private void handleStudyJoined(StudyGroup joinedGroup) {
        // 먼저 성공 메시지 표시
        JOptionPane.showMessageDialog(this, joinedGroup.getSubject() + " 스터디 가입 완료!");

        // 즉시 모든 패널 갱신
        refreshAllPanels();

        // 추가 보장 갱신
        SwingUtilities.invokeLater(() -> {
            if (createSchedulePanel != null) {
                createSchedulePanel.refreshStudyComboBox();
            }
            if (homePanel != null) {
                homePanel.refreshData();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Login loginService = new Login();
            Main frame = new Main(loginService);
            frame.pack();
            frame.setVisible(true);
        });
    }
}
