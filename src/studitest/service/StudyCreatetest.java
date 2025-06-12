//package studitest.service;
//
//import studit.domain.*;
//import studit.service.*;
//
//import java.util.*;
//
//public class StudyCreatetest {
//    public static void main(String[] args) {
//        // 리더 사용자 생성
//        User leader = new User("이지은", "20231234", "AI공학부");
//
//        // UI에서 받은 정보 시뮬레이션
//        String subject = "딥러닝 기초";
//        String mode = "온라인";
//        Set<String> tags = new HashSet<>(Arrays.asList("딥러닝", "인공지능"));
//        int maxSize = 5;
//
//        // 시간대 선택 시뮬레이션
//        Set<TimeSlot> selectedTimes = new HashSet<>();
//        selectedTimes.add(new TimeSlot("월", "10:00"));
//        selectedTimes.add(new TimeSlot("화", "14:00"));
//
//        // 스터디 생성 서비스 호출
//        StudyCreate creator = new StudyCreate();
//        StudyGroup newGroup = creator.createStudyGroup(subject, mode, tags, maxSize, leader, selectedTimes);
//
//        // 생성 결과 출력
//        creator.printStudySummary(newGroup);
//
//        // 리스트에 넣고 삭제도 테스트
//        List<StudyGroup> allGroups = new ArrayList<>();
//        allGroups.add(newGroup);
//        System.out.println("\n전체 스터디 수: " + allGroups.size());
//
//        creator.cancelStudyGroup(newGroup, allGroups);
//        System.out.println("남은 스터디 수: " + allGroups.size());
//    }
//}
