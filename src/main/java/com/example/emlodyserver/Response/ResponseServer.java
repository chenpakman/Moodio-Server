package com.example.emlodyserver.Response;

import java.util.HashMap;
import java.util.Map;

public class ResponseServer {
    Map<String, String> playlistUrls;
    String error;
    String emotion;

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void addPlaylistUrl(String emotion, String playlistUrl) {
        if(null == this.playlistUrls){
            this.playlistUrls = new HashMap<>();
        }

        this.playlistUrls.put(emotion, playlistUrl);
    }

    public String getEmotion() {
        return emotion;
    }

    public String getError() {
        return error;
    }

    public Map<String, String> getPlaylistUrls() {
        return playlistUrls;
    }
}