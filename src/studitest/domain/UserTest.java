package studitest.domain;

import studit.domain.*;
import java.util.*;

public class UserTest {
    public static void main(String[] args) {
        // ✅ 1. 사용자 생성 및 정보 출력
        User user1 = new User("김지원", "20230001", "인공지능공학부");
        User user2 = new User("김지원", "20230001", "인공지능공학부"); // 동일 학번
        User user3 = new User("최민수", "20230002", "컴퓨터과학전공");

        user1.getProfile().addInterest("머신러닝");
        user1.getProfile().addInterest("딥러닝");
        user3.getProfile().addInterest("데이터 분석");

        System.out.println("\n✅ [User 정보 출력]");
        System.out.println(user1);
        System.out.println(user3);

        // ✅ 2. equals() 및 hashCode() 비교
        System.out.println("\n✔ equals/hashCode 비교:");
        System.out.println("user1.equals(user2)? " + user1.getStudentId().equals(user2.getStudentId())); // true
        System.out.println("user1.equals(user3)? " + user1.getStudentId().equals(user3.getStudentId())); // false

        // ✅ 3. StudyGroup에 참여시키기
        Set<String> tags = new HashSet<>(Arrays.asList("AI", "머신러닝"));
        StudyGroup group = new StudyGroup("AI 스터디", "온라인", tags, 5, user1);

        group.apply(user3);
        group.apply(user2); // 학번이 같기 때문에 중복 판단 가능

        System.out.println("\n✅ [스터디 그룹 정보]");
        System.out.println(group);

        System.out.println("\n👥 [스터디 참여 멤버 목록]");
        for (StudyMember m : group.getMembers()) {
            System.out.println("- " + m.getUser().getName() + " (" + m.getUser().getStudentId() + ")");
        }
    }
}
