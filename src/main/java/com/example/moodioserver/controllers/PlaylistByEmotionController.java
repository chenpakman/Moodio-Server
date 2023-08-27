package com.example.moodioserver.controllers;

import com.example.moodioserver.HeartbeatSimulator;
import com.example.moodioserver.Playlist;
import com.example.moodioserver.response.Errors;
import com.example.moodioserver.response.ResponseServer;
import com.example.moodioserver.spotify.SpotifyApiManager;
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
import java.util.Map;
import java.util.Random;

@RestController
public class PlaylistByEmotionController {
    @Autowired
    private FileService fileService;
    @Value("${project.image}")
    private String path;

    //todo:
    //טלי: היו לי NULLPOINTEREXCEPTIONS, חשבתי שזה קשור למקבילות אז ניסיתי להפוך את הסרבר לסטייטלס
    //טיפלתי בשגיאות אבל לא בדקתי אם הכל עובד אם הייתי מחזירה הכל חזרה
    //private final SpotifyApiManager spotifyApiManager=new SpotifyApiManager();
    //HeartbeatSimulator heartbeatSimulator=new HeartbeatSimulator();


    @PostMapping(value = "/app")
    public ResponseEntity<String> fileUpload(@RequestParam("image") MultipartFile image) {
        HeartbeatSimulator heartbeatSimulator = new HeartbeatSimulator();
        System.out.println("heartbeatSimulator.getHeartbeatFromFile();->"+ heartbeatSimulator.getHeartbeatFromFile()); //todo: delete?
        Gson gson = new Gson();
        ResponseServer response = new ResponseServer();

        //System.out.println("Uploaded image file"); todo: delete
        try {
            //savaToLocal(image);
            String resEmotion = this.fileService.getEmotionByImage(path, image);

            //this.fileService.getHeartbeatFromFile();

            if (null != resEmotion && !resEmotion.isEmpty()) {
                resEmotion = Character.toUpperCase(resEmotion.charAt(0)) + resEmotion.substring(1);
                //System.out.println("resEmotion="+resEmotion); todo: delete

                return getPlayListsWithoutDeepFace(resEmotion);
            } else {
                response.setError(Errors.getInvalidImage());
                //System.out.println("No emotion Detected" + response.getError()); todo: delete
                return new ResponseEntity<>(gson.toJson(response), HttpStatus.NO_CONTENT);
            }
        } catch (IOException e ) {
            response.setError(e.getMessage());
            //System.out.println("No emotion Detected" + response.getError()); todo: delete
            return new ResponseEntity<>(gson.toJson(response), HttpStatus.NO_CONTENT);
        }
    }
        @GetMapping(value = "/test")
    public String test() {
       return "success";
    }

    @PutMapping(value = "/app")
    public ResponseEntity<String> getPlayListsWithoutDeepFace(@RequestParam (name = "emotions") String emotions) throws IOException {
        HeartbeatSimulator heartbeatSimulator=new HeartbeatSimulator();
        Gson gson = new Gson();
        ResponseServer response = new ResponseServer();
        boolean isHeartbeatHigh=heartbeatSimulator.isHeartbeatHigh();
        String[] splitEmotions = emotions.split(" ");
        response.setEmotion(splitEmotions[0]);
        //System.out.println("resEmotion="+response.getEmotion()); todo: delete

        if(isHeartbeatHigh){
            addPlaylistHighHeartbeat(emotions,response);
        }
        else{
            addPlaylistNormalHeartbeat(emotions,response);
        }

        if(null == response.getPlaylistsUrls() &&
                (emotions.contains("Surprise") || emotions.contains("Neutral") || emotions.contains("Disgust"))){
            response.setError("Detected Surprise/Neutral/Disgust");
            return new ResponseEntity<>(gson.toJson(response), HttpStatus.NO_CONTENT);
        }

        String detectedEmotion = response.getEmotion();
        for(String emotion : response.getPlaylistsUrls().keySet()){
            if(emotion.contains(detectedEmotion)){
                detectedEmotion = emotion;
                response.setDefaultMixName(detectedEmotion);
                break;
            }
        }
        Playlist defaultPlayList = response.getPlaylistsUrls().get(detectedEmotion);
        String defaultUrl = null;
        if(null != defaultPlayList){
            defaultUrl = defaultPlayList.getPlaylistUrl();
        }
        else{
            int randomIndex = new Random().nextInt(response.getPlaylistsUrls().size());
            int currentIndex = 0;

            for (Map.Entry<String, Playlist> entry: response.getPlaylistsUrls().entrySet()) {
                if (currentIndex == randomIndex) {
                    defaultUrl = entry.getValue().getPlaylistUrl();
                    response.setDefaultMixName(entry.getKey());
                    break;
                }
                currentIndex++;
            }
        }

        response.setDefaultPlaylistUrl(defaultUrl);
        return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
    }
    private void addPlaylistHighHeartbeat(String emotions,ResponseServer response) throws IOException {
        SpotifyApiManager spotifyApiManager = new SpotifyApiManager();
        if (emotions.contains("Sad")||emotions.contains("Fear") || emotions.contains("Nervous")) {
            Playlist sadUrl = spotifyApiManager.getPlaylistUrl("anti anxiety");
            response.addPlaylist("Anti Anxiety", sadUrl);
        }
        if (emotions.contains("Angry")) {
            Playlist metalUrl = spotifyApiManager.getPlaylistUrl("angry metal");
            response.addPlaylist("Angry Metal", metalUrl);
            Playlist calmUrl = spotifyApiManager.getPlaylistUrl("angry calm");
            response.addPlaylist("Angry Calm", calmUrl);
        }
        if (emotions.contains("Happy") || emotions.contains("Exited")){
            Playlist metalUrl = spotifyApiManager.getPlaylistUrl("high energy");
            response.addPlaylist("High Energy", metalUrl);
        }
    }
    private void addPlaylistNormalHeartbeat(String emotions,ResponseServer response) throws IOException {
        SpotifyApiManager spotifyApiManager = new SpotifyApiManager();

        if (emotions.contains("Sad")) {
            Playlist sadUrl = spotifyApiManager.getPlaylistUrl("sad");
            response.addPlaylist("Sad", sadUrl);
            Playlist cheerUrl = spotifyApiManager.getPlaylistUrl("cheerful uplifting");
            response.addPlaylist("Cheerful", cheerUrl);
        }
        if (emotions.contains("Fear") || emotions.contains("Nervous")) {
            Playlist calmUrl = spotifyApiManager.getPlaylistUrl("calm slow");
            response.addPlaylist("Calm", calmUrl);
        }
        if (emotions.contains("Angry")) {
            Playlist angryUrl = spotifyApiManager.getPlaylistUrl("angry");
            response.addPlaylist("Angry", angryUrl);
            Playlist relaxingUrl = spotifyApiManager.getPlaylistUrl("relaxing");
            response.addPlaylist("Relaxing", relaxingUrl);
        }
        if (emotions.contains("Happy") || emotions.contains("Exited")){
            Playlist happyUrl = spotifyApiManager.getPlaylistUrl("Happy");
            response.addPlaylist("Happy", happyUrl);
        }

    }

    /*private void savaToLocal(MultipartFile image) { todo: delete/move to utils
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
    }*/

    // todo: enums for emotions/mix names



}