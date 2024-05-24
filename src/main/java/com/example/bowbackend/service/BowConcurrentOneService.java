package com.example.bowbackend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import com.example.bowbackend.constant.Constant;
import com.example.bowbackend.dto.BowResultDTO;
import com.example.bowbackend.dto.ThreadMetricsDTO;

public class BowConcurrentOneService extends RecursiveTask<HashMap<String, Integer>> {
    private final String[] words;
    private static final int LIMIT = 10000;
    private int start;
    private int end;
    private static final ConcurrentHashMap<String, Long> threadNameToExecutionTimesMap = new ConcurrentHashMap<>();

    public BowConcurrentOneService(String[] words, int start, int end) {
        this.words = words;
        this.start = start;
        this.end = end;
    }

    @Override
    protected HashMap<String, Integer> compute() {
        long startTime = System.currentTimeMillis();
        if ((end - start) <= LIMIT) {
            return computeDirectly();
        } else {
            int mid = start + ((end - start) / 2);
            BowConcurrentOneService left = new BowConcurrentOneService(words, start, mid);
            BowConcurrentOneService right = new BowConcurrentOneService(words, mid, end);

            left.fork();
            right.fork();
            HashMap<String, Integer> leftResult = left.join();
            HashMap<String, Integer> rightResult = right.join();

            mergeBowMaps(leftResult, rightResult);
            long endTime = System.currentTimeMillis();
            String threadName = Thread.currentThread().getName();
            threadNameToExecutionTimesMap.merge(threadName, endTime - startTime, Math::max);
            return leftResult;
        }
    }

    private HashMap<String, Integer> computeDirectly() {
        HashMap<String, Integer> bagOfWords = new HashMap<>();

        for (int i = start; i < end; i++) {
            bagOfWords.put(words[i], bagOfWords.getOrDefault(words[i], 0) + 1);
        }

        return bagOfWords;
    }

    private void mergeBowMaps(HashMap<String, Integer> leftResult, HashMap<String, Integer> rightResult) {
        for (Map.Entry<String, Integer> entry : rightResult.entrySet()) {
            leftResult.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
    }

    public static BowResultDTO executeBowConcurrentOne(String[] words) {
        BowConcurrentOneService bowTask = new BowConcurrentOneService(words, 0, words.length);
        ForkJoinPool pool = new ForkJoinPool();
        long startTime = System.currentTimeMillis();
        HashMap<String, Integer> bagOfWords = pool.invoke(bowTask);
        long endTime = System.currentTimeMillis();
        List<ThreadMetricsDTO> threadMetricsDTOList = new ArrayList<>();
        long longestThreadTimeInMs = 0;
        for (String threadName : threadNameToExecutionTimesMap.keySet()) {
            long timeInMs = threadNameToExecutionTimesMap.get(threadName);
            threadMetricsDTOList.add(
                    new ThreadMetricsDTO(threadName, timeInMs)
            );
            longestThreadTimeInMs = Math.max(longestThreadTimeInMs, timeInMs);
        }
        return BowResultDTO.builder()
                .strategyType(Constant.CONCURRENT_ONE)
                .executionTimeInMs(endTime - startTime)
                .bagOfWords(bagOfWords)
                .threadMetricsDTOList(threadMetricsDTOList)
                .build();
    }
}
