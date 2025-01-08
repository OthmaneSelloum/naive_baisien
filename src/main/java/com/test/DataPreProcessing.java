package com.test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import org.tartarus.snowball.ext.PorterStemmer;


public class DataPreProcessing {
    private final Set<String> stopwords;

    public DataPreProcessing(String stopwordsFilePath) throws IOException {
        stopwords = new HashSet<>(Files.readAllLines(Paths.get(stopwordsFilePath)));
    }

    public String preprocess(String text) {
        String[] words = text.toLowerCase().split("\\W+");
        List<String> filteredWords = Arrays.stream(words)
                .filter(word -> !stopwords.contains(word))
                .map(this::stem)
                .collect(Collectors.toList());
        return String.join(" ", filteredWords);
    }


    private String stem(String word) {
        PorterStemmer stemmer = new PorterStemmer();
        stemmer.setCurrent(word);
        stemmer.stem();
        return stemmer.getCurrent(); // Retourner le mot après le stemming
    }
}
