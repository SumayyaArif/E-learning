package com.elearn.ui;

import javax.swing.*;

/**
 * Video player functionality removed — stub class kept to avoid compile errors.
 * All video support has been disabled and related UI removed.
 */
public class VideoPlayerFrame extends JFrame {
    public VideoPlayerFrame() {
        super("Video Player (removed)");
        JLabel label = new JLabel("Video support has been removed from this application.", SwingConstants.CENTER);
        add(label);
        setSize(400, 120);
        setLocationRelativeTo(null);
    }
}