package com.test;

import java.util.HashMap;
import java.util.Map;

public class Evaluation {
    public static void evaluate(NaiveBayes classifier, Map<String, String> testSet, Map<String, String> classLabels) {
        int correct = 0;
        int total = testSet.size();

        Map<String, Integer> truePositives = new HashMap<>();
        Map<String, Integer> falsePositives = new HashMap<>();
        Map<String, Integer> falseNegatives = new HashMap<>();

        for (String className : classLabels.values()) {
            truePositives.put(className, 0);
            falsePositives.put(className, 0);
            falseNegatives.put(className, 0);
        }

        for (Map.Entry<String, String> entry : testSet.entrySet()) {
            String actualClass = classLabels.get(entry.getKey());
            String predictedClass = classifier.predict(entry.getValue());

            if (predictedClass.equals(actualClass)) {
                correct++;
                truePositives.put(actualClass, truePositives.get(actualClass) + 1);
            } else {
                falsePositives.put(predictedClass, falsePositives.getOrDefault(predictedClass, 0) + 1);
                falseNegatives.put(actualClass, falseNegatives.get(actualClass) + 1);
            }
        }

        double precision = truePositives.values().stream().mapToInt(Integer::intValue).sum() /
                (double) (truePositives.values().stream().mapToInt(Integer::intValue).sum() + falsePositives.values().stream().mapToInt(Integer::intValue).sum());
        double recall = truePositives.values().stream().mapToInt(Integer::intValue).sum() /
                (double) (truePositives.values().stream().mapToInt(Integer::intValue).sum() + falseNegatives.values().stream().mapToInt(Integer::intValue).sum());
        double f1Score = 2 * precision * recall / (precision + recall);

        System.out.printf("Precision: %.2f%%\n", precision * 100);
        System.out.printf("Recall: %.2f%%\n", recall * 100);
        System.out.printf("F1-Score: %.2f%%\n", f1Score * 100);
        System.out.printf("Accuracy: %.2f%%\n", (double) correct / total * 100);
    }
}

