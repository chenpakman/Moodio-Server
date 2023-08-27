package com.example.moodioserver.response;

public class Errors {

    private final static String invalidImage="The image that you choose is not valid.\n Please try again.";
    private final static String somethingWentWrong="Something Went Wrong\n Please try again.";

    public static String getInvalidImage() {
        return invalidImage;
    }

    public static String getSomethingWentWrong() {
        return somethingWentWrong;
    }
}