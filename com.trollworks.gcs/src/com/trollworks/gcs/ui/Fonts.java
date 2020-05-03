/*
 * Copyright ©1998-2020 by Richard A. Wilkes. All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, version 2.0. If a copy of the MPL was not distributed with
 * this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, version 2.0.
 */

package com.trollworks.gcs.ui;

import com.trollworks.gcs.utility.I18n;
import com.trollworks.gcs.utility.Preferences;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;
import javax.swing.UIManager;

/** Provides standardized font access and utilities. */
public class Fonts {
    /** The standard text field font. */
    public static final  String                 KEY_STD_TEXT_FIELD    = "TextField.font";
    /** The label font. */
    public static final  String                 KEY_LABEL             = "trollworks.v2.label";
    /** The field font. */
    public static final  String                 KEY_FIELD             = "trollworks.v2.field";
    /** The field notes font. */
    public static final  String                 KEY_FIELD_NOTES       = "trollworks.v2.field.notes";
    /** The technique field font. */
    public static final  String                 KEY_TECHNIQUE_FIELD   = "trollworks.v2.field.technique";
    /** The primary footer font. */
    public static final  String                 KEY_PRIMARY_FOOTER    = "trollworks.v2.footer.primary";
    /** The secondary footer font. */
    public static final  String                 KEY_SECONDARY_FOOTER  = "trollworks.v2.footer.secondary";
    /** The notes font. */
    public static final  String                 KEY_NOTES             = "trollworks.v2.notes";
    /** The notification key used when font change notifications are broadcast. */
    public static final  String                 FONT_NOTIFICATION_KEY = "FontsChanged";
    private static final String                 MODULE                = "Font";
    private static final TreeMap<String, Fonts> DEFAULTS              = new TreeMap<>();
    private              String                 mDescription;
    private              Font                   mDefaultFont;

    private Fonts(String description, Font defaultFont) {
        mDescription = description;
        mDefaultFont = defaultFont;
    }

    /** Loads the current font settings from the preferences file. */
    public static void loadFromPreferences() {
        String name = getDefaultFont().getName();
        register(KEY_LABEL, I18n.Text("Labels"), new Font(name, Font.PLAIN, 9));
        register(KEY_FIELD, I18n.Text("Fields"), new Font(name, Font.BOLD, 9));
        register(KEY_FIELD_NOTES, I18n.Text("Field Notes"), new Font(name, Font.PLAIN, 8));
        register(KEY_TECHNIQUE_FIELD, I18n.Text("Technique Fields"), new Font(name, Font.BOLD + Font.ITALIC, 9));
        register(KEY_PRIMARY_FOOTER, I18n.Text("Primary Footer"), new Font(name, Font.BOLD, 8));
        register(KEY_SECONDARY_FOOTER, I18n.Text("Secondary Footer"), new Font(name, Font.PLAIN, 6));
        register(KEY_NOTES, I18n.Text("Notes"), new Font(name, Font.PLAIN, 9));
        Preferences prefs = Preferences.getInstance();
        for (String key : DEFAULTS.keySet()) {
            Font font = prefs.getFontValue(MODULE, key);
            if (font != null) {
                UIManager.put(key, font);
            }
        }
    }

    /** Saves the current font settings to the preferences file. */
    public static void saveToPreferences() {
        Preferences prefs = Preferences.getInstance();
        prefs.removePreferences(MODULE);
        for (String key : DEFAULTS.keySet()) {
            Font font = UIManager.getFont(key);
            if (font != null) {
                prefs.setValue(MODULE, key, font);
            }
        }
    }

    private static void register(String key, String description, Font defaultFont) {
        UIManager.put(key, defaultFont);
        DEFAULTS.put(key, new Fonts(description, defaultFont));
    }

    /** Restores the default fonts. */
    public static void restoreDefaults() {
        for (Entry<String, Fonts> entry : DEFAULTS.entrySet()) {
            UIManager.put(entry.getKey(), entry.getValue().mDefaultFont);
        }
    }

    /** @return Whether the fonts are currently at their default values or not. */
    public static boolean isSetToDefaults() {
        for (Entry<String, Fonts> entry : DEFAULTS.entrySet()) {
            if (!entry.getValue().mDefaultFont.equals(UIManager.getFont(entry.getKey()))) {
                return false;
            }
        }
        return true;
    }

    /** @return The default font to use. */
    public static Font getDefaultFont() {
        return UIManager.getFont(KEY_STD_TEXT_FIELD);
    }

    /** @return The available font keys. */
    public static String[] getKeys() {
        return DEFAULTS.keySet().toArray(new String[0]);
    }

    /**
     * @param key The font key to lookup.
     * @return The human-readable label for the font.
     */
    public static String getDescription(String key) {
        Fonts match = DEFAULTS.get(key);
        return match != null ? match.mDescription : null;
    }

    /**
     * @param font The font to work on.
     * @return The specified font as a canonical string.
     */
    public static String getStringValue(Font font) {
        return font.getName() + "," + font.getStyle() + "," + font.getSize();
    }

    /**
     * @param font The font to work on.
     * @return The font metrics for the specified font.
     */
    public static FontMetrics getFontMetrics(Font font) {
        Graphics2D  g2d = GraphicsUtilities.getGraphics();
        FontMetrics fm  = g2d.getFontMetrics(font);
        g2d.dispose();
        return fm;
    }

    /**
     * @param buffer       The string to create the font from.
     * @param defaultValue The value to use if the string is invalid.
     * @return A font created from the specified string.
     */
    public static Font create(String buffer, Font defaultValue) {
        if (defaultValue == null) {
            defaultValue = getDefaultFont();
        }
        String name  = defaultValue.getName();
        int    style = defaultValue.getStyle();
        int    size  = defaultValue.getSize();
        if (buffer != null && !buffer.isEmpty()) {
            StringTokenizer tokenizer = new StringTokenizer(buffer, ",");
            if (tokenizer.hasMoreTokens()) {
                name = tokenizer.nextToken();
                if (!isValidFontName(name)) {
                    name = defaultValue.getName();
                }
                if (tokenizer.hasMoreTokens()) {
                    buffer = tokenizer.nextToken();
                    try {
                        style = Integer.parseInt(buffer);
                    } catch (NumberFormatException nfe1) {
                        // We'll use the default style instead
                    }
                    if (style < 0 || style > 3) {
                        style = defaultValue.getStyle();
                    }
                    if (tokenizer.hasMoreTokens()) {
                        buffer = tokenizer.nextToken();
                        try {
                            size = Integer.parseInt(buffer);
                        } catch (NumberFormatException nfe1) {
                            // We'll use the default size instead
                        }
                        if (size < 1) {
                            size = 1;
                        } else if (size > 200) {
                            size = 200;
                        }
                    }
                }
            }
        }
        return new Font(name, style, size);
    }

    /**
     * @param name The name to check.
     * @return {@code true} if the specified name is a valid font name.
     */
    public static boolean isValidFontName(String name) {
        for (String element : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            if (element.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /** Cause font change listeners to be notified. */
    public static void notifyOfFontChanges() {
        Preferences.getInstance().getNotifier().notify(null, FONT_NOTIFICATION_KEY, null);
    }
}