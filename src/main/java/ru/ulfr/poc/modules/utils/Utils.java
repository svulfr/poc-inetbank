package ru.ulfr.poc.modules.utils;

/**
 * Miscellaneous helpers
 */
public class Utils {
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
