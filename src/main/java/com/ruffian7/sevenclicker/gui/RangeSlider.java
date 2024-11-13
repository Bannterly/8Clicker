package com.ruffian7.sevenclicker.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import com.ruffian7.sevenclicker.AutoClicker;

public class RangeSlider extends JPanel {
    private static final long serialVersionUID = 1L;
    public int sliderVal1 = 7;
    public int sliderVal2 = 11;

    // Constants for customization
    private static final int PANEL_WIDTH = 260;
    private static final int PANEL_HEIGHT = 30;
    private static final int SLIDER_HEIGHT = 8;
    private static final int THUMB_SIZE = 16;
    private static final int SLIDER_Y_OFFSET = (PANEL_HEIGHT - SLIDER_HEIGHT) / 2;
    
    // Colors
    private static final Color SLIDER_BACKGROUND = new Color(45, 47, 49);
    private static final Color SLIDER_FILL = new Color(35, 168, 105);
    private static final Color THUMB_COLOR = new Color(240, 240, 240);
    private static final Color THUMB_BORDER = new Color(35, 168, 105);
    
    Rectangle2D.Double sliderBody = new Rectangle2D.Double(0, SLIDER_Y_OFFSET, PANEL_WIDTH, SLIDER_HEIGHT);
    Ellipse2D.Double sliderThumb1 = new Ellipse2D.Double((sliderVal1 / 20.0f) * PANEL_WIDTH, 
            (PANEL_HEIGHT - THUMB_SIZE) / 2, THUMB_SIZE, THUMB_SIZE);
    Ellipse2D.Double sliderThumb2 = new Ellipse2D.Double((sliderVal2 / 20.0f) * PANEL_WIDTH, 
            (PANEL_HEIGHT - THUMB_SIZE) / 2, THUMB_SIZE, THUMB_SIZE);
    Rectangle2D.Double sliderRange = new Rectangle2D.Double((sliderVal1 / 20.0f) * PANEL_WIDTH + THUMB_SIZE/2, 
            SLIDER_Y_OFFSET, ((sliderVal2 - sliderVal1) / 20.0f) * PANEL_WIDTH, SLIDER_HEIGHT);

    public RangeSlider(JPanel panel, int x, int y) {
        setLayout(null);
        setBounds(x, y, PANEL_WIDTH, PANEL_HEIGHT);
        setBackground(new Color(60, 70, 73));
        setOpaque(false);

        MouseAdapter dragListener = new MouseAdapter() {
            private boolean thumbPressed1 = false;
            private boolean thumbPressed2 = false;
            private int dragOffsetX = 0;

            @Override
            public void mousePressed(MouseEvent e) {
                if (sliderThumb1.getBounds().contains(e.getPoint())) {
                    thumbPressed1 = true;
                    dragOffsetX = e.getX() - (int)sliderThumb1.x;
                } else if (sliderThumb2.getBounds().contains(e.getPoint())) {
                    thumbPressed2 = true;
                    dragOffsetX = e.getX() - (int)sliderThumb2.x;
                }
            }


            @Override
            public void mouseReleased(MouseEvent e) {
                thumbPressed1 = thumbPressed2 = false;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (thumbPressed1 || thumbPressed2) {
                    double newX = e.getX() - dragOffsetX;
                    newX = Math.max(0, Math.min(newX, PANEL_WIDTH - THUMB_SIZE));
                    
                    if (thumbPressed1) {
                        sliderThumb1.x = newX;
                        sliderVal1 = (int) Math.round((newX / (PANEL_WIDTH - THUMB_SIZE)) * 20);
                    } else {
                        sliderThumb2.x = newX;
                        sliderVal2 = (int) Math.round((newX / (PANEL_WIDTH - THUMB_SIZE)) * 20);
                    }

                    // Update range rectangle
                    double minX = Math.min(sliderThumb1.x, sliderThumb2.x);
                    double maxX = Math.max(sliderThumb1.x, sliderThumb2.x);
                    sliderRange.x = minX + THUMB_SIZE/2;
                    sliderRange.width = maxX - minX;

                    // Update AutoClicker values
                    AutoClicker.minCPS = Math.min(sliderVal1, sliderVal2) + 1;
                    AutoClicker.maxCPS = Math.max(sliderVal1, sliderVal2) + 1;
                    AutoClicker.gui.minCPSField.setText(String.valueOf(AutoClicker.minCPS));
                    AutoClicker.gui.maxCPSField.setText(String.valueOf(AutoClicker.maxCPS));
                    
                    repaint();
                }
            }
        };

        addMouseListener(dragListener);
        addMouseMotionListener(dragListener);
        panel.add(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw slider background
        g2d.setColor(SLIDER_BACKGROUND);
        g2d.fill(sliderBody);

        // Draw selected range
        g2d.setColor(SLIDER_FILL);
        g2d.fill(sliderRange);

        // Draw thumbs with border
        g2d.setColor(THUMB_COLOR);
        g2d.fill(sliderThumb1);
        g2d.fill(sliderThumb2);
        
        g2d.setColor(THUMB_BORDER);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.draw(sliderThumb1);
        g2d.draw(sliderThumb2);
    }
}