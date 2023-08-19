package com.example.moodioserver.controllers;

import com.example.moodioserver.Playlist;
import com.example.moodioserver.Response.Errors;
import com.example.moodioserver.Response.ResponseServer;
import com.example.moodioserver.Spotify.SpotifyApiManager;
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
        System.out.println("Uploaded image file");
        try {

            String resEmotion = this.fileService.getEmotionByImage(path, image);

            if (null != resEmotion && !resEmotion.isEmpty()) {
                resEmotion =
                        resEmotion.replace(resEmotion.charAt(0), resEmotion.substring(0,1).toUpperCase().charAt(0));
                System.out.println("Detected: " + resEmotion);
                return getPlayListsWithoutDeepFace(resEmotion);
            } else {
                response.setError(Errors.getInvalidImage());
                System.out.println("No emotion Detected" + response.getError());

                return new ResponseEntity<>(gson.toJson(response), HttpStatus.NO_CONTENT);
            }
        } catch (IOException | InterruptedException e ) {
            response.setError(e.getMessage());
            System.out.println("No emotion Detected" + response.getError());
            return new ResponseEntity<>(gson.toJson(response), HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping(value = "/token")
    public ResponseEntity<String> getSpotifyAccessToken() {
        return new ResponseEntity<>(spotifyApiManager.getSpotifyAccessToken(), HttpStatus.OK);
    }

    @PutMapping(value = "/app")
    public ResponseEntity<String> getPlayListsWithoutDeepFace(@RequestParam (name = "emotions") String emotions) throws IOException {
        Gson gson = new Gson();
        boolean isRelaxing = false;
        boolean isHappy = false;
        ResponseServer response = new ResponseServer();
        response.setEmotion(emotions);
        System.out.println("Here");

        if (emotions.contains("Sad")) {
            Playlist sadPlaylist = this.spotifyApiManager.getPlaylistUrl("Sad Soft");
            response.addPlaylist("Sad", sadPlaylist);
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
            Playlist relaxingPlaylist = this.spotifyApiManager.getPlaylistUrl("Relaxing");
            response.setPlaylistUrl(relaxingPlaylist.getPlaylistUrl());
            response.addPlaylist("Relaxing", relaxingPlaylist);
        }
        if(isHappy){
            Playlist happyUrl = this.spotifyApiManager.getPlaylistUrl("Feel Happy");
            response.setPlaylistUrl(happyUrl.getPlaylistUrl());
            response.addPlaylist("Happy", happyUrl);
        }

        System.out.println(response.getPlaylistUrl());

        return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
    }




}