package com.example.bowbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.bowbackend.constant.Constant;
import com.example.bowbackend.dto.BowResultDTO;
import com.example.bowbackend.helper.TextFileToWordExtractor;
import com.example.bowbackend.service.BowConcurrentOneService;
import com.example.bowbackend.service.BowConcurrentTwoService;
import com.example.bowbackend.service.BowSequentialService;

import java.util.List;

@RestController
@RequestMapping("/bow")
public class BowExecutionController {

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/all")
    public ResponseEntity<List<BowResultDTO>> executeBowAlgorithms(@RequestPart MultipartFile textFile) {
        String[] words = TextFileToWordExtractor.extractToWords(textFile);
        BowResultDTO sequentialBowResult = BowSequentialService.executeBowSequential(words);
        BowResultDTO concurrentOneBowResult = BowConcurrentOneService.executeBowConcurrentOne(words);
        BowResultDTO concurrentTwoBowResult = BowConcurrentTwoService.executeBowConcurrentTwo(words);
        return ResponseEntity.ok(List.of(sequentialBowResult, concurrentOneBowResult, concurrentTwoBowResult));
    }
}
