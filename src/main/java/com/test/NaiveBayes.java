package com.test;

import java.util.*;

public class NaiveBayes {
    private final Map<String, Map<String, Double>> probabilities = new HashMap<>();
    private final Map<String, Double> classPrior = new HashMap<>();
    private final Set<String> vocabulary = new HashSet<>();

    public void train(Map<String, String> trainSet, Map<String, String> classLabels) {
        Map<String, Map<String, Integer>> termCounts = new HashMap<>();
        Map<String, Integer> classDocCounts = new HashMap<>();

        for (Map.Entry<String, String> entry : trainSet.entrySet()) {
            String className = classLabels.get(entry.getKey());
            classDocCounts.put(className, classDocCounts.getOrDefault(className, 0) + 1);

            String[] words = entry.getValue().split("\\s+");
            termCounts.putIfAbsent(className, new HashMap<>());
            for (String word : words) {
                vocabulary.add(word);
                termCounts.get(className).put(word, termCounts.get(className).getOrDefault(word, 0) + 1);
            }
        }

        int totalDocs = trainSet.size();
        for (String className : classDocCounts.keySet()) {
            classPrior.put(className, classDocCounts.get(className) / (double) totalDocs);
        }

        for (String className : termCounts.keySet()) {
            int totalTerms = termCounts.get(className).values().stream().mapToInt(Integer::intValue).sum();
            probabilities.put(className, new HashMap<>());

            for (String word : vocabulary) {
                int count = termCounts.get(className).getOrDefault(word, 0);
                probabilities.get(className).put(word, (count + 1.0) / (totalTerms + vocabulary.size())); // Laplace smoothing
            }
        }
    }

    public String predict(String text) {
        String[] words = text.split("\\s+");
        double maxProbability = Double.NEGATIVE_INFINITY;
        String predictedClass = null;

        for (String className : probabilities.keySet()) {
            double logProb = Math.log(classPrior.get(className));

            for (String word : words) {
                logProb += Math.log(probabilities.get(className).getOrDefault(word, 1.0 / (vocabulary.size() + 1)));
            }

            if (logProb > maxProbability) {
                maxProbability = logProb;
                predictedClass = className;
            }
        }
        return predictedClass;
    }
}
