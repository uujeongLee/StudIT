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

    public StudyGroup(String subject, String mode, Set<String> tags, int maxSize, User leader) {
        this.subject = subject;
        this.mode = mode;
        this.tags = tags;
        this.maxSize = maxSize;
        this.leader = leader;
        this.members = new ArrayList<>();
        this.schedule = new Schedule();
    }

    public boolean apply(User user) {
        if (isFull() || isMember(user)) return false;
        members.add(new StudyMember(user));
        return true;
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

    @Override
    public String toString() {
        return String.format("스터디 과목: %s, 방식: %s, 인원: %d/%d, 태그: %s, 리더: %s",
                subject, mode, members.size(), maxSize, tags, leader.getName());
    }
}
