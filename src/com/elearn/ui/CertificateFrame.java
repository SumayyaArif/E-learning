package com.elearn.ui;

import com.elearn.dao.CertificateDAO;
import com.elearn.dao.CourseDAO;
import com.elearn.dao.impl.CertificateDAOImpl;
import com.elearn.dao.impl.CourseDAOImpl;
import com.elearn.model.Certificate;
import com.elearn.model.Course;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class CertificateFrame extends JFrame {
    private final int studentId;
    private final CertificateDAO certificateDAO = new CertificateDAOImpl();
    private final CourseDAO courseDAO = new CourseDAOImpl();
    private final DefaultListModel<Certificate> certificatesModel = new DefaultListModel<>();

    public CertificateFrame(int studentId) {
        this.studentId = studentId;
        setTitle("My Certificates");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        createUI();
        loadCertificates();
    }
    
    private void createUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton downloadBtn = new JButton("📥 Download Certificate");
        JButton refreshBtn = new JButton("🔄 Refresh");
        
        downloadBtn.addActionListener(e -> onDownloadCertificate());
        refreshBtn.addActionListener(e -> loadCertificates());
        
        topPanel.add(downloadBtn);
        topPanel.add(refreshBtn);
        
        // Center panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("My Certificates"));
        
        JList<Certificate> certificatesList = new JList<>(certificatesModel);
        certificatesList.setCellRenderer(new CertificateListRenderer());
        centerPanel.add(new JScrollPane(certificatesList), BorderLayout.CENTER);
        
        // Info panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Certificate Information"));
        JTextArea infoArea = new JTextArea(8, 50);
        infoArea.setEditable(false);
        infoArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoPanel.add(new JScrollPane(infoArea), BorderLayout.CENTER);
        
        // Store reference to info area
        this.infoArea = infoArea;
        
        // Split panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(centerPanel);
        splitPane.setBottomComponent(infoPanel);
        splitPane.setDividerLocation(300);
        
        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }
    
    private JTextArea infoArea;
    
    private void loadCertificates() {
        certificatesModel.clear();
        try {
            List<Certificate> certificates = certificateDAO.findByStudentId(studentId);
            for (Certificate certificate : certificates) {
                certificatesModel.addElement(certificate);
            }
            
            if (certificates.isEmpty()) {
                infoArea.setText("No certificates found. Complete courses with 60% or higher quiz scores to earn certificates.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading certificates: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void onDownloadCertificate() {
        Certificate selected = getSelectedCertificate();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a certificate to download", "No Certificate Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Get course information
            Course course = courseDAO.findById(selected.getCourseId());
            if (course == null) {
                JOptionPane.showMessageDialog(this, "Course information not found", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Look for certificate file
            String fileName = "Student_" + studentId + "_" + course.getTitle().replaceAll("[^a-zA-Z0-9]", "_") + "_certificate.png";
            File certificateFile = new File("certificates", fileName);
            
            if (!certificateFile.exists()) {
                // Try alternative naming pattern
                fileName = studentId + "_" + course.getCourseId() + "_certificate.png";
                certificateFile = new File("certificates", fileName);
            }
            
            if (certificateFile.exists()) {
                // Open file dialog to save
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new File(certificateFile.getName()));
                fileChooser.setDialogTitle("Save Certificate As");
                
                int result = fileChooser.showSaveDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File destination = fileChooser.getSelectedFile();
                    java.nio.file.Files.copy(certificateFile.toPath(), destination.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    JOptionPane.showMessageDialog(this, "Certificate downloaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Certificate file not found. It may not have been generated yet.", "File Not Found", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error downloading certificate: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Certificate getSelectedCertificate() {
        // Get the selected certificate from the certificates list
        JList<Certificate> certificatesList = (JList<Certificate>) ((JScrollPane) ((JPanel) ((JSplitPane) getContentPane().getComponent(1)).getTopComponent()).getComponent(0)).getViewport().getView();
        return certificatesList.getSelectedValue();
    }
    
    // Custom renderer
    private class CertificateListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Certificate) {
                Certificate certificate = (Certificate) value;
                try {
                    Course course = courseDAO.findById(certificate.getCourseId());
                    String courseTitle = course != null ? course.getTitle() : "Unknown Course";
                    setText("<html><b>🏆 " + courseTitle + "</b><br>" +
                           "Issued: " + certificate.getIssueDate() + "<br>" +
                           "Status: " + certificate.getStatus() + "</html>");
                } catch (Exception e) {
                    setText("Certificate " + certificate.getCertId());
                }
            }
            return this;
        }
    }
}
