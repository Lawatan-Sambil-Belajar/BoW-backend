package com.example.bowbackend.service;

import com.example.bowbackend.dto.BowResultDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BowSequentialServiceTest {
    private String[] words;
    private BowResultDTO result;

    @BeforeEach
    void setup() {
        words = new String[]{"apple", "banana", "apple", "cherry", "banana", "cherry"};
        result = BowSequentialService.executeBowSequential(words);
    }

    @Test
    void executeBowSequentialShouldReturnCorrectWordCount() {
        assertEquals(2, result.getBagOfWords().get("apple"));
        assertEquals(2, result.getBagOfWords().get("banana"));
        assertEquals(2, result.getBagOfWords().get("cherry"));
    }

    @Test
    void executeBowSequentialShouldReturnCorrectExecutionTime() {
        assertTrue(result.getExecutionTimeInMs() >= 0);
    }

    @Test
    void executeBowSequentialShouldReturnSequentialStrategyType() {
        assertEquals("Sequential", result.getStrategyType());
    }

    @Test
    void executeBowSequentialShouldHandleEmptyInput() {
        BowResultDTO emptyResult = BowSequentialService.executeBowSequential(new String[]{});
        assertTrue(emptyResult.getBagOfWords().isEmpty());
        assertEquals("Sequential", emptyResult.getStrategyType());
    }
}