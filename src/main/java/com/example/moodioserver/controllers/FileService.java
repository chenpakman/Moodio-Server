package com.example.moodioserver.controllers;
import com.opencsv.CSVReader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
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
     private String directoryPath = "/home/ubuntu/moodio/";
     private int rowNum=1;
    public synchronized String  getEmotionByImage(String path, MultipartFile file){
        String filePath= saveImage(path,file);
        //ProcessBuilder pb = new ProcessBuilder("emotion_identifier\\emotion_identifier.exe",filePath);
        ProcessBuilder pb = new ProcessBuilder("./emotion_identifier",filePath);
        pb.directory(new File(directoryPath+"dist/emotion_identifier"));
        long start=System.currentTimeMillis();
        try {
            Process p = pb.start();
            p.waitFor();


        long end=System.currentTimeMillis();
        //System.out.println("time!"+(end-start)); todo: delete
        StringBuilder builder = new StringBuilder();
        BufferedReader bfr = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = "";
        while (true) {
            try {
                if (!((line = bfr.readLine()) != null)) break;
            } catch (IOException e) {
                System.out.println("error2"+e.getMessage());
            }
            builder.append(line);
        }
        File f= new File(filePath);
        f.delete();
            System.out.println("delete"+f);
            return builder.toString();
        } catch (IOException | InterruptedException e) {
            System.out.println("error"+e.getMessage());
        }
        return null;

    }
    private String saveImage(String path, MultipartFile file){
        //for prod:
        String filePath=directoryPath+path+getImageName();
        //for test:
        //String filePath=path+getImageName();
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
        System.out.println("filePath "+filePath);//todo: delete?
        return filePath;
    }


    private String getImageName(){
       // SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd_HHmmss");
        String imageName;
       imageName="IMG"+ System.currentTimeMillis()+".jpg";
       return imageName;

    }

}