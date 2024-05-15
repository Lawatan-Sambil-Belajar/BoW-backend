package com.example.bowbackend.controller;

import com.example.bowbackend.constant.Constant;
import com.example.bowbackend.dto.BowResultDTO;
import com.example.bowbackend.helper.TextFileToWordExtractor;
import com.example.bowbackend.service.BowConcurrentOneService;
import com.example.bowbackend.service.BowConcurrentTwoService;
import com.example.bowbackend.service.BowSequentialService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/bow")
public class BowExecutionController {

    @PostMapping("/sequential")
    public ResponseEntity<BowResultDTO> executeBowSequential(@RequestPart MultipartFile textFile) {
        if (!textFile.getOriginalFilename().endsWith(Constant.TEXT_FILE_SUFFIX)) {
            return null;
        }
        String[] words = TextFileToWordExtractor.extractToWords(textFile);
        return ResponseEntity.ok(BowSequentialService.executeBowSequential(words));
    }

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
