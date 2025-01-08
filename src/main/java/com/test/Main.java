package com.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException {
        String corpusPath = "Corpus";
        String stopwordsPath = "StopwordsEn.txt";

        Map<String, String> classLabels = new HashMap<>();
        Map<String, String> corpus = CorpusReader.readCorpus(corpusPath, classLabels);

        DataPreProcessing preprocessor = new DataPreProcessing(stopwordsPath);
        for (Map.Entry<String, String> entry : corpus.entrySet()) {
            corpus.put(entry.getKey(), preprocessor.preprocess(entry.getValue()));
        }

        Set<String> folds = DataSpliter.getUniqueFolds(corpus);
        for (String testFold : folds) {
            System.out.println("Utilisation du fold " + testFold + " comme test.");

            Map<String, Map<String, String>> split = DataSpliter.splitByFold(corpus, classLabels, testFold);
            Map<String, String> trainSet = split.get("train");
            Map<String, String> testSet = split.get("test");

            NaiveBayes classifier = new NaiveBayes();
            classifier.train(trainSet, classLabels);

            Evaluation.evaluate(classifier, testSet, classLabels);
        }
    }
}
