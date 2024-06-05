package com.example.bowbackend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import com.example.bowbackend.constant.Constant;
import com.example.bowbackend.dto.BowResultDTO;
import com.example.bowbackend.dto.ThreadMetricsDTO;

public class BowConcurrentTwoService {

    private static final ConcurrentHashMap<String, Long> threadNameToExecutionTimesMap = new ConcurrentHashMap<>();

    public static BowResultDTO executeBowConcurrentTwo(String[] words) {
        threadNameToExecutionTimesMap.clear();
        int processors = Runtime.getRuntime().availableProcessors();
        int chunkSize = (words.length + processors - 1) / processors;

        Map<String, Integer> finalBagOfWords = new ConcurrentHashMap<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < words.length; i += chunkSize) {
            int start = i;
            int end = Math.min(i + chunkSize, words.length);

            CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> processChunk(words, start, end))
                    .thenAccept((Map<String, Integer> partialResult) -> partialResult
                            .forEach((word, count) -> finalBagOfWords.merge(word, count, Integer::sum)));
            futures.add(future);
        }

        long startTime = System.currentTimeMillis();
        CompletableFuture<Void> allDone = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allDone.join();

        long endTime = System.currentTimeMillis();
        List<ThreadMetricsDTO> threadMetricsDTOList = new ArrayList<>();
        for (String threadName : threadNameToExecutionTimesMap.keySet()) {
            long timeInMs = threadNameToExecutionTimesMap.get(threadName);
            threadMetricsDTOList.add(
                    new ThreadMetricsDTO(threadName, timeInMs)
            );
        }
        long combinedTime = endTime - startTime;
        return BowResultDTO.builder()
                .strategyType(Constant.CONCURRENT_TWO)
                .executionTimeInMs(combinedTime)
                .bagOfWords(finalBagOfWords)
                .threadMetricsDTOList(threadMetricsDTOList)
                .build();
    }

    private static Map<String, Integer> processChunk(String[] chunk, int start, int end) {
        long startTime = System.currentTimeMillis();
        Map<String, Integer> frequencyMap = new HashMap<>();
        for (int i = start; i < end; i++) {
            frequencyMap.merge(chunk[i], 1, Integer::sum);
        }
        long endTime = System.currentTimeMillis();
        String threadName = Long.toString(Thread.currentThread().getId());
        long elapsedTime = endTime - startTime;
        threadNameToExecutionTimesMap.merge(threadName, elapsedTime, Long::sum);
        return frequencyMap;
    }
}
