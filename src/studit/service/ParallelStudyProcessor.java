package studit.service;

import studit.domain.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 추천 + 검색 기능을 병렬로 실행하는 통합 프로세서
 */
public class ParallelStudyProcessor {

    private final StudyRecommender recommender = new StudyRecommender();
    private final StudySearchEngine searchEngine = new StudySearchEngine();

    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    /**
     * 병렬로 추천 및 검색을 수행하고 결과를 출력
     */
    public void runParallelSearch(User user, List<StudyGroup> allGroups, String subjectKeyword, String tagKeyword, String dayKeyword) {
        try {
            // 병렬 작업 정의
            Future<List<StudyGroup>> recommendedFuture = executor.submit(() ->
                    recommender.recommendByTags(user, allGroups));

            Future<List<StudyGroup>> subjectFuture = executor.submit(() ->
                    searchEngine.searchBySubject(allGroups, subjectKeyword));

            Future<List<StudyGroup>> tagFuture = executor.submit(() ->
                    searchEngine.searchByTag(allGroups, tagKeyword));

            Future<List<StudyGroup>> dayFuture = executor.submit(() ->
                    searchEngine.searchByDay(allGroups, dayKeyword));

            // 결과 수집
            List<StudyGroup> recommended = recommendedFuture.get();
            List<StudyGroup> subjectMatches = subjectFuture.get();
            List<StudyGroup> tagMatches = tagFuture.get();
            List<StudyGroup> dayMatches = dayFuture.get();

            // 결과 출력
            System.out.println("추천 스터디 (" + recommended.size() + "개):");
            printGroups(recommended);

            System.out.println("\n과목명 검색 결과 (" + subjectMatches.size() + "개):");
            printGroups(subjectMatches);

            System.out.println("\n태그 검색 결과 (" + tagMatches.size() + "개):");
            printGroups(tagMatches);

            System.out.println("\n요일 검색 결과 (" + dayMatches.size() + "개):");
            printGroups(dayMatches);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    private void printGroups(List<StudyGroup> groups) {
        for (StudyGroup g : groups) {
            System.out.println("- " + g.getSubject() + " [" + g.getMode() + "] " + g.getTags());
        }
    }
}
