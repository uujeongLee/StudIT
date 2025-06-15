package studit.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class StudyMember implements Serializable {
    private User user;
    private Set<TimeSlot> availableTimes;

    public StudyMember(User user) {
        this.user = user;
        this.availableTimes = new HashSet<>();
    }

    public User getUser() {
        return user;
    }

    public void addAvailableTime(TimeSlot timeSlot) {
        availableTimes.add(timeSlot);
    }

    public Set<TimeSlot> getAvailableTimes() {
        return availableTimes;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof StudyMember other)) return false;
        StudyMember that = (StudyMember) obj;
        return user.getStudentId().equals(that.user.getStudentId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user.getStudentId());
    }

    @Override
    public String toString() {
        return "스터디원: " + user.getName() + ", 가능 시간대: " + availableTimes;
    }
}
