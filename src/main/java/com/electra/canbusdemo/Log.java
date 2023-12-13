package com.electra.canbusdemo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Singleton class for managing and saving log data.
 *
 * <p>
 * The {@code Log} class is designed as a singleton to manage log data and provides a method
 * for saving the log to a CSV file. It uses an {@code ArrayList<String[]>} to store log entries.
 * </p>
 *
 */
public class Log{
    private static Log instance = null;
    public static ArrayList<String[]> array_log;
    static private File csvFile;

    public String getFilePath(){
        return csvFile.getAbsolutePath().toString();
    }

    private Log() {}

    /**
     * Gets the singleton instance of the Log class.
     *
     * @return The singleton instance of the Log class.
     */
    public static Log getInstance(){
        if (instance == null){
            instance = new Log();
            array_log = new ArrayList<String[]>();

            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String timestampString = dateFormat.format(date);
            System.out.println(timestampString);

            timestampString=timestampString.replace(" ","_");
            timestampString=timestampString.replace(":","_");
            timestampString=timestampString.replace(".","_");
            timestampString=timestampString.replace("-","_");

            System.out.println(timestampString);

            csvFile = new File(timestampString+".csv");
        }

        return instance;
    }

    /**
     * Saves the log data to a CSV file.
     *
     * <p>
     * This method writes the log data to a CSV file.
     * It handles the necessary CSV formatting, including quoting and escaping.
     * </p>
     *
     * @throws Exception If an exception occurs during the log saving process.
     */
    public void Save()throws Exception{

        /*
       if(csvFile.getParent()==null || !(new File(csvFile.getParent()).exists()))
           throw new IncorrectFileNameException("Incorrect filename");


       if(!csvFile.getName().endsWith("csv"))
           throw new IncorrectFileExtensionException("Incorrect exception");
        */

            FileWriter fileWriter = new FileWriter(csvFile);

        for (String[] data : array_log) { StringBuilder line = new StringBuilder();
            for (int i = 0; i < data.length; i++) { line.append("\""); line.append(data[i].replaceAll("\"","\"\"")); line.append("\""); if (i != data.length - 1) { line.append(';'); } } line.append("\n"); fileWriter.write(line.toString()); } fileWriter.close();
    }
}

class IncorrectFileNameException extends Exception {
    public IncorrectFileNameException(String errorMessage) {
        super(errorMessage);
    }
}

class IncorrectFileExtensionException extends Exception {
    public IncorrectFileExtensionException(String errorMessage) {
        super(errorMessage);
    }
}