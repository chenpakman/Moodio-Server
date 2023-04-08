package com.example.emlodyserver.controllers;

import com.example.emlodyserver.Response.Errors;
import com.example.emlodyserver.Response.ResponseServer;
import com.example.emlodyserver.Spotify.SpotifyApiManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    ResponseServer response = new ResponseServer();
    String emotion;

    @PostMapping(value = "/app")
    public ResponseEntity<String> fileUpload(@RequestParam("image") MultipartFile image) {
        System.out.println("Here");
        Gson gson = new Gson();

        try {
            emotion = this.fileService.getEmotionByImage(path, image);

            System.out.println(emotion);
            if (!emotion.isEmpty()) {


                switch(emotion){
                    case "angry":
                        response.setEmotion("Angry");
                        String angryUrl = spotifyApiManager.getPlaylistUrl("Angry");
                        response.addPlaylistUrl("Angry", angryUrl);
                        String relaxedUrl = spotifyApiManager.getPlaylistUrl("Relaxing");
                        response.addPlaylistUrl("Relaxing",relaxedUrl);
                        response.setPlaylistUrl(spotifyApiManager.getPlaylistUrl("Relaxing"));
                        break;
                }

                return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

            } else {
                response.setError(Errors.getInvalidImage());
                return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            response.setError(e.getMessage());
            return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
    @GetMapping(value = "/app")
    public void fileUpload() throws IOException {

    }
    }