package com.example.moodioserver.Response;

import com.example.moodioserver.Playlist;

import java.util.HashMap;
import java.util.Map;

public class ResponseServer {
    Map<String, Playlist> playlistsUrls;
    String playlistUrl;
    String error;
    String emotion;
    String imageUrl;
    public void setImageUrl(String imageUrl){
        this.imageUrl=imageUrl;
    }
    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setPlaylistUrl(String playlistUrl) {
        this.playlistUrl = playlistUrl;
    }

    public String getEmotion() {
        return emotion;
    }

    public String getError() {
        return error;
    }

    public String getPlaylistUrl() {
        return playlistUrl;
    }

    public Map<String, Playlist> getPlaylistsUrls() {
        return playlistsUrls;
    }

    public void addPlaylist(String emotion, Playlist playlist) {

        if(null == this.playlistsUrls){
            this.playlistsUrls = new HashMap<>();
        }

        this.playlistsUrls.put(emotion, playlist);
    }
}