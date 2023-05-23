package com.example.emlodyserver.controllers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

@Service
public class FileService {
    public String getEmotionByImage(String path, MultipartFile file) throws IOException, InterruptedException {
        String filePath= saveImage(path,file);
        ProcessBuilder pb = new ProcessBuilder("emotion_identifier\\emotion_identifier.exe",filePath);
        long start=System.currentTimeMillis();
        Process p = pb.start();
        p.waitFor();
        long end=System.currentTimeMillis();
        System.out.println("time!"+(end-start));
        StringBuilder builder = new StringBuilder();
        BufferedReader bfr = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = "";
        while ((line = bfr.readLine()) != null) {
            builder.append(line);
        }
        File f= new File(filePath);
        f.delete();
        return builder.toString();
    }
    private String saveImage(String path, MultipartFile file){
        String filePath=path+ File.separator+getImageName();
        File newFile=new File(path);
        if(!newFile.exists()){
            newFile.mkdir();
        }
        try {
            Files.copy(file.getInputStream(), Paths.get(filePath));
        }
        catch (IOException e){
            System.out.println("Error: "+e);

        }
        return filePath;
    }


    private String getImageName(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd_HHmmss");
        String imageName;
       imageName="IMG"+ sdf.format(new Date())+".jpg";
       return imageName;

    }

}