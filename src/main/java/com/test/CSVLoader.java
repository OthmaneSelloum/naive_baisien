package com.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVLoader {
    public static void main(String[] args) {
        String csvFile = "C:/Users/Othmane/Downloads/world_bank_dataset.csv";
        String line;
        String separator = ",";

        List<String[]> data = new ArrayList<>();
        int[] columnWidths = null;

        // Lire les données et stocker dans une liste
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] row = line.split(separator);
                data.add(row);

                // Calculer la largeur maximale de chaque colonne
                if (columnWidths == null) {
                    columnWidths = new int[row.length];
                }
                for (int i = 0; i < row.length; i++) {
                    columnWidths[i] = Math.max(columnWidths[i], row[i].length());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Afficher les données sous forme de tableau
        for (String[] row : data) {
            for (int i = 0; i < row.length; i++) {
                System.out.printf("%-" + (columnWidths[i] + 2) + "s", row[i]);
            }
            System.out.println();
        }
    }
}
