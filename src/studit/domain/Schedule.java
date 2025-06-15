package studit.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Schedule implements Serializable {
    private Map<StudyMember, Set<TimeSlot>> memberAvailability;
    private Set<TimeSlot> confirmedTimeSlots;
    // Schedule.java 상단 클래스 내부에 추가
    private final Map<StudyMember, Set<TimeSlot>> availabilityMap = new HashMap<>();

    // 기본 생성자
    public Schedule() {
        this.memberAvailability = new HashMap<>();
        this.confirmedTimeSlots = new HashSet<>();
    }
    public Schedule(Set<TimeSlot> initialTimeSlots) {
        this.memberAvailability = new HashMap<>();
        this.confirmedTimeSlots = new HashSet<>();
        if (initialTimeSlots != null && !initialTimeSlots.isEmpty()) {
            this.confirmedTimeSlots.addAll(initialTimeSlots);
        }
    }

    public Set<TimeSlot> getAvailabilityOf(User user) {
        for (StudyMember member : availabilityMap.keySet()) {
            if (member.getUser().equals(user)) {
                return availabilityMap.get(member);
            }
        }
        return Collections.emptySet();
    }
    // 스케줄 확정 상태를 확인하는 메서드 추가
    public boolean isConfirmed() {
        return !confirmedTimeSlots.isEmpty();
    }
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    // 기존 메서드들...
    public void registerAvailability(StudyMember member, Set<TimeSlot> availableTimes) {
        memberAvailability.put(member, availableTimes);
    }

    public Set<TimeSlot> computeRecommendedTimeSlots() {
        Map<TimeSlot, Integer> timeCount = new HashMap<>();
        for (Set<TimeSlot> times : memberAvailability.values()) {
            for (TimeSlot slot : times) {
                timeCount.put(slot, timeCount.getOrDefault(slot, 0) + 1);
            }
        }
        int maxCount = timeCount.values().stream().max(Integer::compareTo).orElse(0);
        Set<TimeSlot> result = new HashSet<>();
        for (Map.Entry<TimeSlot, Integer> entry : timeCount.entrySet()) {
            if (entry.getValue() == maxCount) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    public void setConfirmedTimeSlots(Set<TimeSlot> slots) {
        this.confirmedTimeSlots = slots;
    }

    public Set<TimeSlot> getConfirmedTimeSlots() {
        return confirmedTimeSlots;
    }

    public void addConfirmedTimeSlot(TimeSlot slot) {
        this.confirmedTimeSlots.add(slot);
    }

    public Set<StudyMember> getMembers() {
        return memberAvailability.keySet();
    }

    public Set<TimeSlot> getAvailabilityOf(StudyMember member) {
        return memberAvailability.getOrDefault(member, new HashSet<>());
    }

    public void setAvailability(StudyMember member, Set<TimeSlot> slots) {
        availabilityMap.put(member, slots);
        memberAvailability.put(member, slots);
    }

    public void confirmTimeSlot(TimeSlot slot) {
        confirmedTimeSlots.add(slot);
    }

    private Set<LocalDate> candidateDates = new HashSet<>();

    public void setCandidateDates(Set<LocalDate> dates) {
        this.candidateDates = dates;
    }

    public Set<LocalDate> getCandidateDates() {
        return candidateDates;
    }
    // Schedule.java
    private LocalTime startTime;
    private LocalTime endTime;

    public void setTimeRange(LocalTime start, LocalTime end) {
        this.startTime = start;
        this.endTime = end;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }


    @Override
    public String toString() {
        return "등록된 멤버 수: " + memberAvailability.size() +
                ", 확정 시간대: " + confirmedTimeSlots;
    }
}
