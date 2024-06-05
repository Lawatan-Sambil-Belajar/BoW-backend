package com.example.bowbackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.bowbackend.dto.BowResultDTO;

public class BowConcurrentOneServiceTest {
    private BowConcurrentOneService service;
    private String[] words;

    @BeforeEach
    public void setup() {
        words = new String[]{"apple", "banana", "apple", "cherry", "banana", "cherry"};
        service = new BowConcurrentOneService(words, 0, words.length);
    }

    @Test
    public void computeShouldReturnCorrectWordCount() {
        HashMap<String, Integer> result = service.compute();
        assertEquals(2, result.get("apple"));
        assertEquals(2, result.get("banana"));
        assertEquals(2, result.get("cherry"));
    }

    @Test
    public void executeBowConcurrentOneShouldReturnCorrectWordCount() {
        BowResultDTO result = BowConcurrentOneService.executeBowConcurrentOne(words);
        assertEquals(2, result.getBagOfWords().get("apple"));
        assertEquals(2, result.getBagOfWords().get("banana"));
        assertEquals(2, result.getBagOfWords().get("cherry"));
    }


    @Test
    public void executeBowConcurrentOneUsingSmallArrayShouldReturnEmptyThreadMetrics() {
        BowResultDTO result = BowConcurrentOneService.executeBowConcurrentOne(words);
        assertTrue(result.getThreadMetricsDTOList().isEmpty());
    }
}