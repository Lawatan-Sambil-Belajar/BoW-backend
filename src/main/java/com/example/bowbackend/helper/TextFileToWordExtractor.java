package com.example.bowbackend.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class TextFileToWordExtractor {

    public static String[] extractToWords(MultipartFile textFile) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(textFile.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String[] words = sb.toString().trim().split("\\s+");
        String regex = "[^a-zA-Z]";
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].replaceAll(regex, "");
        }
        List<String> wordsList = new ArrayList<>(Arrays.asList(words));
        wordsList.removeAll(Arrays.asList("", null));
        words = wordsList.toArray(new String[0]);
        return words;
    }
}
