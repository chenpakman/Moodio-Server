package com.example.moodioserver.controllers;

import com.example.moodioserver.HeartbeatSimulator;
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


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

@RestController
public class PlaylistByEmotionController {
    @Autowired
    private FileService fileService;
    @Value("${project.image}")
    private String path;
    private final SpotifyApiManager spotifyApiManager=new SpotifyApiManager();
    HeartbeatSimulator heartbeatSimulator=new HeartbeatSimulator();


    @PostMapping(value = "/app")
    public ResponseEntity<String> fileUpload(@RequestParam("image") MultipartFile image) {
        System.out.println("heartbeatSimulator.getHeartbeatFromFile();->"+ heartbeatSimulator.getHeartbeatFromFile());
        Gson gson = new Gson();
        ResponseServer response = new ResponseServer();

        System.out.println("Uploaded image file");
        try {
            savaToLocal(image);
            String resEmotion = this.fileService.getEmotionByImage(path, image);

            //this.fileService.getHeartbeatFromFile();

            if (null != resEmotion && !resEmotion.isEmpty() && !resEmotion.equals("neutral")) {
                resEmotion =
                        resEmotion.replace(resEmotion.charAt(0), resEmotion.substring(0,1).toUpperCase().charAt(0));
                System.out.println("resEmotion="+resEmotion);

                return getPlayListsWithoutDeepFace(resEmotion);
            } else {
                response.setError(Errors.getInvalidImage());
                System.out.println("No emotion Detected" + response.getError());
                return new ResponseEntity<>(gson.toJson(response), HttpStatus.NO_CONTENT);
            }
        } catch (IOException e ) {
            response.setError(e.getMessage());
            System.out.println("No emotion Detected" + response.getError());
            return new ResponseEntity<>(gson.toJson(response), HttpStatus.NO_CONTENT);
        }
    }
        @GetMapping(value = "/test")
    public String test() {
       return "success";
    }

    @PutMapping(value = "/app")
    public ResponseEntity<String> getPlayListsWithoutDeepFace(@RequestParam (name = "emotions") String emotions) throws IOException {
        Gson gson = new Gson();
        ResponseServer response = new ResponseServer();
        boolean isHeartbeatHigh=heartbeatSimulator.isHeartbeatHigh();
        String[] splitEmotions = emotions.split(" ");
        response.setEmotion(splitEmotions[0]);
        if(isHeartbeatHigh){
            addPlaylistHighHeartbeat(emotions,response);
        }
        else{
            addPlaylistNormalHeartbeat(emotions,response);
        }
        String detectedEmotion = response.getEmotion();
        response.setPlaylistUrl(
                response.getPlaylistsUrls()
                        .get(detectedEmotion).getPlaylistUrl());
        return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
    }
    private void addPlaylistHighHeartbeat(String emotions,ResponseServer response) throws IOException {
        if (emotions.contains("Sad")||emotions.contains("Fear") || emotions.contains("Nervous")) {
            Playlist sadUrl = this.spotifyApiManager.getPlaylistUrl("anti anxiety");
            response.addPlaylist("Anxiety", sadUrl);
        }
        if (emotions.contains("Angry")) {
            Playlist metalUrl = this.spotifyApiManager.getPlaylistUrl("angry metal");
            response.addPlaylist("metal", metalUrl);
            Playlist calmUrl = this.spotifyApiManager.getPlaylistUrl("angry calm");
            response.addPlaylist("Calm", calmUrl);
        }
        if (emotions.contains("Happy") || emotions.contains("Exited")){
            Playlist metalUrl = this.spotifyApiManager.getPlaylistUrl("high energy");
            response.addPlaylist("Energy", metalUrl);
        }

    }
    private void addPlaylistNormalHeartbeat(String emotions,ResponseServer response) throws IOException {
        if (emotions.contains("Sad")) {
            Playlist sadUrl = this.spotifyApiManager.getPlaylistUrl("sad");
            response.addPlaylist("Sad", sadUrl);
            Playlist cheerUrl = this.spotifyApiManager.getPlaylistUrl("cheerful uplifting");
            response.addPlaylist("Cheerful", cheerUrl);
        }
        if (emotions.contains("Fear") || emotions.contains("Nervous")) {
            Playlist calmUrl = this.spotifyApiManager.getPlaylistUrl("calm slow");
            response.addPlaylist("Calm", calmUrl);
        }
        if (emotions.contains("Angry")) {
            Playlist angryUrl = this.spotifyApiManager.getPlaylistUrl("angry");
            response.addPlaylist("Angry", angryUrl);
            Playlist relaxingUrl = this.spotifyApiManager.getPlaylistUrl("relaxing");
            response.addPlaylist("Relaxing", relaxingUrl);
        }
        if (emotions.contains("Happy") || emotions.contains("Exited")){
            Playlist happyUrl = this.spotifyApiManager.getPlaylistUrl("Happy");
            response.addPlaylist("Happy", happyUrl);
        }

    }

    private void savaToLocal(MultipartFile image) {
        if (image.isEmpty()) {
            System.out.println("Please upload a file");
        }

        try {
            // Specify the directory where you want to save the image
            String uploadDir = "C:\\Temp";

            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            // Generate a unique file name for the uploaded image
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            File targetFile = new File(uploadPath, fileName);

            // Save the file
            FileOutputStream outputStream = new FileOutputStream(targetFile);
            outputStream.write(image.getBytes());
            outputStream.close();
            System.out.println("File saved successfully");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save File");

        }
    }




}