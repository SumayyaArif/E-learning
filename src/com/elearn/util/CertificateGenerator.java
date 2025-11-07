package com.elearn.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class CertificateGenerator {
    public static File generatePng(String studentName, String courseTitle, String outputDir) throws IOException {
        int width = 1000;
        int height = 700;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        try {
            // Enable anti-aliasing for better quality
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            // Background gradient
            GradientPaint gradient = new GradientPaint(0, 0, new Color(240, 248, 255), 0, height, new Color(230, 240, 250));
            g.setPaint(gradient);
            g.fillRect(0, 0, width, height);

            // Decorative border
            g.setColor(new Color(25, 118, 210));
            g.setStroke(new BasicStroke(8));
            g.drawRect(30, 30, width - 60, height - 60);
            
            // Inner border
            g.setColor(new Color(100, 181, 246));
            g.setStroke(new BasicStroke(3));
            g.drawRect(50, 50, width - 100, height - 100);

            // Decorative corner elements
            g.setColor(new Color(25, 118, 210));
            g.setStroke(new BasicStroke(4));
            int cornerSize = 40;
            g.drawLine(50, 50, 50 + cornerSize, 50);
            g.drawLine(50, 50, 50, 50 + cornerSize);
            g.drawLine(width - 50, 50, width - 50 - cornerSize, 50);
            g.drawLine(width - 50, 50, width - 50, 50 + cornerSize);
            g.drawLine(50, height - 50, 50 + cornerSize, height - 50);
            g.drawLine(50, height - 50, 50, height - 50 - cornerSize);
            g.drawLine(width - 50, height - 50, width - 50 - cornerSize, height - 50);
            g.drawLine(width - 50, height - 50, width - 50, height - 50 - cornerSize);

            // Main title
            g.setColor(new Color(25, 118, 210));
            g.setFont(new Font("Serif", Font.BOLD, 52));
            String title = "CERTIFICATE OF COMPLETION";
            drawCentered(g, title, width, 150);

            // Subtitle
            g.setColor(new Color(66, 66, 66));
            g.setFont(new Font("Serif", Font.PLAIN, 24));
            drawCentered(g, "This is to certify that", width, 220);

            // Student name with decorative underline
            g.setColor(new Color(25, 118, 210));
            g.setFont(new Font("Serif", Font.BOLD, 44));
            drawCentered(g, studentName, width, 280);
            
            // Decorative line under name
            g.setStroke(new BasicStroke(2));
            g.drawLine(width/2 - 150, 300, width/2 + 150, 300);

            // Course completion text
            g.setColor(new Color(66, 66, 66));
            g.setFont(new Font("Serif", Font.PLAIN, 26));
            drawCentered(g, "has successfully completed the course", width, 360);

            // Course title
            g.setColor(new Color(25, 118, 210));
            g.setFont(new Font("Serif", Font.BOLD, 32));
            drawCentered(g, '"' + courseTitle + '"', width, 420);

            // Date
            g.setColor(new Color(100, 100, 100));
            g.setFont(new Font("Serif", Font.PLAIN, 20));
            drawCentered(g, "Completed on: " + LocalDate.now(), width, 480);

            // Institution name
            g.setColor(new Color(25, 118, 210));
            g.setFont(new Font("Serif", Font.BOLD, 24));
            drawCentered(g, "E-Learning Management System", width, 540);

            // Bottom decorative line
            g.setColor(new Color(100, 181, 246));
            g.setStroke(new BasicStroke(2));
            g.drawLine(width/2 - 200, 580, width/2 + 200, 580);
            
        } finally {
            g.dispose();
        }
        File dir = new File(outputDir);
        if (!dir.exists()) dir.mkdirs();
        File out = new File(dir, studentName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + courseTitle.replaceAll("[^a-zA-Z0-9]", "_") + "_certificate.png");
        ImageIO.write(image, "png", out);
        return out;
    }

    private static void drawCentered(Graphics2D g, String text, int width, int y) {
        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }
}


