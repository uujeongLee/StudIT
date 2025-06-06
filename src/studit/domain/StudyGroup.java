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

    // 🔒 Thread-safe를 위한 락과 대기열
    private final Queue<User> waitlist = new LinkedList<>();
    private final Object lock = new Object();

    public StudyGroup(String subject, String mode, Set<String> tags, int maxSize, User leader) {
        this.subject = subject;
        this.mode = mode;
        this.tags = tags;
        this.maxSize = maxSize;
        this.leader = leader;
        this.members = new ArrayList<>();
        this.schedule = new Schedule();
    }

    // ✅ Thread-safe 스터디 신청 메서드
    public boolean apply(User user) {
        synchronized (lock) {
            if (isMember(user)) return false;

            if (members.size() < maxSize) {
                members.add(new StudyMember(user));
                System.out.println("✅ 스터디 가입 완료: " + user.getName());
                return true;
            } else {
                waitlist.add(user);
                System.out.println("📋 대기열에 추가됨: " + user.getName());
                return false;
            }
        }
    }

    // ❗ 정원 이탈 시 대기자 승격 메서드 (옵션)
    public void promoteFromWaitlist() {
        synchronized (lock) {
            while (!isFull() && !waitlist.isEmpty()) {
                User next = waitlist.poll();
                members.add(new StudyMember(next));
                System.out.println("⏫ 대기자 승격됨: " + next.getName());
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

    @Override
    public String toString() {
        return String.format("스터디 과목: %s, 방식: %s, 인원: %d/%d, 태그: %s, 리더: %s",
                subject, mode, members.size(), maxSize, tags, leader.getName());
    }
}
