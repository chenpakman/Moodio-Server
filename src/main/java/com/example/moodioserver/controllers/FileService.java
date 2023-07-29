package com.example.moodioserver.controllers;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        Path ofImage = Path.of(filePath);
        try {
            Files.copy(file.getInputStream(), ofImage);
        } catch (FileAlreadyExistsException e) {
            // Destination file already exists, delete it and then copy
            try {
                Files.delete(ofImage);
                Files.copy(file.getInputStream(), ofImage);
                System.out.println("File copied successfully after deleting the existing file.");
            } catch (IOException ex) {
                System.out.println("Error occurred while deleting the existing file: " + ex.getMessage());
            }
        }catch (IOException e) {
            System.out.println("An error occurred while copying the file: " + e.getMessage());
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