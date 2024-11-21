package com.test;


import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Reformate {
    public Reformate() {
    }

    public static void renameAndMoveFiles(String rootDirectoryPath, String destinationDirectoryPath, int n) {
        File rootDirectory = new File(rootDirectoryPath);
        File destinationDirectory = new File(destinationDirectoryPath);
        if (rootDirectory.exists() && rootDirectory.isDirectory()) {
            if (!destinationDirectory.exists() && !destinationDirectory.mkdirs()) {
                System.out.println("Erreur lors de la création du dossier de destination.");
            } else {
                System.out.println("Dossier de destination : " + destinationDirectory.getAbsolutePath());
                File[] classDirectories = rootDirectory.listFiles(File::isDirectory);
                if (classDirectories != null) {
                    File[] var9 = classDirectories;
                    int var8 = classDirectories.length;

                    for(int var7 = 0; var7 < var8; ++var7) {
                        File classDirectory = var9[var7];
                        String className = classDirectory.getName();
                        System.out.println("Traitement du sous-dossier (classe) : " + className);
                        File[] classFiles = classDirectory.listFiles();
                        if (classFiles != null && classFiles.length > 0) {
                            List<File> fileList = new ArrayList();
                            Collections.addAll(fileList, classFiles);
                            Collections.shuffle(fileList);
                            int totalFiles = fileList.size();
                            int foldSize = totalFiles / n;
                            int remainder = totalFiles % n;
                            int fileIndex = 0;

                            for(int fold = 1; fold <= n; ++fold) {
                                int currentFoldSize = foldSize + (fold <= remainder ? 1 : 0);

                                for(int i = 0; i < currentFoldSize; ++i) {
                                    File file = (File)fileList.get(fileIndex++);
                                    String fileName = file.getName();
                                    int dotIndex = fileName.lastIndexOf(".");
                                    String newFileName;
                                    if (dotIndex == -1) {
                                        newFileName = fileName + "_" + className + "_" + fold;
                                    } else {
                                        newFileName = fileName.substring(0, dotIndex) + "_" + className + "_" + fold + fileName.substring(dotIndex);
                                    }

                                    File renamedFile = new File(destinationDirectory, newFileName);
                                    if (file.renameTo(renamedFile)) {
                                        PrintStream var10000 = System.out;
                                        String var25 = file.getName();
                                        var10000.println("Fichier renommé et déplacé : " + var25 + " -> " + newFileName);
                                    } else {
                                        System.out.println("Échec du renommage ou du déplacement du fichier : " + file.getName());
                                    }
                                }
                            }
                        } else {
                            System.out.println("Aucun fichier trouvé dans la classe " + className);
                        }
                    }
                } else {
                    System.out.println("Aucun sous-dossier trouvé dans le répertoire racine.");
                }

            }
        } else {
            System.out.println("Le dossier spécifié n'existe pas ou n'est pas un répertoire.");
        }
    }

    public static void main(String[] args) {
        String rootDirectoryPath = "mini_newsgroups";
        String destinationDirectoryPath = "C:\\Users\\Othmane\\Desktop\\Nouveau dossier";
        int n = 10;
        renameAndMoveFiles(rootDirectoryPath, destinationDirectoryPath, n);
    }
}
