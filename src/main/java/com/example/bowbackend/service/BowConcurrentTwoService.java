package com.example.bowbackend.service;

import com.example.bowbackend.constant.Constant;
import com.example.bowbackend.dto.BowResultDTO;
import com.example.bowbackend.dto.ThreadMetricsDTO;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class BowConcurrentTwoService {

    private static final ConcurrentHashMap<String, Long> threadNameToExecutionTimesMap = new ConcurrentHashMap<>();

    public static BowResultDTO executeBowConcurrentTwo(String[] words) {
        int processors = Runtime.getRuntime().availableProcessors();
        int chunkSize = (words.length + processors - 1) / processors;

        List<CompletableFuture<Map<String, Integer>>> futures = new ArrayList<>();

        for (int i = 0; i < words.length; i += chunkSize) {
            int start = i;
            int end = Math.min(i + chunkSize, words.length);

            CompletableFuture<Map<String, Integer>> future = CompletableFuture.supplyAsync(() -> processChunk(Arrays.copyOfRange(words, start, end)));
            futures.add(future);
        }

        long startTime = System.currentTimeMillis();
        CompletableFuture<Void> allDone = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allDone.join();

        Map<String, Integer> finalBagOfWords = new HashMap<>();
        for (CompletableFuture<Map<String, Integer>> future : futures) {
            try {
                Map<String, Integer> partialResult = future.get();
                partialResult.forEach((word, count) -> finalBagOfWords.merge(word, count, Integer::sum));
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

        }

        List<ThreadMetricsDTO> threadMetricsDTOList = new ArrayList<>();
        long longestThreadTimeInMs = 0;
        for (String threadName : threadNameToExecutionTimesMap.keySet()) {
            long timeInMs = threadNameToExecutionTimesMap.get(threadName);
            threadMetricsDTOList.add(
                    new ThreadMetricsDTO(threadName, timeInMs)
            );
            longestThreadTimeInMs = Math.max(longestThreadTimeInMs, timeInMs);
        }
        long endTime = System.currentTimeMillis();
        long combinedTime = longestThreadTimeInMs + endTime - startTime;
        return BowResultDTO.builder()
                .strategyType(Constant.CONCURRENT_TWO)
                .executionTimeInMs(combinedTime)
                .bagOfWords(finalBagOfWords)
                .threadMetricsDTOList(threadMetricsDTOList)
                .build();
    }

    private static Map<String, Integer> processChunk(String[] chunk) {
        long startTime = System.currentTimeMillis();
        Map<String, Integer> frequencyMap = new HashMap<>();
        for (String word : chunk) {
            frequencyMap.merge(word, 1, Integer::sum);
        }
        String threadName = Thread.currentThread().getName();
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        threadNameToExecutionTimesMap.merge(threadName, elapsedTime, Long::sum);
        return frequencyMap;
    }
}
