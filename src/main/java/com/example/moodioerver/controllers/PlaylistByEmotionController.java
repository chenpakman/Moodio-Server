package com.example.moodioerver.controllers;

import com.example.moodioerver.Playlist;
import com.example.moodioerver.Response.Errors;
import com.example.moodioerver.Response.ResponseServer;
import com.example.moodioerver.Spotify.SpotifyApiManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.google.gson.Gson;


import java.io.IOException;

@RestController
public class PlaylistByEmotionController {
    @Autowired
    private FileService fileService;
    @Value("${project.image}")
    private String path;
    private final SpotifyApiManager spotifyApiManager=new SpotifyApiManager();


    @PostMapping(value = "/app")
    public ResponseEntity<String> fileUpload(@RequestParam("image") MultipartFile image) {
        Gson gson = new Gson();
        ResponseServer response = new ResponseServer();

        try {

            String resEmotion = this.fileService.getEmotionByImage(path, image);

            if (null != resEmotion && !resEmotion.isEmpty()) {
                resEmotion =
                        resEmotion.replace(resEmotion.charAt(0), resEmotion.substring(0,1).toUpperCase().charAt(0));

                return getPlayListsWithoutDeepFace(resEmotion);
            } else {
                response.setError(Errors.getInvalidImage());
                return new ResponseEntity<>(gson.toJson(response), HttpStatus.NO_CONTENT);
            }
        } catch (IOException | InterruptedException e ) {
            response.setError(e.getMessage());
            return new ResponseEntity<>(gson.toJson(response), HttpStatus.NO_CONTENT);
        }
    }

    @PutMapping(value = "/app")
    public ResponseEntity<String> getPlayListsWithoutDeepFace(@RequestParam (name = "emotions") String emotions) throws IOException {
        Gson gson = new Gson();
        boolean isRelaxing = false;
        boolean isHappy = false;
        ResponseServer response = new ResponseServer();
        response.setEmotion(emotions);
        if (emotions.contains("Sad")) {
            Playlist sadUrl = this.spotifyApiManager.getPlaylistUrl("Sad Soft");
            response.addPlaylist("Sad", sadUrl);
            isHappy = true;
        }
        if (emotions.contains("Fear") || emotions.contains("Nervous")) {
            isRelaxing = true;
        }
        if (emotions.contains("Angry")) {
            isRelaxing = true;
            Playlist angryUrl = this.spotifyApiManager.getPlaylistUrl("Angry");
            response.addPlaylist("Angry", angryUrl);
        }
        if (emotions.contains("Happy") || emotions.contains("Exited")){
            isHappy = true;
        }

        if(isRelaxing){
            response.setPlaylistUrl(this.spotifyApiManager.getPlaylistUrl("Relaxing").getPlaylistUrl());
            Playlist relaxingUrl = this.spotifyApiManager.getPlaylistUrl("Relaxing");
            response.addPlaylist("Relaxing", relaxingUrl);
        }
        if(isHappy){
            response.setPlaylistUrl(this.spotifyApiManager.getPlaylistUrl("Happy").getPlaylistUrl());
            Playlist happyUrl = this.spotifyApiManager.getPlaylistUrl("Happy");
            response.addPlaylist("Happy", happyUrl);
        }

        return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
    }




}