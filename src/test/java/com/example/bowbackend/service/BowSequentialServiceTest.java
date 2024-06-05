package com.example.bowbackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.bowbackend.dto.BowResultDTO;

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