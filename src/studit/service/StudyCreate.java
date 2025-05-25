package studit.service;

import studit.domain.*;
import java.util.*;

public class StudyCreate {

    // 스터디 생성
    public StudyGroup createStudyGroup(String subject, String mode, Set<String> tags, int maxSize, User leader, Set<TimeSlot> availableTimes) {
        StudyGroup group = new StudyGroup(subject, mode, tags, maxSize, leader);
        group.apply(leader);  // 리더도 멤버로 등록

        // 리더의 가능 시간 등록
        StudyManager manager = new StudyManager();
        manager.updateMyAvailableTimes(group, leader, availableTimes);

        return group;
    }

    // 생성된 스터디에 대한 간단 정보 출력 (디버깅용)
    public void printStudySummary(StudyGroup group) {
        System.out.println("\n✅ 스터디가 성공적으로 생성되었습니다!");
        System.out.println("과목: " + group.getSubject());
        System.out.println("방식: " + group.getMode());
        System.out.println("태그: " + group.getTags());
        System.out.println("정원: " + group.getMaxSize());
        System.out.println("리더: " + group.getLeader().getName());

        System.out.println("가능 시간대 (리더):");
        for (TimeSlot slot : group.getSchedule().getAvailabilityOf(group.getMembers().get(0))) {
            System.out.println("- " + slot);
        }
    }

    // 스터디 삭제 기능 (단순 취소)
    public void cancelStudyGroup(StudyGroup group, List<StudyGroup> allGroups) {
        allGroups.remove(group);
        System.out.println("\n❌ 스터디가 삭제되었습니다: " + group.getSubject());
    }
}
