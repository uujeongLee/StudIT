//package studitest.service;
//
//import studit.domain.*;
//import studit.service.StudySearchEngine;
//
//import java.util.*;
//
//public class StudySearchEngineTest {
//    public static void main(String[] args) {
//        User leader = new User("김리더", "20201111", "인공지능공학부");
//
//        // 스터디 그룹들 생성
//        StudyGroup group1 = new StudyGroup("AI 스터디", "온라인", Set.of("인공지능", "딥러닝"), 5, leader);
//        group1.getSchedule().addConfirmedTimeSlot(new TimeSlot("월", "10:00~12:00"));
//
//        StudyGroup group2 = new StudyGroup("자료구조", "오프라인", Set.of("CS", "자료구조"), 5, leader);
//        group2.getSchedule().addConfirmedTimeSlot(new TimeSlot("화", "13:00~15:00"));
//
//        List<StudyGroup> allGroups = List.of(group1, group2);
//
//        StudySearchEngine searchEngine = new StudySearchEngine();
//
//        System.out.println("▶ 과목명 검색(AI):");
//        searchEngine.searchBySubject(allGroups, "AI")
//                .forEach(g -> System.out.println(" - " + g.getSubject()));
//
//        System.out.println("\n▶ 요일 검색(월):");
//        searchEngine.searchByDay(allGroups, "월")
//                .forEach(g -> System.out.println(" - " + g.getSubject()));
//
//        System.out.println("\n▶ 태그 검색(딥러닝):");
//        searchEngine.searchByTag(allGroups, "딥러닝")
//                .forEach(g -> System.out.println(" - " + g.getSubject()));
//
//        System.out.println("\n▶ 방식 검색(오프라인):");
//        searchEngine.searchByMode(allGroups, "오프라인")
//                .forEach(g -> System.out.println(" - " + g.getSubject()));
//    }
//}
