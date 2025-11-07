package com.elearn.util;

import java.io.*;
import java.nio.file.Files;

public class DocumentTextExtractor {
    
    public static String extractTextFromFile(File file) {
        String fileName = file.getName().toLowerCase();
        
        if (fileName.endsWith(".txt")) {
            return extractTextFromTxt(file);
        } else if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
            return extractTextFromWord(file);
        } else if (fileName.endsWith(".pdf")) {
            return extractTextFromPdf(file);
        } else {
            return "Unsupported file format. Please use .txt, .doc, .docx, or .pdf files.";
        }
    }
    
    private static String extractTextFromTxt(File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            return "Error reading text file: " + e.getMessage();
        }
    }
    
    private static String extractTextFromWord(File file) {
        // For now, return a placeholder message
        // In a real implementation, you would use Apache POI library
        return "Word document detected. Text extraction from Word documents requires Apache POI library.\n" +
               "File: " + file.getName() + "\n" +
               "Please install Apache POI dependency to enable Word document text extraction.\n\n" +
               "To add Apache POI to your project:\n" +
               "1. Download poi-bin-5.2.3.zip from https://poi.apache.org/download.html\n" +
               "2. Extract and add poi-5.2.3.jar, poi-ooxml-5.2.3.jar, and poi-ooxml-schemas-5.2.3.jar to your lib folder\n" +
               "3. Update your classpath to include these JAR files";
    }
    
    private static String extractTextFromPdf(File file) {
        // For now, return a placeholder message
        // In a real implementation, you would use Apache PDFBox library
        return "PDF document detected. Text extraction from PDF documents requires Apache PDFBox library.\n" +
               "File: " + file.getName() + "\n" +
               "Please install Apache PDFBox dependency to enable PDF text extraction.\n\n" +
               "To add Apache PDFBox to your project:\n" +
               "1. Download pdfbox-app-2.0.28.jar from https://pdfbox.apache.org/download.html\n" +
               "2. Add the JAR file to your lib folder\n" +
               "3. Update your classpath to include this JAR file";
    }
    
    // Simple implementation for basic text extraction from Word documents
    // This is a basic implementation that works for simple .doc files
    public static String extractTextFromWordBasic(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            StringBuilder content = new StringBuilder();
            int data;
            boolean inText = false;
            StringBuilder currentWord = new StringBuilder();
            
            while ((data = fis.read()) != -1) {
                char c = (char) data;
                
                // Simple heuristic to extract readable text
                if (Character.isLetterOrDigit(c) || Character.isWhitespace(c) || c == '.' || c == ',' || c == '!' || c == '?') {
                    if (Character.isLetterOrDigit(c)) {
                        currentWord.append(c);
                    } else {
                        if (currentWord.length() > 0) {
                            content.append(currentWord.toString());
                            currentWord.setLength(0);
                        }
                        content.append(c);
                    }
                } else if (currentWord.length() > 0) {
                    content.append(currentWord.toString());
                    currentWord.setLength(0);
                }
            }
            
            if (currentWord.length() > 0) {
                content.append(currentWord.toString());
            }
            
            String result = content.toString();
            return result.length() > 100 ? result : "Could not extract meaningful text from the document. The file might be corrupted or in an unsupported format.";
            
        } catch (IOException e) {
            return "Error reading Word document: " + e.getMessage();
        }
    }
}
