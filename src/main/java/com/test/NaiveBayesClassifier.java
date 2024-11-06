package com.test;

import java.io.*;
import java.util.*;

public class NaiveBayesClassifier {

    // Map pour stocker le nombre de documents par classe
    private Map<String, Integer> classCounts = new HashMap<>();
    // Map pour stocker les occurrences de chaque mot dans chaque classe
    private Map<String, Map<String, Integer>> wordCounts = new HashMap<>();
    // Nombre total de documents dans toutes les classes
    private int totalDocuments = 0;
    // Vocabulaire unique de tous les mots dans tous les documents
    private Set<String> vocabulary = new HashSet<>();

    // Fonction pour lire les fichiers et remplir les classes et mots
    public void train(String datasetPath) throws IOException {
        File folder = new File(datasetPath);
        for (File classFolder : folder.listFiles()) {
            String className = classFolder.getName();
            classCounts.put(className, 0);
            wordCounts.put(className, new HashMap<>());

            for (File file : classFolder.listFiles()) {
                String content = readFile(file);
                List<String> words = tokenize(content);
                updateWordCounts(className, words);
                classCounts.put(className, classCounts.get(className) + 1);
                totalDocuments++;
            }
        }
    }

    // Lecture de fichier
    private String readFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append(" ");
        }
        reader.close();
        return content.toString();
    }

    // Tokenizer pour extraire les mots
    private List<String> tokenize(String text) {
        String[] words = text.toLowerCase().replaceAll("[^a-zA-Z]", " ").split("\\s+");
        return Arrays.asList(words);
    }

    // Met à jour les comptages de mots pour une classe donnée
    private void updateWordCounts(String className, List<String> words) {
        Map<String, Integer> wordCount = wordCounts.get(className);
        for (String word : words) {
            vocabulary.add(word);
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }
    }

    // Prédiction de la classe d'un texte donné
    public String predict(String text) {
        List<String> words = tokenize(text);
        String bestClass = null;
        double maxProbability = Double.NEGATIVE_INFINITY;

        for (String className : classCounts.keySet()) {
            double logProbability = Math.log((double) classCounts.get(className) / totalDocuments);
            Map<String, Integer> wordCount = wordCounts.get(className);
            int totalWordsInClass = wordCount.values().stream().mapToInt(Integer::intValue).sum();

            for (String word : words) {
                int wordFrequency = wordCount.getOrDefault(word, 0);
                double wordProbability = (wordFrequency + 1.0) / (totalWordsInClass + vocabulary.size());
                logProbability += Math.log(wordProbability);
            }

            if (logProbability > maxProbability) {
                maxProbability = logProbability;
                bestClass = className;
            }
        }
        return bestClass;
    }

    public static void main(String[] args) throws IOException {
        NaiveBayesClassifier classifier = new NaiveBayesClassifier();
        classifier.train("C:\\Users\\Othmane\\Desktop\\naive_baisien\\mini_newsgroups");  // Remplacez par le chemin de votre dataset

        String testText = "allows, the Land Cruiser? ";
        String predictedClass = classifier.predict(testText);
        System.out.println("Class prédite : " + predictedClass);
    }
}
