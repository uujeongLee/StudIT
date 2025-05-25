package studit;

import studit.domain.*;
import studit.service.StudyRecommender;
import studit.service.StudySearchEngine;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        // 리더 생성
        User leader = new User("김리더", "20201111", "인공지능공학부");

        // 스터디 그룹 생성
        StudyGroup group1 = new StudyGroup("AI 스터디", "온라인", Set.of("인공지능", "딥러닝"), 5, leader);
        group1.getSchedule().addConfirmedTimeSlot(new TimeSlot("월", "10:00~12:00"));

        StudyGroup group2 = new StudyGroup("자료구조", "오프라인", Set.of("CS", "자료구조"), 5, leader);
        group2.getSchedule().addConfirmedTimeSlot(new TimeSlot("화", "13:00~15:00"));

        StudyGroup group3 = new StudyGroup("웹 개발", "온라인", Set.of("프론트엔드", "React"), 2, leader);
        group3.getSchedule().addConfirmedTimeSlot(new TimeSlot("수", "14:00~16:00"));
        group3.apply(new User("유저1", "20201112", "소프트웨어"));

        List<StudyGroup> allGroups = List.of(group1, group2, group3);

        // 검색 테스트
        StudySearchEngine searchEngine = new StudySearchEngine();

        System.out.println("▶ 과목명 검색(AI):");
        List<StudyGroup> subjectResult = searchEngine.searchBySubject(allGroups, "AI");
        subjectResult.forEach(g -> System.out.println(" - " + g.getSubject()));

        System.out.println("\n▶ 요일 검색(월):");
        List<StudyGroup> dayResult = searchEngine.searchByDay(allGroups, "월");
        dayResult.forEach(g -> System.out.println(" - " + g.getSubject()));

        System.out.println("\n▶ 태그 검색(딥러닝):");
        List<StudyGroup> tagResult = searchEngine.searchByTag(allGroups, "딥러닝");
        tagResult.forEach(g -> System.out.println(" - " + g.getSubject()));

        System.out.println("\n▶ 방식 검색(오프라인):");
        List<StudyGroup> modeResult = searchEngine.searchByMode(allGroups, "오프라인");
        modeResult.forEach(g -> System.out.println(" - " + g.getSubject()));

        // 추천 테스트
        User currentUser = new User("이유정", "20201113", "AI공학부");
        Profile profile = new Profile();
        profile.addInterest("딥러닝");
        profile.addInterest("React");
        currentUser.setProfile(profile);

        StudyRecommender recommender = new StudyRecommender();
        List<StudyGroup> recommendations = recommender.recommendByTags(currentUser, allGroups);

        System.out.println("\n🎯 관심 태그 기반 추천 결과:");
        recommendations.forEach(group -> System.out.println(" - " + group.getSubject()));
    }
}
