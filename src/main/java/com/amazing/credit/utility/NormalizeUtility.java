package com.amazing.credit.utility;

public class NormalizeUtility {
    public static String removeSpecialCharacters(String input){
         return input.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
    }
}
