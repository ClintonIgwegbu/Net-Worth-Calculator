package com.nile.networthcalculator.util;

import java.text.NumberFormat;
import java.text.ParseException;

public class Utilities {

    final private static String TAG = "Utilities";

    public static double parseDouble(String s) {
        try {
            return (double)NumberFormat.getInstance().parse(s).longValue();
        } catch (ParseException e) {
            return 0;
        }
    }

}
