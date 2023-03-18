package com.example.emlodyserver.Response;

public class Errors {

    private final static String invalidImage="The image that you choose is not valid.\n Please try again.";

    public static String getInvalidImage() {
        return invalidImage;
    }
}