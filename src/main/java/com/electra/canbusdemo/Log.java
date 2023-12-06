package com.electra.canbusdemo;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class Log{
    private static Log instance = null;
    public static ArrayList<String[]> array_log;
    private Log() {}
    public static Log getInstance() {
        if (instance == null){
            instance = new Log();
            array_log = new ArrayList<String[]>();
        }

        return instance;
    }

    public void Save(String file_path)throws Exception{
        File csvFile = new File(file_path);
        FileWriter fileWriter = new FileWriter(csvFile);
        for (String[] data : array_log) { StringBuilder line = new StringBuilder();
            for (int i = 0; i < data.length; i++) { line.append("\""); line.append(data[i].replaceAll("\"","\"\"")); line.append("\""); if (i != data.length - 1) { line.append(';'); } } line.append("\n"); fileWriter.write(line.toString()); } fileWriter.close();
    }
}