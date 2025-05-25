package studit.service;

import studit.domain.StudyGroup;
import studit.domain.User;
import studit.domain.Profile;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 사용자의 관심 태그와 일치하는 스터디 그룹을 추천하는 클래스
 */
public class StudyRecommender {

    /**
     * 사용자 관심 태그 기반으로 스터디 그룹 추천
     *
     * @param user 추천 대상 사용자
     * @param allGroups 전체 스터디 그룹 목록
     * @return 추천된 스터디 그룹 목록
     */
    public List<StudyGroup> recommendByTags(User user, List<StudyGroup> allGroups) {
        Profile profile = user.getProfile();
        if (profile == null) return List.of();
        Set<String> interests = profile.getInterests();

        return allGroups.stream()
                .filter(group -> !group.isFull()) // 정원 초과 제외
                .filter(group -> group.getTags().stream()
                        .anyMatch(interests::contains))
                .collect(Collectors.toList());
    }
}
