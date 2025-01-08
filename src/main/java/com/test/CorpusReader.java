package com.test;


import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class CorpusReader {
    public static Map<String, String> readCorpus(String corpusPath, Map<String, String> classLabels) throws IOException {
        Map<String, String> corpus = new HashMap<>();
        Files.walk(Paths.get(corpusPath)).forEach(path -> {
            if (Files.isRegularFile(path)) {
                String fileName = path.getFileName().toString();
                try {
                    String content = new String(Files.readAllBytes(path));
                    corpus.put(fileName, content);

                    // Extraire la classe à partir du nom du fichier
                    String[] parts = fileName.split("_");
                    if (parts.length >= 2) {
                        classLabels.put(fileName, parts[1]); // La classe est la deuxième partie du nom
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return corpus;
    }
}
