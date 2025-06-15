package studit.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import studit.domain.*;
import java.util.*;
import java.util.stream.Collectors;

public class StudyManager {

    private final List<StudyGroup> allGroups = new ArrayList<>();

    // StudyManager.java 내부
    private static final Map<String, Set<TimeSlot>> userAvailabilityStore = new HashMap<>();


    public StudyGroup createStudyGroup(String subject, String mode, Set<String> tags, int maxSize, User leader, String description, Set<TimeSlot> timeSlots) {
        StudyGroup group = new StudyGroup(subject, mode, tags, maxSize, leader, description, timeSlots);
        allGroups.add(group);
        return group;
    }

    public List<StudyGroup> getAllStudyGroups() {
        return Collections.unmodifiableList(allGroups);
    }

    public static Map<String, Set<TimeSlot>> getUserAvailabilityStore() {
        return userAvailabilityStore;
    }


    // 사용자가 참여한 스터디 또는 대기중인 스터디 목록 필터링
    public List<StudyGroup> getMyStudies(List<StudyGroup> allGroups, User user) {
        return allGroups.stream()
                .filter(group ->
                        group.isMember(user) ||
                                group.getWaitlist().stream()
                                        .anyMatch(u -> u.getStudentId().equals(user.getStudentId()))
                )
                .collect(Collectors.toList());
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
    public void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("availability.dat"))) {
            oos.writeObject(userAvailabilityStore);
            System.out.println("💾 저장 완료: " + userAvailabilityStore);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 확정된 시간대 반환
    public Set<TimeSlot> getConfirmedTimeSlots(StudyGroup group) {
        return group.getSchedule().getConfirmedTimeSlots();
    }

    public boolean joinStudy(User user, StudyGroup studyGroup) {
        if (user == null || studyGroup == null) {
            return false;
        }

        // 동기화를 통한 데이터 일관성 보장
        synchronized (studyGroup) {
            // 이미 가입된 사용자인지 확인
            if (studyGroup.isMember(user)) {
                return false; // 이미 가입됨
            }

            // 대기열에 있는지 확인
            if (studyGroup.getWaitlist().contains(user)) {
                return false; // 이미 대기열에 있음
            }

            // 정원 초과 확인
            if (studyGroup.getMembers().size() >= studyGroup.getMaxSize()) {
                // 대기열에 추가
                studyGroup.getWaitlist().add(user);
                return true; // 대기열 추가 성공
            }

            // 스터디 멤버로 추가
            StudyMember newMember = new StudyMember(user);
            studyGroup.getMembers().add(newMember);
            return true; // 가입 성공
        }
    }


}
