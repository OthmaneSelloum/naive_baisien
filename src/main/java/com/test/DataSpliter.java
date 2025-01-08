package com.test;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataSpliter {
    public static Map<String, Map<String, String>> splitByFold(Map<String, String> corpus, Map<String, String> classLabels, String testFold) {
        Map<String, String> trainSet = new HashMap<>();
        Map<String, String> testSet = new HashMap<>();

        for (String fileName : corpus.keySet()) {
            String[] parts = fileName.split("_");

            String fold = parts[2];
            if (fold.equals(testFold)) {
                testSet.put(fileName, corpus.get(fileName));
            } else {
                trainSet.put(fileName, corpus.get(fileName));
            }
        }

        Map<String, Map<String, String>> split = new HashMap<>();
        split.put("train", trainSet);
        split.put("test", testSet);
        return split;
    }

    public static Set<String> getUniqueFolds(Map<String, String> corpus) {
        Set<String> folds = new HashSet<>();
        for (String fileName : corpus.keySet()) {
            String[] parts = fileName.split("_");
            if (parts.length >= 3) {
                folds.add(parts[2]);
            }
        }
        return folds;
    }
}

