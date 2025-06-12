//package studitest.service;
//
//import studit.domain.*;
//import studit.service.StudyRecommender;
//
//import java.util.*;
//
//public class StudyRecommenderTest {
//    public static void main(String[] args) {
//        // 리더 생성
//        User leader = new User("김리더", "20201111", "인공지능공학부");
//
//        // 스터디 그룹 생성
//        StudyGroup group1 = new StudyGroup("AI 스터디", "온라인", Set.of("인공지능", "딥러닝"), 5, leader);
//        StudyGroup group2 = new StudyGroup("웹 개발", "온라인", Set.of("프론트엔드", "React"), 2, leader);
//        group2.apply(new User("유저1", "20201112", "소프트웨어"));  // 1명 참여 (정원 2)
//
//        List<StudyGroup> allGroups = List.of(group1, group2);
//
//        // 사용자 생성 및 관심 태그 등록
//        User currentUser = new User("이유정", "20201113", "AI공학부");
//        Profile profile = new Profile();
//        profile.addInterest("딥러닝");
//        profile.addInterest("React");
//        currentUser.setProfile(profile);
//
//        // 추천 실행
//        StudyRecommender recommender = new StudyRecommender();
//        List<StudyGroup> recommendations = recommender.recommendByTags(currentUser, allGroups);
//
//        // 결과 출력
//        System.out.println("🎯 관심 태그 기반 추천 결과:");
//        recommendations.forEach(group -> System.out.println(" - " + group.getSubject()));
//    }
//}
