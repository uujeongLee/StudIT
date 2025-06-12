package studitest.domain;

import studit.domain.*;
import java.util.*;

public class ScheduleTest {
    public static void main(String[] args) {
        // ✅ 사용자 및 멤버 생성
        User u1 = new User("김지우", "20230001", "AI공학부");
        User u2 = new User("박민수", "20230002", "컴퓨터공학전공");

        StudyMember m1 = new StudyMember(u1);
        m1.addAvailableTime(new TimeSlot("월", "10:00~12:00"));
        m1.addAvailableTime(new TimeSlot("수", "14:00~16:00"));

        StudyMember m2 = new StudyMember(u2);
        m2.addAvailableTime(new TimeSlot("수", "14:00~16:00"));
        m2.addAvailableTime(new TimeSlot("금", "10:00~12:00"));

        // ✅ schedule 생성 및 등록
        Schedule schedule = new Schedule();
        schedule.registerAvailability(m1, m1.getAvailableTimes());
        schedule.registerAvailability(m2, m2.getAvailableTimes());

        // ✅ 추천 시간대 계산
        Set<TimeSlot> recommended = schedule.computeRecommendedTimeSlots();

        // ✅ 출력
        System.out.println("\n✅ [schedule 추천 시간대 테스트]");
        System.out.println("스터디원 수: " + schedule.getMembers().size());

        System.out.println("\n📅 추천 시간대:");
        for (TimeSlot slot : recommended) {
            System.out.println("- " + slot);
        }

        System.out.println("\n🧾 확정 시간대 설정 테스트:");
        schedule.setConfirmedTimeSlots(recommended);
        System.out.println("확정된 시간대: " + schedule.getConfirmedTimeSlots());
    }
}
