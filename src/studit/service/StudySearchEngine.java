package studit.service;

import studit.domain.StudyGroup;
import studit.domain.TimeSlot;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 스터디 그룹을 과목명, 요일 또는 태그로 검색하는 기능을 제공하는 클래스
 */
public class StudySearchEngine {

    /**
     * 과목명을 기준으로 검색 (대소문자 무시, 포함 관계)
     */
    public List<StudyGroup> searchBySubject(List<StudyGroup> groups, String keyword) {
        if (keyword == null || keyword.isBlank()) return List.of();
        String lower = keyword.toLowerCase();
        return groups.stream()
                .filter(g -> g.getSubject().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    /**
     * 요일(예: "월", "화", ...) 기준으로 확정된 시간대에 포함된 스터디 반환
     */
    public List<StudyGroup> searchByDay(List<StudyGroup> groups, String day) {
        if (day == null || day.isBlank()) return List.of();
        return groups.stream()
                .filter(g -> g.getSchedule().getConfirmedTimeSlots().stream()
                        .anyMatch(slot -> slot.getDay().equalsIgnoreCase(day)))
                .collect(Collectors.toList());
    }

    /**
     * 태그를 포함한 스터디 그룹 검색 (하나라도 일치하는 경우)
     */
    public List<StudyGroup> searchByTag(List<StudyGroup> groups, String tag) {
        if (tag == null || tag.isBlank()) return List.of();
        String norm = tag.trim().toLowerCase();
        return groups.stream()
                .filter(g -> g.getTags().stream()
                        .anyMatch(t -> t.toLowerCase().contains(norm)))
                .collect(Collectors.toList());
    }

    /**
     * 스터디 방식 (온라인/오프라인) 기준 필터링
     */
    public List<StudyGroup> searchByMode(List<StudyGroup> groups, String mode) {
        if (mode == null || mode.isBlank()) return List.of();
        return groups.stream()
                .filter(g -> g.getMode().equalsIgnoreCase(mode))
                .collect(Collectors.toList());
    }
}
