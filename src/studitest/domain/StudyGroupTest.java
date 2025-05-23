package studitest.domain;

import studit.domain.*;
import java.util.*;

public class StudyGroupTest {
    public static void main(String[] args) {
        // ✅ 리더 및 스터디 생성
        User leader = new User("이지은", "20230000", "AI공학부");
        Set<String> tags = new HashSet<>(Arrays.asList("머신러닝", "딥러닝"));

        StudyGroup group = new StudyGroup("AI 스터디", "오프라인", tags, 4, leader);

        // ✅ 멤버 추가
        User m1 = new User("김태훈", "20230001", "컴퓨터공학과");
        User m2 = new User("박유진", "20230002", "데이터사이언스전공");
        User m3 = new User("정예린", "20230003", "소프트웨어학과");

        group.apply(m1);
        group.apply(m2);
        group.apply(m3);

        // ✅ 중복 신청 테스트
        boolean reapplied = group.apply(m2); // false 기대

        // ✅ 출력
        System.out.println("\n✅ [StudyGroup 테스트 시작]");
        System.out.println("스터디 정보: " + group);

        System.out.println("\n👥 참여자 목록:");
        for (StudyMember sm : group.getMembers()) {
            System.out.println("- " + sm.getUser().getName() + " (" + sm.getUser().getStudentId() + ")");
        }

        System.out.println("\n✔ 중복 신청 결과 (박유진): " + reapplied);
        System.out.println("정원 초과 여부: " + group.isFull());
    }
}
