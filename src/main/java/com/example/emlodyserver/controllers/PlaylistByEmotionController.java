package com.example.emlodyserver.controllers;

import com.example.emlodyserver.Response.Errors;
import com.example.emlodyserver.Response.ResponseServer;
import com.example.emlodyserver.Spotify.SpotifyApiManager;
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
    private SpotifyApiManager spotifyApiManager=new SpotifyApiManager();
    ResponseServer response ;
    String emotion;

    @PostMapping(value = "/app")
    public ResponseEntity<String> fileUpload(@RequestParam("image") MultipartFile image) {
        System.out.println("Here");//TODO:delete
        Gson gson = new Gson();
        boolean isRelaxing = false;
        boolean isHappy = false;
        this.response = new ResponseServer();

        try {

            String resEmotion = this.fileService.getEmotionByImage(path, image);

            if (null != resEmotion && !resEmotion.isEmpty()) {
                System.out.println(resEmotion); //TODO:delete

                this.emotion =
                        resEmotion.replace(resEmotion.charAt(0), resEmotion.substring(0,1).toUpperCase().charAt(0));
                this.response.setEmotion(this.emotion);

                switch(this.emotion){

                    case "Fear":
                        isRelaxing = true;
                        break;
                    case "Sad":
                        String sadUrl = this.spotifyApiManager.getPlaylistUrl("Sad Soft");
                        this.response.addPlaylistUrl("Sad", sadUrl);
                        isHappy = true;
                        break;
                    case "Angry":
                        String angryUrl = this.spotifyApiManager.getPlaylistUrl("Angry");
                        this.response.addPlaylistUrl("Angry", angryUrl);
                        isRelaxing = true;
                        break;
                    case "Happy":
                        isHappy = true;
                        break;
                    default:
                        response.setError(Errors.getInvalidImage());
                        return new ResponseEntity<>(gson.toJson(response), HttpStatus.NO_CONTENT);
                }

                if(isRelaxing){
                    this.response.setPlaylistUrl(this.spotifyApiManager.getPlaylistUrl("Relaxing"));
                    String relaxingUrl = this.spotifyApiManager.getPlaylistUrl("Relaxing");
                    this.response.addPlaylistUrl("Relaxing", relaxingUrl);
                }
                if(isHappy){
                    this.response.setPlaylistUrl(this.spotifyApiManager.getPlaylistUrl("Happy"));
                    String happyUrl = this.spotifyApiManager.getPlaylistUrl("Happy");
                    this.response.addPlaylistUrl("Happy", happyUrl);
                }

                return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
            } else {
                response.setError(Errors.getInvalidImage());
                return new ResponseEntity<>(gson.toJson(response), HttpStatus.NO_CONTENT);
            }
        } catch (IOException e) {
            response.setError(e.getMessage());
            return new ResponseEntity<>(gson.toJson(response), HttpStatus.NO_CONTENT);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @PutMapping(value = "/app")
    public ResponseEntity<String> getPlayListsWithoutDeepFace(@RequestParam ("emotions") String emotions) throws IOException {
        Gson gson = new Gson();
        String playlistUrl = this.spotifyApiManager.getPlaylistUrl(emotions);
        this.response.addPlaylistUrl(emotion, playlistUrl);
        return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
    }

    @GetMapping(value = "/app")
    public void fileUpload() throws IOException {

    }




}

