package studit.ui.schedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 시간대, 날짜, 요일 등 시간 관련 문자열 변환을 위한 유틸리티 클래스입니다.
 * - 요일을 한글/영문으로 변환, 날짜와 시간을 포맷팅하는 기능을 제공합니다.
 * - 시간표, 일정표, UI 날짜/시간 표시 등에 재사용할 수 있습니다.
 */
public class TimeSlotUtils {

    // 기존 요일 관련 메서드
    public static String getDayKor(DayOfWeek day) {
        return switch (day) {
            case MONDAY    -> "월";
            case TUESDAY   -> "화";
            case WEDNESDAY -> "수";
            case THURSDAY  -> "목";
            case FRIDAY    -> "금";
            case SATURDAY  -> "토";
            case SUNDAY    -> "일";
        };
    }

    public static String getDayEng(DayOfWeek day) {
        return day.toString().substring(0, 1) +
                day.toString().substring(1).toLowerCase();
    }

    // 날짜를 "MM/dd (E)" 형식의 문자열로 반환
    public static String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd (E)");
        return date.format(formatter);
    }

    // 시간을 "HH:mm" 형식의 문자열로 반환
    public static String formatTime(LocalTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(formatter);
    }
}
