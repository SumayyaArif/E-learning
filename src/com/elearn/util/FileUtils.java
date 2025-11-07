package com.elearn.util;

import java.io.File;

public class FileUtils {
    
    /**
     * Gets the file extension from a filename
     * 
     * @param filename The filename to extract extension from
     * @return The extension without the dot, or an empty string if no extension
     */
    public static String getExtension(String filename) {
        if (filename == null) {
            return "";
        }
        
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == filename.length() - 1) {
            return "";
        }
        
        return filename.substring(dotIndex + 1);
    }
    
    /**
     * Creates directories for videos and images if they don't exist
     */
    public static void ensureDirectoriesExist() {
        // video directories removed — videos are no longer supported
        createDirectoryIfNotExists("course_images");
    }
    
    /**
     * Creates a directory if it doesn't exist
     * 
     * @param dirName The directory name to create
     * @return true if directory exists or was created, false otherwise
     */
    public static boolean createDirectoryIfNotExists(String dirName) {
        File dir = new File(dirName);
        if (!dir.exists()) {
            return dir.mkdirs();
        }
        return true;
    }
}