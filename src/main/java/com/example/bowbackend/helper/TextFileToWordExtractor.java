package com.example.bowbackend.helper;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class TextFileToWordExtractor {

    public static String[] extractToWords(MultipartFile textFile) {
        String longText = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(textFile.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                longText += line;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String[] words = longText.trim().split("\\s+");
        String regex = "[^a-zA-Z]";
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].replaceAll(regex, "");
        }
        return words;
    }
}
