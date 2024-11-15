package com.ruffian7.sevenclicker;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import com.ruffian7.sevenclicker.config.ConfigManager;
import com.ruffian7.sevenclicker.gui.ClickerGui;
import com.ruffian7.sevenclicker.listener.KeyListener;
import com.ruffian7.sevenclicker.listener.MouseListener;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;

public class AutoClicker {

    public static Robot robot;
    public static Point mousePos;
    public static ClickerGui gui;

    public static boolean toggled = false;
    public static boolean activated = false;
    public static boolean skipNext = false;
    public static boolean blockHit = false;
    public static boolean minecraftOnly = true;
    public static boolean randomizer = false;
    public static boolean rightEnabled = false;

    // Add right click specific variables
    public static boolean rightToggled = false;
    public static boolean rightActivated = false;
    public static boolean rightSkipNext = false;

    private static int delay = -1;
    private static int rightDelay = -1;
    public static long lastTime = 0;
    public static long rightLastTime = 0;
    public static int minCPS = 8;
    public static int maxCPS = 12;
    public static int rightMinCPS = 8;
    public static int rightMaxCPS = 12;
    public static int button = 1;
    private static final Random random = new Random();
    public static ConfigManager configManager;
    public static String[] toggleKey = {"", ""};
    public static int toggleMouseButton = 3;

    public static void main(String[] args) {
        try {
            // Add basic logging
            System.out.println("Starting 7Clicker...");


            System.out.println("Java version: " + System.getProperty("java.version"));
            System.out.println("Working directory: " + System.getProperty("user.dir"));

            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            System.out.println("Thread priority set to maximum");

            configManager = new ConfigManager();
            System.out.println("Config manager initialized");

            try {
                // Load saved config values
                toggleKey[0] = configManager.getProperty("toggleKey1", "");
                toggleKey[1] = configManager.getProperty("toggleKey2", "");
                toggleMouseButton = configManager.getIntProperty("toggleMouseButton", 3);
                minCPS = configManager.getIntProperty("minCPS", 8);
                maxCPS = configManager.getIntProperty("maxCPS", 12);
                rightMinCPS = configManager.getIntProperty("rightMinCPS", 8);
                rightMaxCPS = configManager.getIntProperty("rightMaxCPS", 12);
                button = configManager.getIntProperty("button", 1);
                minecraftOnly = configManager.getBooleanProperty("minecraftOnly", true);
                randomizer = configManager.getBooleanProperty("randomizer", false);
                rightEnabled = configManager.getBooleanProperty("rightEnabled", false);
                System.out.println("Config loaded successfully");
            } catch (Exception e) {
                System.err.println("Error loading config: " + e.getMessage());

            }

            LogManager.getLogManager().reset();
            Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);
            System.out.println("Logging configured");

            try {
                robot = new Robot();
                robot.setAutoDelay(0);
                robot.setAutoWaitForIdle(false);
                System.out.println("Robot initialized");

                GlobalScreen.registerNativeHook();
                GlobalScreen.addNativeKeyListener(new KeyListener());
                GlobalScreen.addNativeMouseListener(new MouseListener());
                System.out.println("Global hooks registered");
            } catch (AWTException | NativeHookException e) {
                System.err.println("Error initializing robot or hooks: " + e.getMessage());

            }

            gui = new ClickerGui();
            gui.updateFromConfig();
            System.out.println("GUI initialized");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Saving config before shutdown");
                saveCurrentConfig();
            }));

            // Main loop
            System.out.println("Entering main loop");
            //noinspection InfiniteLoopStatement
            while (true) {
                try {
                    mousePos = gui.frame.getMousePosition();

                    if (toggled && (!minecraftOnly || isMinecraftFocused())) {
                        if (activated) {
                            long currentTime = System.currentTimeMillis();
                            if (currentTime - lastTime >= delay) {
                                if (!skipNext) {
                                    click(true); // Left click
                                }
                                skipNext = false;
                                lastTime = currentTime;

                                delay = (int) (1000.0 / (randomizer ?
                                        (random.nextInt(maxCPS - minCPS + 1) + minCPS) :
                                        ((maxCPS + minCPS) / 2.0)));
                            }
                        } else {
                            lastTime = System.currentTimeMillis();
                            delay = (int) (1000.0 / (randomizer ?
                                    (random.nextInt(maxCPS - minCPS + 1) + minCPS) :
                                    ((maxCPS + minCPS) / 2.0)));
                        }
                    }

                    // Right click logic
                    if (rightEnabled && rightToggled && (!minecraftOnly || isMinecraftFocused())) {
                        if (rightActivated) {
                            long currentTime = System.currentTimeMillis();
                            if (currentTime - rightLastTime >= rightDelay) {
                                if (!rightSkipNext) {
                                    click(false); // Right click
                                }
                                rightSkipNext = false;
                                rightLastTime = currentTime;

                                rightDelay = (int) (1000.0 / (randomizer ?
                                        (random.nextInt(rightMaxCPS - rightMinCPS + 1) + rightMinCPS) :
                                        ((rightMaxCPS + rightMinCPS) / 2.0)));
                            }
                        } else {
                            rightLastTime = System.currentTimeMillis();
                            rightDelay = (int) (1000.0 / (randomizer ?
                                    (random.nextInt(rightMaxCPS - rightMinCPS + 1) + rightMinCPS) :
                                    ((rightMaxCPS + rightMinCPS) / 2.0)));
                        }
                    }


                } catch (Exception e) {
                    System.err.println("Error in main loop: " + e.getMessage());

                }
            }
        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());

            // Keep the window open on error
            System.out.println("Press Enter to exit...");

        }
    }

    private static boolean isMinecraftFocused() {
        char[] windowText = new char[512];
        HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        User32.INSTANCE.GetWindowText(hwnd, windowText, 512);
        String activeWindowTitle = Native.toString(windowText).toLowerCase();

        return activeWindowTitle.contains("minecraft") ||
                activeWindowTitle.contains("lunar client") ||
                activeWindowTitle.contains("forge") ||
                activeWindowTitle.contains("pvp");
    }

    private static void click(boolean isLeftClick) {
        if (isLeftClick) {
            skipNext = true;
            System.out.println("Performing left click");
        } else {
            rightSkipNext = true;
            System.out.println("Performing right click");
        }

        int mouseButton = isLeftClick ? 16 : 4;
        int oppositeButton = isLeftClick ? 4 : 16;

        if (randomizer) {
            if (random.nextDouble() < 0.15) {
                System.out.println("Click skipped by randomizer");
                return;
            }

            robot.mousePress(mouseButton);
            try {
                Thread.sleep(random.nextInt(25));
            } catch (InterruptedException _) {

            }
            robot.mouseRelease(mouseButton);

            if (blockHit && isLeftClick) {
                System.out.println("Performing block hit");
                try {
                    Thread.sleep(random.nextInt(50));
                } catch (InterruptedException _) {

                }
                robot.mousePress(oppositeButton);
                try {
                    Thread.sleep(random.nextInt(25));
                } catch (InterruptedException _) {

                }
                robot.mouseRelease(oppositeButton);
            }
        } else {
            robot.mousePress(mouseButton);
            robot.mouseRelease(mouseButton);

            if (blockHit && isLeftClick) {
                System.out.println("Performing block hit");
                robot.mousePress(oppositeButton);
                robot.mouseRelease(oppositeButton);
            }
        }
    }

    private static void saveCurrentConfig() {
        System.out.println("Saving configuration...");
        configManager.saveConfig(
                toggleKey[0],
                toggleKey[1],
                toggleMouseButton,
                minCPS,
                maxCPS,
                rightMinCPS,
                rightMaxCPS,
                button,
                minecraftOnly,
                randomizer,
                rightEnabled
        );
        System.out.println("Configuration saved successfully");
    }

    public static void toggle() {
        AutoClicker.toggled = !AutoClicker.toggled;

        AutoClicker.gui.updateTitle(AutoClicker.toggled);
        AutoClicker.activated = false;
        AutoClicker.skipNext = false;
        AutoClicker.blockHit = false;
    }

}
