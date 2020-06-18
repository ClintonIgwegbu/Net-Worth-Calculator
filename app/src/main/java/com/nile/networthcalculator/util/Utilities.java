package com.nile.networthcalculator.util;

public class Utilities {

    public static double parseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}
