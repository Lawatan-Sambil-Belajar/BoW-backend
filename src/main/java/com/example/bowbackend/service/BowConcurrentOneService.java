package com.example.bowbackend.service;

import com.example.bowbackend.constant.Constant;
import com.example.bowbackend.dto.BowResultDTO;
import com.example.bowbackend.dto.ThreadMetricsDTO;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class BowConcurrentOneService extends RecursiveTask<HashMap<String, Integer>> {

    private final String[] words;
    private static final int LIMIT = 1000000;
    private static final ConcurrentHashMap<String, Long> threadNameToExecutionTimesMap = new ConcurrentHashMap<>();

    public BowConcurrentOneService(String[] words) {
        this.words = words;
    }

    @Override
    protected HashMap<String, Integer> compute() {
        long startTime = System.currentTimeMillis();
        try {
            if (words.length <= LIMIT) {
                return computeDirectly();
            } else {
                int mid = words.length / 2;

                String[] leftArr = Arrays.copyOfRange(words, 0, mid);
                String[] rightArr = Arrays.copyOfRange(words, mid, words.length);
                BowConcurrentOneService left = new BowConcurrentOneService(leftArr);
                BowConcurrentOneService right = new BowConcurrentOneService(rightArr);

                left.fork();
                right.fork();
                HashMap<String, Integer> leftResult = left.join();
                HashMap<String, Integer> rightResult = right.join();

                mergeBowMaps(leftResult, rightResult);

                return leftResult;
            }
        } finally {
            long endTime = System.currentTimeMillis();
            String threadName = Thread.currentThread().getName();
            long elapsedTime = endTime - startTime;
            threadNameToExecutionTimesMap.merge(threadName, elapsedTime, Long::sum);
        }
    }

    private HashMap<String, Integer> computeDirectly() {
        HashMap<String, Integer> bagOfWords = new HashMap<>();

        for (String word : words) {
            bagOfWords.put(word, bagOfWords.getOrDefault(word, 0) + 1);
        }

        return bagOfWords;
    }

    private void mergeBowMaps(HashMap<String, Integer> leftResult, HashMap<String, Integer> rightResult) {
        for (Map.Entry<String, Integer> entry : rightResult.entrySet()) {
            leftResult.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
    }

    public static BowResultDTO executeBowConcurrentOne(String[] words) {
        BowConcurrentOneService bowTask = new BowConcurrentOneService(words);
        ForkJoinPool pool = new ForkJoinPool();
        HashMap<String, Integer> bagOfWords = pool.invoke(bowTask);
        List<ThreadMetricsDTO> threadMetricsDTOList = new ArrayList<>();
        long startTime = System.currentTimeMillis();
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
                .strategyType(Constant.CONCURRENT_ONE)
                .executionTimeInMs(combinedTime)
                .bagOfWords(bagOfWords)
                .threadMetricsDTOList(threadMetricsDTOList)
                .build();
    }
}

