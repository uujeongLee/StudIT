package studit.service;

import studit.domain.*;
import java.util.*;

public class StudyManager {

    // 사용자가 참여한 스터디 목록 필터링
    public List<StudyGroup> getMyStudies(List<StudyGroup> allGroups, User user) {
        List<StudyGroup> result = new ArrayList<>();
        for (StudyGroup group : allGroups) {
            for (StudyMember member : group.getMembers()) {
                if (member.getUser().equals(user)) {
                    result.add(group);
                    break;
                }
            }
        }
        return result;
    }

    // 특정 스터디에서 현재 사용자 자신의 가능한 시간대 가져오기
    public Set<TimeSlot> getMyAvailableTimes(StudyGroup group, User user) {
        for (StudyMember member : group.getMembers()) {
            if (member.getUser().equals(user)) {
                return group.getSchedule().getAvailabilityOf(member);
            }
        }
        return Collections.emptySet();
    }

    // 사용자의 시간대 등록 및 스케줄 반영
    public void updateMyAvailableTimes(StudyGroup group, User user, Set<TimeSlot> newTimes) {
        for (StudyMember member : group.getMembers()) {
            if (member.getUser().equals(user)) {
                group.getSchedule().registerAvailability(member, newTimes);
                break;
            }
        }
    }

    // 리더만 실행할 수 있는 추천 시간 계산
    public Set<TimeSlot> computeOptimalSchedule(StudyGroup group, User user) {
        if (group.getLeader().equals(user)) {
            return group.getSchedule().computeRecommendedTimeSlots();
        }
        return Collections.emptySet();
    }

    // 스터디 참여자 모두가 선택한 전체 시간대 집계 (빈도순으로 정렬)
    public List<Map.Entry<TimeSlot, Integer>> getTimeSlotFrequency(StudyGroup group) {
        Map<TimeSlot, Integer> freq = new HashMap<>();
        for (StudyMember member : group.getMembers()) {
            for (TimeSlot slot : group.getSchedule().getAvailabilityOf(member)) {
                freq.put(slot, freq.getOrDefault(slot, 0) + 1);
            }
        }
        List<Map.Entry<TimeSlot, Integer>> sorted = new ArrayList<>(freq.entrySet());
        sorted.sort((a, b) -> b.getValue() - a.getValue());
        return sorted;
    }

    // 스터디 리더가 수동으로 특정 시간대를 확정할 수 있게 설정
    public void confirmStudyTime(StudyGroup group, User leader, Set<TimeSlot> selected) {
        if (group.getLeader().equals(leader)) {
            group.getSchedule().setConfirmedTimeSlots(selected);
        }
    }

    // 확정된 시간대 반환
    public Set<TimeSlot> getConfirmedTimeSlots(StudyGroup group) {
        return group.getSchedule().getConfirmedTimeSlots();
    }
}
