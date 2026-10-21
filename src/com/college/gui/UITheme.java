package com.college.gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Central design tokens, color palette, and styling utilities for the Swing GUI.
 * Adheres strictly to Java Swing/AWT without external dependencies.
 */
public class UITheme {

    // Harmonious academic color palette
    public static final Color PRIMARY = new Color(26, 54, 93);       // Deep Navy
    public static final Color PRIMARY_HOVER = new Color(43, 108, 176); // Slate Blue
    public static final Color ACCENT = new Color(49, 151, 149);       // Teal
    public static final Color ACCENT_LIGHT = new Color(230, 255, 250);
    public static final Color BG_LIGHT = new Color(247, 250, 252);    // Off-white canvas
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT_MAIN = new Color(45, 55, 72);     // Slate Charcoal
    public static final Color TEXT_MUTED = new Color(113, 128, 150);  // Cool Grey
    public static final Color BORDER_COLOR = new Color(226, 232, 240); // Soft grey border
    public static final Color SUCCESS = new Color(56, 161, 105);      // Emerald Green
    public static final Color DANGER = new Color(229, 62, 62);        // Crimson Red
    public static final Color WARNING = new Color(221, 107, 32);      // Amber Orange

    // Modern typography
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);

    /**
     * Applies standard flat system look-and-feel with anti-aliasing.
     */
    public static void applyLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // Fall back to system or default Swing Look & Feel
        }

        // Enable crisp text antialiasing across all Swing components
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
    }

    /**
     * Creates a styled modern primary JButton.
     */
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(
            new LineBorder(PRIMARY.darker(), 1, true),
            new EmptyBorder(8, 18, 8, 18)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Creates a styled secondary JButton.
     */
    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(new Color(237, 242, 247));
        btn.setForeground(TEXT_MAIN);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(8, 16, 8, 16)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Creates a styled danger JButton.
     */
    public static JButton createDangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(DANGER);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(
            new LineBorder(DANGER.darker(), 1, true),
            new EmptyBorder(8, 16, 8, 16)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Creates a clean styled JTextField.
     */
    public static JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(FONT_BODY);
        tf.setForeground(TEXT_MAIN);
        tf.setBackground(Color.WHITE);
        tf.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    /**
     * Creates a clean styled JPasswordField.
     */
    public static JPasswordField createPasswordField(int columns) {
        JPasswordField pf = new JPasswordField(columns);
        pf.setFont(FONT_BODY);
        pf.setForeground(TEXT_MAIN);
        pf.setBackground(Color.WHITE);
        pf.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(6, 10, 6, 10)
        ));
        return pf;
    }

    /**
     * Creates an elevated card border.
     */
    public static Border createCardBorder() {
        return new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(16, 16, 16, 16)
        );
    }
}
