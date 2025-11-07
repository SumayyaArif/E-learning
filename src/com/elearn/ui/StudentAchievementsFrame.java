package com.elearn.ui;

import com.elearn.dao.CertificateDAO;
import com.elearn.dao.QuizDAO;
import com.elearn.dao.impl.CertificateDAOImpl;
import com.elearn.dao.impl.QuizDAOImpl;
import com.elearn.model.Certificate;
import com.elearn.model.QuizResult;
import com.elearn.model.Student;
import com.elearn.util.ModernTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class StudentAchievementsFrame extends JFrame {
    private final Student student;
    private final QuizDAO quizDAO = new QuizDAOImpl();
    private final CertificateDAO certificateDAO = new CertificateDAOImpl();
    
    public StudentAchievementsFrame(Student student) {
        this.student = student;
        setTitle("Achievements - " + student.getName());
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ModernTheme.styleFrame(this);
        
        createUI();
    }
    
    private void createUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = ModernTheme.createHeadingLabel("Your Achievements");
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setOpaque(false);
        
        // Trophy panel
        JPanel trophyPanel = createTrophyPanel();
        contentPanel.add(trophyPanel, BorderLayout.NORTH);
        
        // Certificates panel
        JPanel certificatesPanel = createCertificatesPanel();
        contentPanel.add(certificatesPanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        JButton closeButton = ModernTheme.createSecondaryButton("Close");
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createTrophyPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Your Trophies"));
        panel.setOpaque(false);
        
        JPanel trophiesContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        trophiesContainer.setOpaque(false);
        
        // Get quiz results to determine trophies
        List<QuizResult> results = quizDAO.getQuizResultsByStudentId(student.getId());
        
        // Set trophy icon directly
        ImageIcon trophyIcon = new ImageIcon(getClass().getResource("/com/elearn/resources/trophy.png"));
        if (trophyIcon.getIconWidth() <= 0) {
            // If resource loading fails, try direct file
            trophyIcon = new ImageIcon("t.png");
            if (trophyIcon.getIconWidth() <= 0) {
                // If all fails, use fallback
                trophyIcon = createFallbackTrophyIcon(80, 80);
            }
        }
        
        // Add trophies based on achievements
        int passedQuizzes = 0;
        for (QuizResult result : results) {
            if (result.isPassed()) {
                passedQuizzes++;
            }
        }
        
        if (passedQuizzes > 0) {
            for (int i = 0; i < passedQuizzes; i++) {
                JPanel trophyItemPanel = new JPanel(new BorderLayout(5, 5));
                trophyItemPanel.setOpaque(false);
                
                JLabel trophyLabel = new JLabel(trophyIcon);
                trophyItemPanel.add(trophyLabel, BorderLayout.CENTER);
                
                JLabel nameLabel = new JLabel(student.getName(), JLabel.CENTER);
                nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
                trophyItemPanel.add(nameLabel, BorderLayout.SOUTH);
                
                trophiesContainer.add(trophyItemPanel);
            }
        } else {
            JLabel noTrophiesLabel = new JLabel("Complete quizzes to earn trophies!");
            noTrophiesLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            trophiesContainer.add(noTrophiesLabel);
        }
        
        panel.add(trophiesContainer, BorderLayout.CENTER);
        return panel;
    }
    
    // Create a simple trophy icon as fallback
    private ImageIcon createFallbackTrophyIcon(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Set rendering hints
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw trophy cup
        g2d.setColor(new Color(255, 215, 0)); // Gold color
        g2d.fillOval(10, 5, width - 20, height/3); // Trophy cup top
        g2d.fillRect(20, height/3, width - 40, height/3); // Trophy cup body
        
        // Draw base
        g2d.setColor(new Color(139, 69, 19)); // Brown color
        g2d.fillRect(width/3, 2*height/3, width/3, height/6); // Trophy base top
        g2d.fillRect(width/4, 5*height/6, width/2, height/6); // Trophy base bottom
        
        g2d.dispose();
        return new ImageIcon(image);
    }
    private JPanel createCertificatesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Your Certificates"));
        panel.setOpaque(false);
        
        // Get certificates
        List<Certificate> certificates = certificateDAO.findByStudentId(student.getId());
        
        if (certificates.isEmpty()) {
            JLabel noCertificatesLabel = new JLabel("You haven't earned any certificates yet. Pass quizzes to earn certificates!");
            noCertificatesLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            noCertificatesLabel.setHorizontalAlignment(JLabel.CENTER);
            panel.add(noCertificatesLabel, BorderLayout.CENTER);
        } else {
            JPanel certificatesContainer = new JPanel();
            certificatesContainer.setLayout(new BoxLayout(certificatesContainer, BoxLayout.Y_AXIS));
            certificatesContainer.setOpaque(false);
            
            for (Certificate certificate : certificates) {
                JPanel certPanel = new JPanel(new BorderLayout(5, 5));
                certPanel.setBorder(BorderFactory.createLineBorder(ModernTheme.ACCENT_COLOR, 1));
                certPanel.setOpaque(true);
                certPanel.setBackground(new Color(245, 245, 250));
                
                JLabel courseLabel = new JLabel("Course: " + certificate.getCourseName());
                courseLabel.setFont(new Font("Arial", Font.BOLD, 14));
                
                JLabel dateLabel = new JLabel("Issued: " + certificate.getFormattedIssueDate());
                JLabel scoreLabel = new JLabel("Score: " + certificate.getQuizScore() + "%");
                
                JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
                infoPanel.setOpaque(false);
                infoPanel.add(courseLabel);
                infoPanel.add(dateLabel);
                infoPanel.add(scoreLabel);
                
                certPanel.add(infoPanel, BorderLayout.CENTER);
                
                JButton viewButton = ModernTheme.createModernButton("View Certificate");
                viewButton.addActionListener(e -> {
                    new CertificateGeneratorFrame(student.getId(), certificate.getCourseId(), 0).setVisible(true);
                });
                
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonPanel.setOpaque(false);
                buttonPanel.add(viewButton);
                
                certPanel.add(buttonPanel, BorderLayout.SOUTH);
                
                certificatesContainer.add(certPanel);
                certificatesContainer.add(Box.createVerticalStrut(10));
            }
            
            JScrollPane scrollPane = new JScrollPane(certificatesContainer);
            scrollPane.setBorder(null);
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
            
            panel.add(scrollPane, BorderLayout.CENTER);
        }
        
        return panel;
    }
}