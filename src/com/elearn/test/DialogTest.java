package com.elearn.test;

import com.elearn.ui.BasicCourseDialog;
import javax.swing.*;

public class DialogTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test Frame");
            frame.setSize(300, 200);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            // Test the dialog
            BasicCourseDialog dialog = new BasicCourseDialog(frame, "Test Course Dialog", null);
            System.out.println("Dialog should be visible now!");
        });
    }
}
