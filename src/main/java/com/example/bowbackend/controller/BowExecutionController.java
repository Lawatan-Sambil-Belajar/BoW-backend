package com.example.bowbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
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

@RestController
@RequestMapping("/bow")
public class BowExecutionController {

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/sequential")
    public ResponseEntity<BowResultDTO> executeBowSequential(@RequestPart MultipartFile textFile) {
        if (!textFile.getOriginalFilename().endsWith(Constant.TEXT_FILE_SUFFIX)) {
            return null;
        }
        String[] words = TextFileToWordExtractor.extractToWords(textFile);
        return ResponseEntity.ok(BowSequentialService.executeBowSequential(words));
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/concurrent/{methodType}")
    public ResponseEntity<BowResultDTO> executeBowConcurrent(@PathVariable("methodType") int methodType,
                                                             @RequestPart MultipartFile textFile) {
        if (!textFile.getOriginalFilename().endsWith(Constant.TEXT_FILE_SUFFIX)) {
            return null;
        }
        String[] words = TextFileToWordExtractor.extractToWords(textFile);
        BowResultDTO bowResultDTO = null;
        if (methodType == 1) {
            bowResultDTO = BowConcurrentOneService.executeBowConcurrentOne(words);
        } else if (methodType == 2) {
            bowResultDTO = BowConcurrentTwoService.executeBowConcurrentTwo(words);
        }
        return ResponseEntity.ok(bowResultDTO);
    }
}
