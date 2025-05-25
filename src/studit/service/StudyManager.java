package studit.service;

import studit.domain.StudyGroup;
import studit.domain.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 전체 스터디 그룹을 생성, 관리하는 클래스
 */
public class StudyManager {
    private List<StudyGroup> allGroups;

    public StudyManager() {
        this.allGroups = new ArrayList<>();
    }

    public void addStudyGroup(StudyGroup group) {
        allGroups.add(group);
    }

    public List<StudyGroup> getAllGroups() {
        return allGroups;
    }

    public boolean removeGroup(StudyGroup group) {
        return allGroups.remove(group);
    }

    public void clearAllGroups() {
        allGroups.clear();
    }

    /**
     * 데모용 기본 스터디 그룹 데이터를 초기화한다
     */
    public void initializeDemoData() {
        User leader1 = new User("김지원", "인공지능공학부", "20201111");
        User leader2 = new User("이현우", "데이터사이언스전공", "20201234");

        Set<String> tags1 = new HashSet<>();
        tags1.add("인공지능");
        tags1.add("딥러닝");

        Set<String> tags2 = new HashSet<>();
        tags2.add("프론트엔드");
        tags2.add("React");

        StudyGroup group1 = new StudyGroup("딥러닝 기초", "온라인", tags1, 5, leader1);
        StudyGroup group2 = new StudyGroup("프론트엔드 심화", "오프라인", tags2, 6, leader2);

        addStudyGroup(group1);
        addStudyGroup(group2);
    }
}
