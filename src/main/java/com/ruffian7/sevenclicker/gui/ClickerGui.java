package com.ruffian7.sevenclicker.gui;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import com.ruffian7.sevenclicker.AutoClicker;

public class ClickerGui {


    private final String BASE_TITLE = "8Clicker";
    private final String ACTIVE_INDICATOR = " [ON]";

    private final int WINDOW_WIDTH = 300;
    private final int WINDOW_HEIGHT = 360;

    private final Color LIGHT_GRAY = new Color(60, 70, 73);
    private final Color DARK_GRAY = new Color(45, 47, 49);

    public JFrame frame = new JFrame("8Clicker");

    public JPanel mainPane = new JPanel(null);
    public JPanel titleBar = new JPanel(null);

    public JLabel titleText = new JLabel("8Clicker");
    public JLabel leftCpsRange = new JLabel("Left CPS Range");
    public JLabel rightCpsRange = new JLabel("Right CPS Range");
    public JLabel toggleKeyText = new JLabel("Toggle Button");

    public JTextField minCPSField = new JTextField("8", 2);
    public JTextField maxCPSField = new JTextField("12", 2);
    public JTextField rightMinCPSField = new JTextField("8", 2);
    public JTextField rightMaxCPSField = new JTextField("12", 2);
    public JTextField toggleKeyField = new JTextField("Enter Keybind");

    public JCheckBox overlayBox = new JCheckBox("Overlay", true);
    public JCheckBox rightClickBox = new JCheckBox("Right Click", false);
    public JCheckBox minecraftOnlyBox = new JCheckBox("Minecraft Only", true);
    public JCheckBox randomizerBox = new JCheckBox("Randomizer", false);
    public JCheckBox resetDefaultsBox = new JCheckBox("Reset to Default", false);
    public JCheckBox rightEnabledBox = new JCheckBox("Enable Right", false);

    public RangeSlider leftSlider = new RangeSlider(mainPane, 20, 65);
    public RangeSlider rightSlider = new RangeSlider(mainPane, 20, 240);  // Positioned above left slider

    public boolean focused = false;

    public ClickerGui() {
        setupFrame();
        setupMainPane();
        setupTitleBar();
        setupSettings();
        setupMisc();
    }
    // ... (previous code remains the same until setupSettings())

    private void setupSettings() {
        // Left CPS Range Label
        leftCpsRange.setBounds(0, 45, WINDOW_WIDTH, 13);
        leftCpsRange.setHorizontalAlignment(SwingConstants.CENTER);
        leftCpsRange.setForeground(Color.WHITE);
        mainPane.add(leftCpsRange);

        // Right CPS Range Label
        rightCpsRange.setBounds(0, 35, WINDOW_WIDTH, 397);
        rightCpsRange.setHorizontalAlignment(SwingConstants.CENTER);
        rightCpsRange.setForeground(Color.WHITE);
        mainPane.add(rightCpsRange);

        // Left CPS Fields
        minCPSField.setBounds(20, 90, 40, 30);
        minCPSField.setHorizontalAlignment(SwingConstants.CENTER);
        minCPSField.setBackground(DARK_GRAY);
        minCPSField.setForeground(Color.WHITE);
        minCPSField.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        maxCPSField.setBounds(WINDOW_WIDTH - 60, 90, 40, 30);
        maxCPSField.setHorizontalAlignment(SwingConstants.CENTER);
        maxCPSField.setBackground(DARK_GRAY);
        maxCPSField.setForeground(Color.WHITE);
        maxCPSField.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        // Right CPS Fields
        rightMinCPSField.setBounds(20, 266, 40, 30);
        rightMinCPSField.setHorizontalAlignment(SwingConstants.CENTER);
        rightMinCPSField.setBackground(DARK_GRAY);
        rightMinCPSField.setForeground(Color.WHITE);
        rightMinCPSField.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        rightMaxCPSField.setBounds(WINDOW_WIDTH - 60, 266, 40, 30);
        rightMaxCPSField.setHorizontalAlignment(SwingConstants.CENTER);
        rightMaxCPSField.setBackground(DARK_GRAY);
        rightMaxCPSField.setForeground(Color.WHITE);
        rightMaxCPSField.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        // Add listeners for left CPS fields
        setupCPSFieldListeners(minCPSField, true, false);
        setupCPSFieldListeners(maxCPSField, false, false);

        // Add listeners for right CPS fields
        setupCPSFieldListeners(rightMinCPSField, true, true);
        setupCPSFieldListeners(rightMaxCPSField, false, true);

        mainPane.add(minCPSField);
        mainPane.add(maxCPSField);
        mainPane.add(rightMinCPSField);
        mainPane.add(rightMaxCPSField);

        // Setup checkboxes
        setupCheckboxes();

        // Setup toggle key field
        setupToggleKeyField();
    }

    public void updateTitle(boolean isActive) {
        titleText.setText(BASE_TITLE + (isActive ? ACTIVE_INDICATOR : ""));
    }


    private void setupCPSFieldListeners(JTextField field, boolean isMin, boolean isRight) {
        field.addActionListener(e -> {
            textFieldSetCPS(isMin, isRight);
            saveSettings();
        });

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                textFieldSetCPS(isMin, isRight);
                saveSettings();
            }
        });
    }

    private void setupCheckboxes() {
        // Overlay checkbox
        overlayBox.setBounds(20, 125, 120, 20);
        overlayBox.setBackground(LIGHT_GRAY);
        overlayBox.setForeground(Color.WHITE);
        setupCheckboxIcon(overlayBox);
        overlayBox.addActionListener(e -> frame.setAlwaysOnTop(overlayBox.isSelected()));
        mainPane.add(overlayBox);

        // Right click enable checkbox
        rightEnabledBox.setBounds(20, 300, 120, 20);
        rightEnabledBox.setBackground(LIGHT_GRAY);
        rightEnabledBox.setForeground(Color.WHITE);
        setupCheckboxIcon(rightEnabledBox);
        rightEnabledBox.addActionListener(e -> {
            AutoClicker.rightEnabled = rightEnabledBox.isSelected();
            saveSettings();
        });
        mainPane.add(rightEnabledBox);

        // Minecraft only checkbox
        minecraftOnlyBox.setBounds(20, 155, 120, 20);
        minecraftOnlyBox.setBackground(LIGHT_GRAY);
        minecraftOnlyBox.setForeground(Color.WHITE);
        setupCheckboxIcon(minecraftOnlyBox);
        minecraftOnlyBox.addActionListener(e -> {
            AutoClicker.minecraftOnly = minecraftOnlyBox.isSelected();
            saveSettings();
        });
        mainPane.add(minecraftOnlyBox);

        // Randomizer checkbox
        randomizerBox.setBounds(160, 125, 120, 20);
        randomizerBox.setBackground(LIGHT_GRAY);
        randomizerBox.setForeground(Color.WHITE);
        setupCheckboxIcon(randomizerBox);
        randomizerBox.addActionListener(e -> {
            AutoClicker.randomizer = randomizerBox.isSelected();
            saveSettings();
        });
        mainPane.add(randomizerBox);

        // Reset defaults checkbox
        resetDefaultsBox.setBounds(160, 155, 120, 20);
        resetDefaultsBox.setBackground(LIGHT_GRAY);
        resetDefaultsBox.setForeground(Color.WHITE);
        setupCheckboxIcon(resetDefaultsBox);
        resetDefaultsBox.addActionListener(e -> {
            if (resetDefaultsBox.isSelected()) {
                resetToDefaults();
                resetDefaultsBox.setSelected(false);
            }
        });
        mainPane.add(resetDefaultsBox);
    }

    private void setupCheckboxIcon(JCheckBox checkbox) {
        checkbox.setIcon(new ImageIcon(AutoClicker.class.getClassLoader().getResource("assets/checkbox_unchecked.png")));
        checkbox.setSelectedIcon(new ImageIcon(AutoClicker.class.getClassLoader().getResource("assets/checkbox_checked.png")));
    }

    private void resetToDefaults() {
        AutoClicker.minCPS = 8;
        AutoClicker.maxCPS = 12;
        AutoClicker.rightMinCPS = 8;
        AutoClicker.rightMaxCPS = 12;
        AutoClicker.button = 1;
        AutoClicker.minecraftOnly = true;
        AutoClicker.randomizer = false;
        AutoClicker.rightEnabled = false;
        AutoClicker.toggleKey[0] = "";
        AutoClicker.toggleKey[1] = "";
        AutoClicker.toggleMouseButton = 3;

        updateFromConfig();
        saveSettings();
    }

    private void textFieldSetCPS(boolean isMin, boolean isRight) {
        JTextField textField = isRight ?
                (isMin ? rightMinCPSField : rightMaxCPSField) :
                (isMin ? minCPSField : maxCPSField);
        RangeSlider slider = isRight ? rightSlider : leftSlider;

        if (textField.getText().matches("^\\d+$")) {
            int cpsFieldVal = Integer.parseInt(textField.getText());
            int currentMin = isRight ? AutoClicker.rightMinCPS : AutoClicker.minCPS;
            int currentMax = isRight ? AutoClicker.rightMaxCPS : AutoClicker.maxCPS;

            if ((isMin && cpsFieldVal >= 1 && cpsFieldVal <= currentMax) ||
                    (!isMin && cpsFieldVal >= currentMin && cpsFieldVal <= 99)) {

                if ((isMin && slider.sliderVal1 <= slider.sliderVal2) ||
                        (!isMin && slider.sliderVal1 > slider.sliderVal2)) {
                    slider.sliderVal1 = (cpsFieldVal > 20) ? 19 : cpsFieldVal - 1;
                    slider.sliderThumb1.x = (slider.sliderVal1 / 20.0f) * 260;
                } else {
                    slider.sliderVal2 = (cpsFieldVal > 20) ? 19 : cpsFieldVal - 1;
                    slider.sliderThumb2.x = (slider.sliderVal2 / 20.0f) * 260;
                }

                slider.sliderRange.x = Math.min(slider.sliderThumb1.x, slider.sliderThumb2.x) + 10;
                slider.sliderRange.width = Math.max(slider.sliderThumb1.x, slider.sliderThumb2.x) -
                        Math.min(slider.sliderThumb1.x, slider.sliderThumb2.x);

                textField.setText(textField.getText().replaceFirst("^0*", ""));
                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();

                if (isRight) {
                    if (isMin) AutoClicker.rightMinCPS = cpsFieldVal;
                    else AutoClicker.rightMaxCPS = cpsFieldVal;
                } else {
                    if (isMin) AutoClicker.minCPS = cpsFieldVal;
                    else AutoClicker.maxCPS = cpsFieldVal;
                }

                slider.repaint();
            } else {
                if (isRight) {
                    textField.setText(String.valueOf(isMin ? AutoClicker.rightMinCPS : AutoClicker.rightMaxCPS));
                } else {
                    textField.setText(String.valueOf(isMin ? AutoClicker.minCPS : AutoClicker.maxCPS));
                }
            }
        }
    }

    public void updateFromConfig() {
        // Update left click settings
        minCPSField.setText(String.valueOf(AutoClicker.minCPS));
        maxCPSField.setText(String.valueOf(AutoClicker.maxCPS));

        // Update right click settings
        rightMinCPSField.setText(String.valueOf(AutoClicker.rightMinCPS));
        rightMaxCPSField.setText(String.valueOf(AutoClicker.rightMaxCPS));
        rightEnabledBox.setSelected(AutoClicker.rightEnabled);

        // Update other settings
        minecraftOnlyBox.setSelected(AutoClicker.minecraftOnly);
        randomizerBox.setSelected(AutoClicker.randomizer);

        // Update left slider
        leftSlider.sliderVal1 = AutoClicker.minCPS - 1;
        leftSlider.sliderVal2 = AutoClicker.maxCPS - 1;
        leftSlider.sliderThumb1.x = (leftSlider.sliderVal1 / 20.0f) * 260;
        leftSlider.sliderThumb2.x = (leftSlider.sliderVal2 / 20.0f) * 260;
        leftSlider.sliderRange.x = Math.min(leftSlider.sliderThumb1.x, leftSlider.sliderThumb2.x) + 10;
        leftSlider.sliderRange.width = Math.max(leftSlider.sliderThumb1.x, leftSlider.sliderThumb2.x) -
                Math.min(leftSlider.sliderThumb1.x, leftSlider.sliderThumb2.x);

        // Update right slider
        rightSlider.sliderVal1 = AutoClicker.rightMinCPS - 1;
        rightSlider.sliderVal2 = AutoClicker.rightMaxCPS - 1;
        rightSlider.sliderThumb1.x = (rightSlider.sliderVal1 / 20.0f) * 260;
        rightSlider.sliderThumb2.x = (rightSlider.sliderVal2 / 20.0f) * 260;
        rightSlider.sliderRange.x = Math.min(rightSlider.sliderThumb1.x, rightSlider.sliderThumb2.x) + 10;
        rightSlider.sliderRange.width = Math.max(rightSlider.sliderThumb1.x, rightSlider.sliderThumb2.x) -
                Math.min(rightSlider.sliderThumb1.x, rightSlider.sliderThumb2.x);

        // Repaint sliders
        leftSlider.repaint();
        rightSlider.repaint();

        // Update toggle key field
        if (AutoClicker.toggleMouseButton > 0) {
            toggleKeyField.setText("Mouse " + AutoClicker.toggleMouseButton);
        } else if (!AutoClicker.toggleKey[0].isEmpty()) {
            StringBuilder keyText = new StringBuilder();
            if (!AutoClicker.toggleKey[1].isEmpty()) {
                keyText.append(AutoClicker.toggleKey[1]);
            }
            keyText.append(AutoClicker.toggleKey[0]);
            toggleKeyField.setText(keyText.toString());
        } else {
            toggleKeyField.setText("Enter Keybind");
        }
    }

    private void saveSettings() {
        AutoClicker.configManager.saveConfig(
                AutoClicker.toggleKey[0],
                AutoClicker.toggleKey[1],
                AutoClicker.toggleMouseButton,
                AutoClicker.minCPS,
                AutoClicker.maxCPS,
                AutoClicker.rightMinCPS,
                AutoClicker.rightMaxCPS,
                AutoClicker.button,
                AutoClicker.minecraftOnly,
                AutoClicker.randomizer,
                AutoClicker.rightEnabled
        );
    }

    private void setupFrame() {
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setLocation(50, 50);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        frame.setBackground(new Color(0, 0, 0, 0));
        frame.setAlwaysOnTop(true);
        frame.setResizable(false);

        ImageIcon icon = new ImageIcon(AutoClicker.class.getClassLoader().getResource("assets/7Clicker.png"));
        frame.setIconImage(icon.getImage());

        frame.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                frame.requestFocusInWindow();
                focused = true;
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                frame.requestFocusInWindow();
                focused = false;
            }
        });

        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            public void eventDispatched(AWTEvent event) {
                if (event.getID() == MouseEvent.MOUSE_CLICKED) {
                    if (!(((MouseEvent) event).getSource() instanceof JTextField)) {
                        KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
                    }
                }
            }
        }, AWTEvent.MOUSE_EVENT_MASK);
    }

    private void setupMainPane() {
        mainPane.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        mainPane.setBackground(LIGHT_GRAY);
        mainPane.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, DARK_GRAY));
    }

    private void setupTitleBar() {
        MouseAdapter dragListener = new MouseAdapter() {
            private int pX, pY;

            @Override
            public void mousePressed(MouseEvent e) {
                pX = e.getX();
                pY = e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                frame.setLocation(frame.getLocation().x + e.getX() - pX, frame.getLocation().y + e.getY() - pY);
            }
        };

        titleBar.setBounds(0, 0, WINDOW_WIDTH, 30);
        titleBar.setBackground(DARK_GRAY);
        titleBar.addMouseListener(dragListener);
        titleBar.addMouseMotionListener(dragListener);

        titleText.setBounds(0, 0, WINDOW_WIDTH, 30);
        titleText.setHorizontalAlignment(SwingConstants.CENTER);
        titleText.setForeground(Color.WHITE);
        titleText.setText(BASE_TITLE); // Update
        titleBar.add(titleText);
    }

    private void setupToggleKeyField() {
        toggleKeyText.setBounds(20, 190, 120, 25);
        toggleKeyText.setForeground(Color.WHITE);
        mainPane.add(toggleKeyText);

        toggleKeyField.setBounds(WINDOW_WIDTH - 140, 188, 120, 25);
        toggleKeyField.setHorizontalAlignment(SwingConstants.CENTER);
        toggleKeyField.setBackground(DARK_GRAY);
        toggleKeyField.setForeground(Color.WHITE);
        toggleKeyField.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        ((AbstractDocument) toggleKeyField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attributes)
                    throws BadLocationException {
                if (offset == -1 && length == -1) {
                    super.replace(fb, 0, toggleKeyField.getText().length(), text, attributes);
                }
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                // NO-OP
            }

            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attributes)
                    throws BadLocationException {
                // NO-OP
            }
        });

        toggleKeyField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                try {
                    if (!KeyEvent.getKeyModifiersText(e.getModifiers()).contains(KeyEvent.getKeyText(e.getKeyCode()))
                            && e.getKeyCode() != KeyEvent.VK_CAPS_LOCK) {
                        AutoClicker.toggleKey[0] = KeyEvent.getKeyText(e.getKeyCode());
                        AutoClicker.toggleKey[1] = KeyEvent.getKeyModifiersText(e.getModifiers());
                        AutoClicker.toggleMouseButton = -1;
                        ((AbstractDocument) toggleKeyField.getDocument()).replace(-1, -1,
                                getKeyString(e.getKeyCode(), e.getModifiers()), null);
                        saveSettings();
                    }
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        });

        toggleKeyField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    if (e.getButton() == 2 || e.getButton() > 3) {
                        AutoClicker.toggleMouseButton = (e.getButton() == 2) ? 3 : e.getButton();
                        AutoClicker.toggleKey[0] = "";
                        AutoClicker.toggleKey[1] = "";
                        ((AbstractDocument) toggleKeyField.getDocument()).replace(-1, -1,
                                "Mouse " + ((e.getButton() == 2) ? 3 : e.getButton()), null);
                        saveSettings();
                    }
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        });

        mainPane.add(toggleKeyField);
    }

    private void setupMisc() {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            InputStream fontFile = AutoClicker.class.getClassLoader().getResourceAsStream("assets/BebasNeue.otf");
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            ge.registerFont(font);
            fontFile.close();

            randomizerBox.setFont(font.deriveFont(Font.PLAIN, 14));
            titleText.setFont(font.deriveFont(Font.PLAIN, 25));
            leftCpsRange.setFont(font.deriveFont(Font.PLAIN, 18));
            rightCpsRange.setFont(font.deriveFont(Font.PLAIN, 18));
            overlayBox.setFont(font.deriveFont(Font.PLAIN, 14));
            rightClickBox.setFont(font.deriveFont(Font.PLAIN, 14));
            minecraftOnlyBox.setFont(font.deriveFont(Font.PLAIN, 14));
            toggleKeyText.setFont(font.deriveFont(Font.PLAIN, 14));
            resetDefaultsBox.setFont(font.deriveFont(Font.PLAIN, 14));
            rightEnabledBox.setFont(font.deriveFont(Font.PLAIN, 14));
            minCPSField.setFont(new Font("arial", Font.PLAIN, 12));
            maxCPSField.setFont(new Font("arial", Font.PLAIN, 12));
            rightMinCPSField.setFont(new Font("arial", Font.PLAIN, 12));
            rightMaxCPSField.setFont(new Font("arial", Font.PLAIN, 12));
            toggleKeyField.setFont(new Font("arial", Font.PLAIN, 12));
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }

        frame.add(titleBar);
        frame.add(mainPane);
        frame.setVisible(true);
    }

    private String getKeyString(int keyCode, int modifiers) {
        String modifiersString = KeyEvent.getKeyModifiersText(modifiers).replace("+", "");
        String keyString;

        if (keyCode == 0) {
            keyString = "Invalid Key";
            modifiersString = "";
        } else if (keyCode == 32) {
            keyString = "Space";
        } else {
            keyString = KeyEvent.getKeyText(keyCode);
        }

        return modifiersString + keyString;
    }
}
