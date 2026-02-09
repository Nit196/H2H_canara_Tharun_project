//package com.H2H.H2H_Automations;
//import org.springframework.beans.factory.annotation.Value;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.Writer;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//
//public class LogAppend 
//{
//	
//	 
//	  
//	 
//	  
//	   public static void logError(String message)
//	   {
//	        Path basePath = Paths.get(System.getProperty("user.dir"));
//	        Path applogs = Paths.get(basePath.toString(), "AppLogs");
//
//	        try {
//	            if (Files.notExists(applogs)) {
//	                Files.createDirectory(applogs);
//	                System.out.println("Created base 'AppLogs' directory at: " + applogs.toAbsolutePath());
//	            }
//
//	            DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
//	            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//	            
//	            String dt = dtf.format(LocalDateTime.now());
//	            Path logFilePath = Paths.get(applogs.toString(), "Log-" + dt + ".txt");
//
//	            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath.toFile(), true))) {
//	                writer.write(message);
//	                writer.write(System.lineSeparator());
//	                writer.flush();
//	            }
//
//	        } catch (IOException e) {
//	            System.err.println("An error occurred while logging: " + e.getMessage());
//	        }
//	    }
//}
//

package com.H2H.H2H_Automations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogAppend {

    public static void logError(String message) {
        Path basePath = Paths.get(System.getProperty("user.dir"));
        Path applogs = Paths.get(basePath.toString(), "AppLogs");

        try {
            // Check if 'AppLogs' directory exists, if not create it
            if (Files.notExists(applogs)) {
                Files.createDirectory(applogs);
                System.out.println("Created base 'AppLogs' directory at: " + applogs.toAbsolutePath());
            }

            // DateTime formatters
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter dtfTimestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Get the current date for the log file and current timestamp for the log entry
            String currentDate = dtf.format(LocalDateTime.now());
            String timestamp = dtfTimestamp.format(LocalDateTime.now());

            // Define the log file path
            Path logFilePath = Paths.get(applogs.toString(), "Log-" + currentDate + ".txt");

            // Writing the log message with timestamp
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath.toFile(), true))) {
                writer.write("[" + timestamp + "] " + message); // Add timestamp before the message
                writer.write(System.lineSeparator());
                writer.flush();
            }

        } catch (IOException e) {
            System.err.println("An error occurred while logging: " + e.getMessage());
        }
    }
}

