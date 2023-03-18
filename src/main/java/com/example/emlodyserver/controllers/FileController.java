package com.example.emlodyserver.controllers;

import com.example.emlodyserver.Response.Errors;
import com.example.emlodyserver.Response.FileResponse;
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
public class FileController {
    @Autowired
    private FileService fileService;
   @Value("${project.image}")
    private String path;
    @PostMapping(value="/app")
    public ResponseEntity<String> fileUpload(@RequestParam("image") MultipartFile image) throws IOException, InterruptedException {
        System.out.println("Here");
       String emotion= this.fileService.getEmotionByImage(path,image);
        System.out.println(emotion);
        if(!emotion.isEmpty()) {
            return new ResponseEntity<>(emotion, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(Errors.getInvalidImage(), HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping(value="/app")
    public String test(){
        return "Success";
    }
}