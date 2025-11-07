package com.elearn.ui;

import com.elearn.dao.StudentDAO;
import com.elearn.dao.CourseDAO;
import com.elearn.dao.QuizDAO;
import com.elearn.dao.impl.StudentDAOImpl;
import com.elearn.dao.impl.CourseDAOImpl;
import com.elearn.dao.impl.QuizDAOImpl;
import com.elearn.model.Student;
import com.elearn.model.Course;
import com.elearn.model.QuizResult;
import com.elearn.model.Certificate;
import com.elearn.util.ModernTheme;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class CertificateGeneratorFrame extends JFrame implements Printable {
    private final Student student;
    private final Course course;
    private final QuizResult quizResult;
    private final StudentDAO studentDAO = new StudentDAOImpl();
    private final CourseDAO courseDAO = new CourseDAOImpl();
    private final QuizDAO quizDAO = new QuizDAOImpl();
    
    private JPanel certificatePanel;
    private Certificate certificate;

    public CertificateGeneratorFrame(int studentId, int courseId, int quizResultId) {
        try {
            this.student = studentDAO.findById(studentId);
            this.course = courseDAO.findById(courseId);
            this.quizResult = quizDAO.findResultById(quizResultId);
            
            if (student == null || course == null || quizResult == null) {
                throw new Exception("Could not find required data");
            }
            
            // Create certificate record
            certificate = new Certificate();
            certificate.setCertificateId(UUID.randomUUID().toString());
            certificate.setStudentId(studentId);
            certificate.setCourseId(courseId);
            certificate.setIssueDate(new Date());
            certificate.setQuizScore(quizResult.getScore());
            
            setTitle("Certificate of Completion");
            setSize(800, 600);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            ModernTheme.styleFrame(this);
            
            createUI();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error generating certificate: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            dispose();
        }
    }
    
    private void createUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Certificate panel
        certificatePanel = createCertificatePanel();
        JScrollPane scrollPane = new JScrollPane(certificatePanel);
        add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        JButton saveButton = ModernTheme.createModernButton("💾 Save Certificate");
        JButton printButton = ModernTheme.createSecondaryButton("🖨️ Print Certificate");
        JButton emailButton = ModernTheme.createSecondaryButton("📧 Email Certificate");
        JButton closeButton = ModernTheme.createSecondaryButton("❌ Close");
        
        saveButton.addActionListener(e -> saveCertificate());
        printButton.addActionListener(e -> printCertificate());
        emailButton.addActionListener(e -> emailCertificate());
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(printButton);
        buttonPanel.add(emailButton);
        buttonPanel.add(closeButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createCertificatePanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Set rendering hints for better quality
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                try {
                    // Try to load certificate background image - try SVG first, then PNG
                    ImageIcon bgIcon = new ImageIcon("c.svg");
                    if (bgIcon.getIconWidth() <= 0) {
                        bgIcon = new ImageIcon("c.png");
                    }
                    
                    if (bgIcon.getIconWidth() > 0) {
                        g2d.drawImage(bgIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
                    } else {
                        throw new Exception("Invalid image dimensions");
                    }
                } catch (Exception e) {
                    System.out.println("Error loading certificate background: " + e.getMessage());
                    // Fallback to drawing a default background
                    g2d.setColor(new Color(245, 245, 245));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(new Color(200, 160, 100));
                    g2d.drawRect(10, 10, getWidth() - 20, getHeight() - 20);
                    g2d.drawRect(15, 15, getWidth() - 30, getHeight() - 30);
                }
                
                // Draw certificate title
                g2d.setColor(new Color(70, 130, 180));
                g2d.setFont(new Font("Serif", Font.BOLD, 36));
                String title = "Certificate of Completion";
                FontMetrics fm = g2d.getFontMetrics();
                int titleWidth = fm.stringWidth(title);
                g2d.drawString(title, (getWidth() - titleWidth) / 2, 100);
                
                // Draw decorative line
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(getWidth() / 4, 120, getWidth() * 3 / 4, 120);
                
                // Draw certificate text
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 18));
                
                String text1 = "This is to certify that";
                fm = g2d.getFontMetrics();
                int text1Width = fm.stringWidth(text1);
                g2d.drawString(text1, (getWidth() - text1Width) / 2, 170);
                
                // Student name
                g2d.setFont(new Font("Serif", Font.BOLD, 28));
                String studentName = student.getName();
                fm = g2d.getFontMetrics();
                int nameWidth = fm.stringWidth(studentName);
                g2d.drawString(studentName, (getWidth() - nameWidth) / 2, 220);
                
                // Draw more text
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 18));
                String text2 = "has successfully completed the course";
                fm = g2d.getFontMetrics();
                int text2Width = fm.stringWidth(text2);
                g2d.drawString(text2, (getWidth() - text2Width) / 2, 270);
                
                // Course name
                g2d.setFont(new Font("Serif", Font.BOLD, 24));
                String courseName = course.getTitle();
                fm = g2d.getFontMetrics();
                int courseWidth = fm.stringWidth(courseName);
                g2d.drawString(courseName, (getWidth() - courseWidth) / 2, 320);
                
                // Score
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 18));
                String scoreText = "with a score of " + quizResult.getScore() + "%";
                fm = g2d.getFontMetrics();
                int scoreWidth = fm.stringWidth(scoreText);
                g2d.drawString(scoreText, (getWidth() - scoreWidth) / 2, 360);
                
                // Date
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
                String dateText = "Issued on " + dateFormat.format(certificate.getIssueDate());
                fm = g2d.getFontMetrics();
                int dateWidth = fm.stringWidth(dateText);
                g2d.drawString(dateText, (getWidth() - dateWidth) / 2, 400);
                
                // Certificate ID - removed as requested
                // No certificate information displayed
                
                g2d.dispose();
            }
        };
        
        panel.setPreferredSize(new Dimension(750, 550));
        return panel;
    }
    
    private void saveCertificate() {
        try {
            // Create certificates directory if it doesn't exist
            File certificatesDir = new File("certificates");
            if (!certificatesDir.exists()) {
                certificatesDir.mkdir();
            }
            
            // Create a BufferedImage from the certificate panel
            BufferedImage image = new BufferedImage(
                certificatePanel.getWidth(), 
                certificatePanel.getHeight(), 
                BufferedImage.TYPE_INT_RGB
            );
            Graphics2D g2d = image.createGraphics();
            certificatePanel.paint(g2d);
            g2d.dispose();
            
            // Save the image to a file - use simple naming to avoid errors
            String fileName = "certificates/certificate_" + System.currentTimeMillis() + ".png";
            File outputFile = new File(fileName);
            ImageIO.write(image, "png", outputFile);
            
            JOptionPane.showMessageDialog(
                this, 
                "Certificate saved successfully to: " + outputFile.getAbsolutePath(),
                "Certificate Saved", 
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                this, 
                "Error saving certificate: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private void printCertificate() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        
        if (job.printDialog()) {
            try {
                job.print();
                JOptionPane.showMessageDialog(this, "Certificate sent to printer", "Print", JOptionPane.INFORMATION_MESSAGE);
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this, "Error printing: " + e.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void emailCertificate() {
        JOptionPane.showMessageDialog(this, 
            "Certificate would be emailed to: " + student.getEmail() + "\n\n" +
            "Email functionality would be implemented here.",
            "Email Certificate", JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }
        
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        
        // Scale to fit the page
        double scaleX = pageFormat.getImageableWidth() / certificatePanel.getWidth();
        double scaleY = pageFormat.getImageableHeight() / certificatePanel.getHeight();
        double scale = Math.min(scaleX, scaleY);
        
        g2d.scale(scale, scale);
        
        certificatePanel.print(g2d);
        
        return PAGE_EXISTS;
    }
}