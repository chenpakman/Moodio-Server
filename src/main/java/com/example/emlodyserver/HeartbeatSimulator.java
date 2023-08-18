package com.example.emlodyserver;


import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class HeartbeatSimulator {

    private final String filePath="C:\\Users\\chen3\\IdeaProjects\\EmlodyServer\\src\\main\\java\\com\\example\\emlodyserver\\HR.csv";
    private float expectedHeartbeat;
    private int numOfHeartbeatMeasurements;
    private float sumOfHeartbeatMeasurements;

    private int currentLine=0;
    public HeartbeatSimulator(){
        calculateExpectedHeartbeat();
    }

    public void calculateExpectedHeartbeat(){
        try {
            FileReader filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                for (String cell : nextRecord) {
                    numOfHeartbeatMeasurements++;
                    sumOfHeartbeatMeasurements=sumOfHeartbeatMeasurements+Float.parseFloat(cell);
                }
            }
            expectedHeartbeat=sumOfHeartbeatMeasurements/numOfHeartbeatMeasurements;
            System.out.println("sumOfHeartbeatMeasurements: "+sumOfHeartbeatMeasurements);
            System.out.println("numOfHeartbeatMeasurements: "+numOfHeartbeatMeasurements);
            System.out.println("expectedHeartbeat "+(expectedHeartbeat));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean isHeartbeatHigh(){
        float currentHeartbeat=getHeartbeatFromFile();
        if(currentHeartbeat>=(expectedHeartbeat+20))
        {
            return true;
        }
        return false;
    }
    public float getHeartbeatFromFile(){
        String heartbeatStr="";
        try  {
            try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
                heartbeatStr = lines.skip(currentLine).findFirst().get();
                System.out.println(currentLine);
                if(currentLine==numOfHeartbeatMeasurements){
                    currentLine=0;
                }else {
                    currentLine++;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return Float.parseFloat(heartbeatStr);
    }
}