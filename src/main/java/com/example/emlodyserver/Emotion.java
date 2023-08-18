package com.example.emlodyserver;

public enum Emotion {
    ANGRY("Angry"),
    FEAR("Fear"),
    HAPPY("Happy"),
    SAD("Sad");

    private final String displayName;

    Emotion(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}