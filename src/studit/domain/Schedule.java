package studit.domain;

import java.io.Serializable;
import java.util.*;

public class Schedule implements Serializable {
    private Map<StudyMember, Set<TimeSlot>> memberAvailability;
    private Set<TimeSlot> confirmedTimeSlots;

    public Schedule() {
        this.memberAvailability = new HashMap<>();
        this.confirmedTimeSlots = new HashSet<>();
    }

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

    @Override
    public String toString() {
        return "등록된 멤버 수: " + memberAvailability.size() +
                ", 확정 시간대: " + confirmedTimeSlots;
    }
}
