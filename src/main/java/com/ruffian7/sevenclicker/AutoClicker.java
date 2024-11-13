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

    private static int delay = -1;
    public static long lastTime = 0;
    public static int minCPS = 8;
    public static int maxCPS = 12;
    public static int rightMinCPS = 8;
    public static int rightMaxCPS = 12;
    public static int button = 1;
    private static Random random = new Random();
    public static ConfigManager configManager;
    public static String[] toggleKey = { "", "" };
    public static int toggleMouseButton = 3;

    public static void main(String[] args) {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        configManager = new ConfigManager();

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

        LogManager.getLogManager().reset();
        Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);

        try {
            robot = new Robot();
            robot.setAutoDelay(0);
            robot.setAutoWaitForIdle(false);

            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new KeyListener());
            GlobalScreen.addNativeMouseListener(new MouseListener());
        } catch (AWTException | NativeHookException e) {
            e.printStackTrace();
        }

        gui = new ClickerGui();
        gui.updateFromConfig();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            saveCurrentConfig();
        }));

        while (true) {
            mousePos = gui.frame.getMousePosition();

            if (toggled && (!minecraftOnly || isMinecraftFocused())) {
                if (activated) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastTime >= delay) {
                        if (!skipNext) {
                            click();
                        }
                        skipNext = false;
                        lastTime = currentTime;

                        // Calculate next delay based on CPS settings
                        if (button == 1) {
                            delay = (int) (1000.0 / (randomizer ?
                                    (random.nextInt(maxCPS - minCPS + 1) + minCPS) :
                                    ((maxCPS + minCPS) / 2.0)));
                        } else {
                            delay = (int) (1000.0 / (randomizer ?
                                    (random.nextInt(rightMaxCPS - rightMinCPS + 1) + rightMinCPS) :
                                    ((rightMaxCPS + rightMinCPS) / 2.0)));
                        }
                    }
                } else {
                    lastTime = System.currentTimeMillis();
                    if (button == 1) {
                        delay = (int) (1000.0 / (randomizer ?
                                (random.nextInt(maxCPS - minCPS + 1) + minCPS) :
                                ((maxCPS + minCPS) / 2.0)));
                    } else {
                        delay = (int) (1000.0 / (randomizer ?
                                (random.nextInt(rightMaxCPS - rightMinCPS + 1) + rightMinCPS) :
                                ((rightMaxCPS + rightMinCPS) / 2.0)));
                    }
                }
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isMinecraftFocused() {
        char[] windowText = new char[512];
        HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        User32.INSTANCE.GetWindowText(hwnd, windowText, 512);
        String activeWindowTitle = Native.toString(windowText);

        return activeWindowTitle.contains("Minecraft");
    }

    private static void click() {
        skipNext = true;

        if (randomizer) {
            // Random chance to skip a click (10-20% chance)
            if (random.nextDouble() < 0.15) {
                return;
            }

            // Add random delay between press and release (0-25ms)
            robot.mousePress((button == 1) ? 16 : 4);
            try {
                Thread.sleep(random.nextInt(25));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            robot.mouseRelease((button == 1) ? 16 : 4);

            if (blockHit) {
                try {
                    Thread.sleep(random.nextInt(50));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                robot.mousePress((button == 1) ? 4 : 16);
                try {
                    Thread.sleep(random.nextInt(25));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                robot.mouseRelease((button == 1) ? 4 : 16);
            }
        } else {
            robot.mousePress((button == 1) ? 16 : 4);
            robot.mouseRelease((button == 1) ? 16 : 4);

            if (blockHit) {
                robot.mousePress((button == 1) ? 4 : 16);
                robot.mouseRelease((button == 1) ? 4 : 16);
            }
        }
    }

    private static void saveCurrentConfig() {
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
    }

    public static void toggle() {
        if (AutoClicker.toggled) {
            AutoClicker.toggled = false;
        } else {
            AutoClicker.toggled = true;
        }

        AutoClicker.activated = false;
        AutoClicker.skipNext = false;
        AutoClicker.blockHit = false;
    }
}