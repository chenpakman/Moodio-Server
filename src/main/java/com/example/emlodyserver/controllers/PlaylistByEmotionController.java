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
    public ResponseEntity<ResponseServer> fileUpload(@RequestParam("image") MultipartFile image) {
        System.out.println("Here");

        try {
            emotion = this.fileService.getEmotionByImage(path, image);

            System.out.println(emotion);
            if (!emotion.isEmpty()) {

                response.setEmotion(emotion);
                switch(emotion){
                    case "angry":
                        String angryUrl = spotifyApiManager.getPlaylistUrl("Angry");
                        response.addPlaylistUrl("Angry", angryUrl);
                        String relaxedUrl = spotifyApiManager.getPlaylistUrl("Relaxing");
                        response.addPlaylistUrl("Relaxing",relaxedUrl);
                        break;
                }
                return new ResponseEntity<>(response, HttpStatus.OK);

            } else {
                response.setError(Errors.getInvalidImage());
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            response.setError(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
    @GetMapping(value = "/app")
    public void fileUpload() throws IOException {

    }
    }