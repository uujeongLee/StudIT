package studitest.domain;

import studit.domain.TimeSlot;

public class TimeSlotTest {
    public static void main(String[] args) {
        // 1. TimeSlot 객체 생성
        TimeSlot slot1 = new TimeSlot("월", "10:00~12:00");
        TimeSlot slot2 = new TimeSlot("월", "10:00~12:00");
        TimeSlot slot3 = new TimeSlot("화", "14:00~16:00");

        // 2. toString() 출력
        System.out.println("✅ TimeSlot 테스트 시작\n");
        System.out.println("[slot1] " + slot1); // 월 10:00~12:00
        System.out.println("[slot2] " + slot2); // 월 10:00~12:00
        System.out.println("[slot3] " + slot3); // 화 14:00~16:00

        // 3. equals() 비교
        System.out.println("\n✔ equals 비교");
        System.out.println("slot1.equals(slot2): " + slot1.equals(slot2)); // true
        System.out.println("slot1.equals(slot3): " + slot1.equals(slot3)); // false

        // 4. getDay / getTimeRange 확인
        System.out.println("\n📝 개별 필드 확인");
        System.out.println("slot1.getDay(): " + slot1.getDay());
        System.out.println("slot1.getTimeRange(): " + slot1.getTimeRange());

        System.out.println("\n✅ TimeSlot 테스트 완료");
    }
}
