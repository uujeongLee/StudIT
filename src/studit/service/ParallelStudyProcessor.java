// ParallelStudyProcessor.java
package studit.service;

import studit.domain.*;
import java.util.*;
import java.util.concurrent.*;

public class ParallelStudyProcessor {
    public static record ParallelResults(
            List<StudyGroup> recommended,
            List<StudyGroup> subjectMatches,
            List<StudyGroup> tagMatches,
            List<StudyGroup> dayMatches
    ) {}

    private final StudyRecommender recommender;
    private final StudySearchEngine searchEngine;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public ParallelStudyProcessor(StudyRecommender recommender, StudySearchEngine searchEngine) {
        this.recommender = recommender;
        this.searchEngine = searchEngine;
    }

    public ParallelResults runParallelSearch(User user, List<StudyGroup> allGroups,
                                             String subjectKeyword, String tagKeyword,
                                             String dayKeyword) {
        try {
            Future<List<StudyGroup>> recommendedFuture = executor.submit(() ->
                    recommender.recommendByTags(user, allGroups));

            Future<List<StudyGroup>> subjectFuture = executor.submit(() ->
                    searchEngine.searchBySubject(allGroups, subjectKeyword));

            Future<List<StudyGroup>> tagFuture = executor.submit(() ->
                    searchEngine.searchByTag(allGroups, tagKeyword));

            Future<List<StudyGroup>> dayFuture = executor.submit(() ->
                    searchEngine.searchByDay(allGroups, dayKeyword));

            return new ParallelResults(
                    recommendedFuture.get(),
                    subjectFuture.get(),
                    tagFuture.get(),
                    dayFuture.get()
            );

        } catch (InterruptedException | ExecutionException e) {
            return new ParallelResults(List.of(), List.of(), List.of(), List.of());
        }
    }
}
