package com.example.moodioerver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class MoodioServerApplication {

    public static void main(String[] args) {
        ProcessBuilder pb = new ProcessBuilder("python","download_package.py").inheritIO();
        try {
            Process p= pb.start();
            p.waitFor();
        }
        catch (IOException | InterruptedException e)
        {
            System.out.println("Error: "+e);
        }
        SpringApplication.run(MoodioServerApplication.class, args);
    }

}