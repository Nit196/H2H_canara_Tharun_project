package com.H2H.Config;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class FilesTwoDates {

	
	public List listFilesBetweenDates(String folderPath, Date startDate, Date endDate) {
        File folder = new File(folderPath);
        if (!folder.isDirectory()) {
            System.err.println("The provided path is not a directory.");
            //return;
        }

        File[] files = folder.listFiles();
        List foundFile =new ArrayList<String>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   
                
        for (File file : files) {
            Date lastModified = new Date(file.lastModified());

            if (lastModified.after(startDate) && lastModified.before(endDate)) {
                System.out.println(file.getName() + " - Last Modified: " + dateFormat.format(lastModified));
                foundFile.add(file.getName());
            }
           
  
	
	}
        return foundFile;
}
}
	
