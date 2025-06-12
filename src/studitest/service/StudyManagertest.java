//package studitest.service;
//
//import studit.domain.*;
//import studit.service.*;
//
//import java.util.*;
//
//public class StudyManagertest {
//    public static void main(String[] args) {
//        // ✅ 사용자 생성
//        User leader = new User("이채원", "20230001", "AI공학부");
//        User member1 = new User("김태환", "20230002", "컴퓨터공학");
//        User member2 = new User("박유정", "20230003", "데이터사이언스");
//
//        // ✅ 스터디 생성
//        Set<String> tags = new HashSet<>(Arrays.asList("딥러닝", "AI"));
//        StudyGroup group = new StudyGroup("AI 스터디", "온라인", tags, 5, leader);
//
//        group.apply(member1);
//        group.apply(member2);
//
//        // ✅ StudyManager 생성
//        StudyManager manager = new StudyManager();
//
//        // ✅ 사용자별 시간대 등록
//        Set<TimeSlot> t1 = new HashSet<>();
//        t1.add(new TimeSlot("월", "10:00"));
//        t1.add(new TimeSlot("화", "14:00"));
//        manager.updateMyAvailableTimes(group, member1, t1);
//
//        Set<TimeSlot> t2 = new HashSet<>();
//        t2.add(new TimeSlot("월", "10:00"));
//        t2.add(new TimeSlot("수", "15:00"));
//        manager.updateMyAvailableTimes(group, member2, t2);
//
//        // ✅ 각자 시간 확인
//        System.out.println("\n🧑‍💻 " + member1.getName() + "의 가능 시간:");
//        for (TimeSlot slot : manager.getMyAvailableTimes(group, member1)) {
//            System.out.println("- " + slot);
//        }
//
//        System.out.println("\n🧑‍💻 " + member2.getName() + "의 가능 시간:");
//        for (TimeSlot slot : manager.getMyAvailableTimes(group, member2)) {
//            System.out.println("- " + slot);
//        }
//
//        // ✅ 추천 시간대 확인 (최다 인원 기반)
//        System.out.println("\n📊 추천 시간대 (최다 인원 기준):");
//        for (Map.Entry<TimeSlot, Integer> entry : manager.getTimeSlotFrequency(group)) {
//            System.out.println("- " + entry.getKey() + " (" + entry.getValue() + "명)");
//        }
//
//        // ✅ 리더가 수동으로 시간 확정
//        Set<TimeSlot> confirmed = new HashSet<>();
//        confirmed.add(new TimeSlot("월", "10:00"));
//        manager.confirmStudyTime(group, leader, confirmed);
//
//        // ✅ 확정 시간 확인
//        System.out.println("\n✅ 최종 확정된 시간대:");
//        for (TimeSlot slot : manager.getConfirmedTimeSlots(group)) {
//            System.out.println("- " + slot);
//        }
//    }
//}