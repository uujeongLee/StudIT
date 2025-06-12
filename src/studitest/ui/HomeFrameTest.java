//package studitest.ui;
//
//import studit.domain.*;
//import studit.service.*;
//import studit.ui.home.HomePanel;
//import javax.swing.*;
//import java.util.*;
//
//public class HomeFrameTest {
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            User user = new User("눈송이", "20201234", "IT공학전공");
//            user.getProfile().addInterest("자바");
//            user.getProfile().addInterest("데이터");
//
//            List<StudyGroup> allGroups = new ArrayList<>();
//
//            User leader1 = new User("김순헌", "20211234", "컴퓨터과학전공");
//            StudyGroup group1 = new StudyGroup(
//                    "자바 프로그래밍", "온라인",
//                    new HashSet<>(Arrays.asList("자바", "코딩")), 4, leader1
//            );
//            group1.getSchedule().setConfirmedTimeSlots(Set.of(
//                    new TimeSlot("월", "10:00-12:00"),
//                    new TimeSlot("수", "14:00-16:00")
//            ));
//
//            User member1 = new User("김철수", "20211111", "컴퓨터과학전공");
//            User member2 = new User("이영희", "20212222", "소프트웨어전공");
//            User member3 = new User("박민수", "20213333", "인공지능공학전공");
//            User member4 = new User("최서현", "20214444", "인공지능공학전공");
//            group1.apply(member1);
//            group1.apply(member2);
//            group1.apply(member3);
//            group1.apply(member4);
//            allGroups.add(group1);
//
//            User leader2 = new User("최새힘", "20221234", "데이터사이언스전공");
//            StudyGroup group2 = new StudyGroup(
//                    "파이썬 데이터 분석", "오프라인",
//                    new HashSet<>(Arrays.asList("파이썬", "데이터")), 5, leader2
//            );
//            group2.getSchedule().setConfirmedTimeSlots(Set.of(
//                    new TimeSlot("화", "13:00-15:00"),
//                    new TimeSlot("목", "16:00-18:00")
//            ));
//            allGroups.add(group2);
//
//            User leader3 = new User("이진리", "20231234", "IT공학전공");
//            StudyGroup group3 = new StudyGroup(
//                    "C언어 기초", "온라인",
//                    new HashSet<>(Arrays.asList("C", "프로그래밍", "기초")), 2, leader3
//            );
//            group3.getSchedule().setConfirmedTimeSlots(Set.of(
//                    new TimeSlot("금", "09:00-11:00")
//            ));
//            allGroups.add(group3);
//
//            StudyRecommender recommender = new StudyRecommender();
//            StudySearchEngine searchEngine = new StudySearchEngine();
//            StudyManager studyManager = new StudyManager();
//
//            HomePanel frame = new HomePanel(user, allGroups, recommender, searchEngine, studyManager);
//            frame.setVisible(true);
//        });
//    }
//}
