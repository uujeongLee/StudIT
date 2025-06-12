//package studitest.domain;
//
//import studit.domain.*;
//import java.util.*;
//
//public class StudyGroupTest {
//    public static void main(String[] args) {
//        // ✅ 리더 및 스터디 생성
//        User leader = new User("이지은", "20230000", "AI공학부");
//        Set<String> tags = new HashSet<>(Arrays.asList("머신러닝", "딥러닝"));
//
//        StudyGroup group = new StudyGroup("AI 스터디", "오프라인", tags, 4, leader);
//        group.apply(leader); // 리더 자동 등록
//
//        // ✅ 멤버 추가
//        User m1 = new User("김태훈", "20230001", "컴퓨터공학과");
//        User m2 = new User("박유진", "20230002", "데이터사이언스전공");
//        User m3 = new User("정예린", "20230003", "소프트웨어학과");
//
//        group.apply(m1);
//        group.apply(m2);
//        group.apply(m3);
//
//        // ✅ 중복 신청 테스트
//        boolean reapplied = group.apply(m2); // false 기대
//
//        // ✅ 현재 상태 출력
//        System.out.println("\n✅ [스터디 기본 테스트]");
//        System.out.println("스터디 정보: " + group);
//
//        System.out.println("\n👥 참여자 목록:");
//        for (StudyMember sm : group.getMembers()) {
//            System.out.println("- " + sm.getUser().getName() + " (" + sm.getUser().getStudentId() + ")");
//        }
//
//        System.out.println("\n✔ 중복 신청 결과 (박유진): " + reapplied);
//        System.out.println("정원 초과 여부: " + group.isFull());
//
//        // ✅ 멀티스레드 동시 신청 테스트 시작
//        System.out.println("\n🚀 [멀티스레드 동시 신청 테스트]");
//
//        // 지원자 5명 생성 (정원은 이미 4명이라 대기열로 감)
//        List<User> applicants = new ArrayList<>();
//        for (int i = 4; i <= 8; i++) {
//            applicants.add(new User("지원자" + i, "20230" + i, "AI학부"));
//        }
//
//        List<Thread> threads = new ArrayList<>();
//        for (User user : applicants) {
//            Thread t = new Thread(() -> {
//                boolean result = group.apply(user);
//                if (result) {
//                    System.out.println("🎉 " + user.getName() + " 스터디 신청 성공!");
//                } else {
//                    System.out.println("⏳ " + user.getName() + " 대기열 등록됨.");
//                }
//            });
//            threads.add(t);
//        }
//
//        // 모든 쓰레드 실행
//        for (Thread t : threads) t.start();
//        for (Thread t : threads) {
//            try {
//                t.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        // ✅ 최종 결과 출력
//        System.out.println("\n📌 최종 멤버 목록:");
//        for (StudyMember m : group.getMembers()) {
//            System.out.println("- " + m.getUser().getName());
//        }
//
//        System.out.println("\n📋 대기자 목록:");
//        for (User u : group.getWaitlist()) {
//            System.out.println("- " + u.getName());
//        }
//    }
//}
