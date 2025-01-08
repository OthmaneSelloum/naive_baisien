package com.test;


import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.StringReader;

import static java.awt.SystemColor.text;


public class Naive_Baisien {
    private Map<String, Map<String, Integer>> wordCounts = new HashMap();
    private Map<String, Integer> classCounts = new HashMap();
    private int totalDocuments = 0;

    public Naive_Baisien() {
    }

    private Map<String, String> loadDataset(String datasetPath) throws IOException {
        Map<String, String> trainingData = new HashMap();
        Files.walk(Paths.get(datasetPath)).forEach((filePath) -> {
            if (Files.isRegularFile(filePath, new LinkOption[0])) {
                try {
                    String content = new String(Files.readAllBytes(filePath));
                    String label = filePath.getParent().getFileName().toString();
                    String fileName = filePath.getFileName().toString().replace(".txt", "");
                    trainingData.put(fileName + "_" + label, content);
                } catch (IOException var5) {
                    IOException e = var5;
                    e.printStackTrace();
                }
            }

        });
        return trainingData;
    }

    private List<List<Map.Entry<String, String>>> crossValidate(Map<String, String> data, int n) {
        List<Map.Entry<String, String>> dataList = new ArrayList(data.entrySet());
        Collections.shuffle(dataList);
        List<List<Map.Entry<String, String>>> folds = new ArrayList();
        int foldSize = dataList.size() / n;

        for(int i = 0; i < n; ++i) {
            int start = i * foldSize;
            int end = i == n - 1 ? dataList.size() : (i + 1) * foldSize;
            folds.add(dataList.subList(start, end));
        }

        return folds;
    }

    private List<String> applyStemming(String var1) {
        List<String> stemmedWords = new ArrayList<>();
        try (EnglishAnalyzer analyzer = new EnglishAnalyzer()) {
            TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(var1));
            CharTermAttribute charTermAttr = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();

            while (tokenStream.incrementToken()) {
                stemmedWords.add(charTermAttr.toString());
            }
            tokenStream.end();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stemmedWords;
    }

    public void train(Map<String, String> trainingData) {
        Iterator var3 = trainingData.entrySet().iterator();

        while(var3.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry)var3.next();
            String label = ((String)entry.getKey()).split("_")[1];
            String document = (String)entry.getValue();
            this.classCounts.put(label, (Integer)this.classCounts.getOrDefault(label, 0) + 1);
            ++this.totalDocuments;
            List<String> stemmedWords = this.applyStemming(document.toLowerCase());
            Iterator var8 = stemmedWords.iterator();

            while(var8.hasNext()) {
                String word = (String)var8.next();
                this.wordCounts.putIfAbsent(label, new HashMap());
                Map<String, Integer> counts = (Map)this.wordCounts.get(label);
                counts.put(word, (Integer)counts.getOrDefault(word, 0) + 1);
            }
        }

    }

    public String classify(String document) {
        List<String> stemmedWords = this.applyStemming(document.toLowerCase());
        String bestClass = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        Iterator var7 = this.classCounts.keySet().iterator();

        while(var7.hasNext()) {
            String label = (String)var7.next();
            double score = Math.log((double)(Integer)this.classCounts.get(label) / (double)this.totalDocuments);

            int wordCount;
            int totalWordsInClass;
            for(Iterator var11 = stemmedWords.iterator(); var11.hasNext(); score += Math.log(((double)wordCount + 1.0) / (double)(totalWordsInClass + this.wordCounts.size()))) {
                String word = (String)var11.next();
                wordCount = (Integer)((Map)this.wordCounts.getOrDefault(label, new HashMap())).getOrDefault(word, 0);
                totalWordsInClass = (this.wordCounts.get(label)).values().stream().mapToInt(Integer::intValue).sum();
            }

            if (score > bestScore) {
                bestScore = score;
                bestClass = label;
            }
        }

        return bestClass;
    }

    private double calculatePrecision(Map<String, String> trueLabels, Map<String, String> predictedLabels) {
        int truePositives = 0;
        int retrievedDocuments = predictedLabels.size();
        Iterator var6 = predictedLabels.keySet().iterator();

        while(var6.hasNext()) {
            String file = (String)var6.next();
            String predicted = (String)predictedLabels.get(file);
            String trueLabel = (String)trueLabels.get(file);
            if (predicted.equals(trueLabel)) {
                ++truePositives;
            }
        }

        return retrievedDocuments == 0 ? 0.0 : (double)truePositives / (double)retrievedDocuments;
    }

    private double calculateRecall(Map<String, String> trueLabels, Map<String, String> predictedLabels) {
        int truePositives = 0;
        int relevantDocuments = trueLabels.size();
        Iterator var6 = trueLabels.keySet().iterator();

        while(var6.hasNext()) {
            String file = (String)var6.next();
            String trueLabel = (String)trueLabels.get(file);
            String predicted = (String)predictedLabels.get(file);
            if (predicted != null && predicted.equals(trueLabel)) {
                ++truePositives;
            }
        }

        return relevantDocuments == 0 ? 0.0 : (double)truePositives / (double)relevantDocuments;
    }

    private double calculateF1Score(double precision, double recall) {
        return precision + recall == 0.0 ? 0.0 : 2.0 * precision * recall / (precision + recall);
    }

    public void crossValidateAndEvaluate(String datasetPath, int n) throws Exception {
        Map<String, String> trainingData = this.loadDataset(datasetPath);
        System.out.println("Nombre de documents chargés : " + trainingData.size());
        List<List<Map.Entry<String, String>>> folds = this.crossValidate(trainingData, n);
        double totalAccuracy = 0.0;
        double totalRecall = 0.0;
        double totalF1Score = 0.0;

        for(int i = 0; i < n; ++i) {
            System.out.println("\nFold " + (i + 1));
            List<Map.Entry<String, String>> testFold = (List)folds.get(i);
            List<Map.Entry<String, String>> trainFold = new ArrayList();

            int correctPredictions;
            for(correctPredictions = 0; correctPredictions < n; ++correctPredictions) {
                if (correctPredictions != i) {
                    trainFold.addAll((Collection)folds.get(correctPredictions));
                }
            }

            this.train(this.getMapFromList(trainFold));
            correctPredictions = 0;
            Map<String, String> trueLabels = new HashMap();
            Map<String, String> predictedLabels = new HashMap();
            Iterator var18 = testFold.iterator();

            while(var18.hasNext()) {
                Map.Entry<String, String> testEntry = (Map.Entry)var18.next();
                String document = (String)testEntry.getValue();
                String trueLabel = ((String)testEntry.getKey()).split("_")[1];
                String predictedLabel = this.classify(document);
                trueLabels.put((String)testEntry.getKey(), trueLabel);
                predictedLabels.put((String)testEntry.getKey(), predictedLabel);
                if (predictedLabel.equals(trueLabel)) {
                    ++correctPredictions;
                }
            }

            double accuracy = (double)correctPredictions / (double)testFold.size();
            System.out.println("Précision pour ce fold : " + accuracy * 100.0 + "%");
            totalAccuracy += accuracy;
            double recall = this.calculateRecall(trueLabels, predictedLabels);
            System.out.println("Recall pour ce fold : " + recall * 100.0 + "%");
            totalRecall += recall;
            double precision = this.calculatePrecision(trueLabels, predictedLabels);
            double f1Score = this.calculateF1Score(precision, recall);
            System.out.println("F1-Score pour ce fold : " + f1Score * 100.0 + "%");
            totalF1Score += f1Score;
        }

        double averageAccuracy = totalAccuracy / (double)n;
        double averageRecall = totalRecall / (double)n;
        double averageF1Score = totalF1Score / (double)n;
        System.out.println("\nPrécision moyenne de la cross-validation : " + averageAccuracy * 100.0 + "%");
        System.out.println("Recall moyen de la cross-validation : " + averageRecall * 100.0 + "%");
        System.out.println("F1-Score moyen de la cross-validation : " + averageF1Score * 100.0 + "%");
    }

    private Map<String, String> getMapFromList(List<Map.Entry<String, String>> list) {
        Map<String, String> map = new HashMap();
        Iterator var4 = list.iterator();

        while(var4.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry)var4.next();
            map.put((String)entry.getKey(), (String)entry.getValue());
        }

        return map;
    }

    public static void main(String[] args) {
        Naive_Baisien classifier = new Naive_Baisien();
        String datasetPath = "C:\\Users\\Othmane\\Desktop\\Nouveau dossier";

        try {
            classifier.crossValidateAndEvaluate(datasetPath, 8);
        } catch (Exception var4) {
            Exception e = var4;
            e.printStackTrace();
        }

    }
}
