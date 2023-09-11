package com.example.moodioserver;


import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class HeartbeatSimulator {

  //  private final String filePath="src/main/java/com/example/moodioserver/HR.csv";
    private final String filePath="/home/ubuntu/moodio/HR.csv";
    private float expectedHeartbeat=70;
    private int numOfHeartbeatMeasurements=3180;
    private float sumOfHeartbeatMeasurements;

    private static int currentLine=0;
    public HeartbeatSimulator(){
        //calculateExpectedHeartbeat();
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
            //todo: delete all?
            System.out.println("sumOfHeartbeatMeasurements: "+sumOfHeartbeatMeasurements);
            System.out.println("numOfHeartbeatMeasurements: "+numOfHeartbeatMeasurements);
            System.out.println("expectedHeartbeat "+(expectedHeartbeat));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean isHeartbeatHigh(float currentHeartbeat){

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
                    System.out.println("currentLine "+currentLine);
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