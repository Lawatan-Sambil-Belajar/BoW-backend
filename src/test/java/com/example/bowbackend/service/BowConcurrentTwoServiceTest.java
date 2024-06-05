package com.example.bowbackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.bowbackend.dto.BowResultDTO;

class BowConcurrentTwoServiceTest {
    private String[] words;
    private BowResultDTO result;

    @BeforeEach
    void setup() {
        words = new String[]{"apple", "banana", "apple", "cherry", "banana", "cherry"};
        result = BowConcurrentTwoService.executeBowConcurrentTwo(words);
    }

    @Test
    void executeBowConcurrentTwoShouldReturnCorrectWordCount() {
        assertEquals(2, result.getBagOfWords().get("apple"));
        assertEquals(2, result.getBagOfWords().get("banana"));
        assertEquals(2, result.getBagOfWords().get("cherry"));
    }


    @Test
    void executeBowConcurrentTwoShouldReturnConcurrentTwoStrategyType() {
        assertEquals("Concurrent 2", result.getStrategyType());
    }

    @Test
    void executeBowConcurrentTwoShouldHandleEmptyInput() {
        BowResultDTO emptyResult = BowConcurrentTwoService.executeBowConcurrentTwo(new String[]{});
        assertTrue(emptyResult.getBagOfWords().isEmpty());
        assertEquals("Concurrent 2", emptyResult.getStrategyType());
    }

    @Test
    void executeBowConcurrentTwoShouldReturnThreadMetrics() {
        assertFalse(result.getThreadMetricsDTOList().isEmpty());
    }
}