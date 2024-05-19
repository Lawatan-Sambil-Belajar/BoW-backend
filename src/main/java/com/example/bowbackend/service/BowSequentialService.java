package com.example.bowbackend.service;

import java.util.HashMap;

import com.example.bowbackend.constant.Constant;
import com.example.bowbackend.dto.BowResultDTO;

public class BowSequentialService {

    public static BowResultDTO executeBowSequential(String[] words) {
        long startTime = System.currentTimeMillis();
        HashMap<String, Integer> bagOfWords = new HashMap<>();
        // Count the occurrences of each word
        for (String word : words) {
            bagOfWords.put(word, bagOfWords.getOrDefault(word, 0) + 1);
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        return BowResultDTO.builder()
                .strategyType(Constant.SEQUENTIAL)
                .executionTimeInMs(elapsedTime)
                .bagOfWords(bagOfWords)
                .build();
    }
}
