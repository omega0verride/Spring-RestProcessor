package org.indritbreti.restprocessor;

public class Utilities {
    public static boolean isNullOrEmpty(String value){
        return value == null || value.trim().length() == 0;
    }
}
