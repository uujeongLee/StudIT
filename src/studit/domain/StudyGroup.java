// StudyGroup.java
package studit.domain;

import java.io.Serializable;
import java.util.*;

public class StudyGroup implements Serializable {
    private String subject;
    private String mode;
    private Set<String> tags;
    private User leader;
    private int maxSize;
    private List<StudyMember> members;
    private Schedule schedule;
    private String description;
    private Set<TimeSlot> timeSlots;
    private final Queue<User> waitlist = new LinkedList<>();
    private final Object lock = new Object();

    public StudyGroup(
            String subject, String mode, Set<String> tags, int maxSize,
            User leader, String description, Set<TimeSlot> timeSlots) {
        this.subject = subject;
        this.mode = mode;
        this.tags = tags;
        this.maxSize = maxSize;
        this.leader = leader;
        this.members = new ArrayList<>();
        this.description = description;
        this.timeSlots = timeSlots;

        // TimeSlot 정보를 포함한 Schedule 생성
        this.schedule = new Schedule(timeSlots);

        // 리더를 자동으로 멤버로 추가
        this.members.add(new StudyMember(leader));
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public void addMember(User user) {
        this.members.add(new StudyMember(user));
    }

    public boolean isInWaitlist(User user) {
        return waitlist.stream()
                .anyMatch(u -> u.getStudentId().equals(user.getStudentId()));
    }

    public boolean apply(User user) {
        synchronized (lock) {
            if (isMember(user) || isInWaitlist(user)) {
                return false;
            }
            if (members.size() < maxSize) {
                members.add(new StudyMember(user));
                return true;
            } else {
                waitlist.add(user);
                return false;
            }
        }
    }

    public void promoteFromWaitlist() {
        synchronized (lock) {
            while (!isFull() && !waitlist.isEmpty()) {
                User next = waitlist.poll();
                members.add(new StudyMember(next));
            }
        }
    }

    public boolean isMember(User user) {
        return members.stream()
                .anyMatch(m -> m.getUser().getStudentId().equals(user.getStudentId()));
    }

    public boolean isFull() {
        return members.size() >= maxSize;
    }

    public String getSubject() {
        return subject;
    }

    public String getMode() {
        return mode;
    }

    public Set<String> getTags() {
        return tags;
    }

    public User getLeader() {
        return leader;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public List<StudyMember> getMembers() {
        return members;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public Queue<User> getWaitlist() {
        return waitlist;
    }

    public String getDescription() { return description; }

    public Set<TimeSlot> getTimeSlots() { return timeSlots; }

    @Override
    public String toString() {
        return subject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudyGroup that = (StudyGroup) o;
        return Objects.equals(subject, that.subject)
                && Objects.equals(mode, that.mode)
                && Objects.equals(tags, that.tags)
                && Objects.equals(leader != null ? leader.getStudentId() : null, that.leader != null ? that.leader.getStudentId() : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, mode, tags, leader != null ? leader.getStudentId() : null);
    }
}
