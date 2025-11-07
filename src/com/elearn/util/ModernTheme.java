package com.elearn.util;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Enumeration;

public class ModernTheme {
    
    // Modern color palette - Mushroom and White theme
    public static final Color PRIMARY_COLOR = new Color(139, 69, 19); // Saddle Brown (Mushroom)
    public static final Color SECONDARY_COLOR = new Color(160, 82, 45); // Saddle Brown (Lighter)
    public static final Color ACCENT_COLOR = new Color(210, 180, 140); // Tan
    public static final Color BACKGROUND_COLOR = new Color(255, 255, 255); // White
    public static final Color SURFACE_COLOR = new Color(248, 248, 248); // Light Gray
    public static final Color TEXT_PRIMARY = new Color(33, 33, 33); // Dark Gray
    public static final Color TEXT_SECONDARY = new Color(117, 117, 117); // Medium Gray
    public static final Color SUCCESS_COLOR = new Color(76, 175, 80); // Green
    public static final Color WARNING_COLOR = new Color(255, 152, 0); // Orange
    public static final Color ERROR_COLOR = new Color(244, 67, 54); // Red
    public static final Color INFO_COLOR = new Color(33, 150, 243); // Blue
    
    // Modern fonts
    public static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font SUBHEADING_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    
    public static void applyModernTheme() {
        try {
            // Set system look and feel
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            
            // Apply custom colors and fonts
            UIManager.put("Panel.background", BACKGROUND_COLOR);
            UIManager.put("Button.background", PRIMARY_COLOR);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.font", BUTTON_FONT);
            UIManager.put("Button.border", BorderFactory.createEmptyBorder(12, 24, 12, 24));
            UIManager.put("Button.focus", new ColorUIResource(ACCENT_COLOR));
            
            UIManager.put("TextField.background", SURFACE_COLOR);
            UIManager.put("TextField.foreground", TEXT_PRIMARY);
            UIManager.put("TextField.font", BODY_FONT);
            UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            
            UIManager.put("TextArea.background", SURFACE_COLOR);
            UIManager.put("TextArea.foreground", TEXT_PRIMARY);
            UIManager.put("TextArea.font", BODY_FONT);
            UIManager.put("TextArea.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            
            UIManager.put("Label.foreground", TEXT_PRIMARY);
            UIManager.put("Label.font", BODY_FONT);
            
            UIManager.put("List.background", SURFACE_COLOR);
            UIManager.put("List.foreground", TEXT_PRIMARY);
            UIManager.put("List.font", BODY_FONT);
            UIManager.put("List.selectionBackground", ACCENT_COLOR);
            UIManager.put("List.selectionForeground", TEXT_PRIMARY);
            
            UIManager.put("ScrollPane.background", BACKGROUND_COLOR);
            UIManager.put("ScrollPane.border", BorderFactory.createLineBorder(ACCENT_COLOR, 1));
            
            UIManager.put("TitledBorder.titleColor", PRIMARY_COLOR);
            UIManager.put("TitledBorder.font", SUBHEADING_FONT);
            
            UIManager.put("OptionPane.background", BACKGROUND_COLOR);
            UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
            UIManager.put("OptionPane.messageFont", BODY_FONT);
            
            UIManager.put("Dialog.background", BACKGROUND_COLOR);
            UIManager.put("Frame.background", BACKGROUND_COLOR);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static JButton createModernButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(BUTTON_FONT);
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(SECONDARY_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
        
        return button;
    }
    
    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(ACCENT_COLOR);
        button.setForeground(TEXT_PRIMARY);
        button.setFont(BUTTON_FONT);
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(ACCENT_COLOR.getRed() + 20, ACCENT_COLOR.getGreen() + 20, ACCENT_COLOR.getBlue() + 20));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT_COLOR);
            }
        });
        
        return button;
    }
    
    public static JTextField createModernTextField() {
        JTextField field = new JTextField();
        field.setBackground(SURFACE_COLOR);
        field.setForeground(TEXT_PRIMARY);
        field.setFont(BODY_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }
    
    public static JTextArea createModernTextArea() {
        JTextArea area = new JTextArea();
        area.setBackground(SURFACE_COLOR);
        area.setForeground(TEXT_PRIMARY);
        area.setFont(BODY_FONT);
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }
    
    public static JLabel createHeadingLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(HEADING_FONT);
        label.setForeground(PRIMARY_COLOR);
        return label;
    }
    
    public static JLabel createSubheadingLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(SUBHEADING_FONT);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }
    
    public static JLabel createBodyLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BODY_FONT);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }
    
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(SURFACE_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }
    
    public static void styleFrame(JFrame frame) {
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
        frame.setIconImage(createAppIcon());
    }
    
    public static void styleDialog(JDialog dialog) {
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);
    }
    
    private static Image createAppIcon() {
        // Create a simple app icon
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = icon.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw a simple book icon
        g.setColor(PRIMARY_COLOR);
        g.fillRoundRect(4, 8, 24, 20, 4, 4);
        g.setColor(Color.WHITE);
        g.fillRoundRect(6, 10, 20, 16, 2, 2);
        g.setColor(PRIMARY_COLOR);
        g.drawLine(10, 12, 22, 12);
        g.drawLine(10, 16, 22, 16);
        g.drawLine(10, 20, 22, 20);
        
        g.dispose();
        return icon;
    }
}
