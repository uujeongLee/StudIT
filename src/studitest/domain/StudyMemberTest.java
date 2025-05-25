package studitest.domain;

import studit.domain.*;
import java.util.*;

public class StudyMemberTest {
    public static void main(String[] args) {
        // ✅ 1. 사용자 생성
        User user = new User("정예린", "20230010", "데이터사이언스전공");
        user.getProfile().addInterest("데이터 분석");
        user.getProfile().addInterest("시각화");

        // ✅ 2. StudyMember 생성 및 가능 시간 추가
        StudyMember member = new StudyMember(user);
        member.addAvailableTime(new TimeSlot("수", "14:00~16:00"));
        member.addAvailableTime(new TimeSlot("금", "10:00~12:00"));

        // ✅ 3. 정보 출력
        System.out.println("\n✅ [StudyMember 테스트]");
        System.out.println("스터디원 이름: " + member.getUser().getName());
        System.out.println("가능 시간대:");
        for (TimeSlot slot : member.getAvailableTimes()) {
            System.out.println("- " + slot);
        }

        // ✅ 4. 중복 시간 추가 테스트
        member.addAvailableTime(new TimeSlot("수", "14:00~16:00"));
        System.out.println("\n✔ 중복 시간 추가 후 시간대:");
        for (TimeSlot slot : member.getAvailableTimes()) {
            System.out.println("- " + slot);
        }
    }
}
