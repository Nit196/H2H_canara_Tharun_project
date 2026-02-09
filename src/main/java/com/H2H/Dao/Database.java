package com.H2H.Dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.H2H.Entity.CONFIGVALUE;
//import com.H2H.Logger.Custome_Logger;

@Service
public class Database {

	@Autowired
	private JdbcTemplate JDBCTemplate;
	
	//@Autowired
	//private Custome_Logger customLogger;
	
	// Function For the Search Function
	public String searchAndDelete(String filename,String table)  throws Exception
	{
	
    if(table.equals("TBL_INWFILE"))
    {
    	
    	
 	
    //	try {
    	
    //	String sql="select * from "+table +" where FINAME = ?";
    	
    	 try {
  	        String sql1 = "DELETE FROM " + table + " WHERE FINAME = ?";

  	        int rowsAffected = JDBCTemplate.update(sql1, filename);

  	        if (rowsAffected > 0) {
  	            // Successfully deleted the data
  	            System.out.println("File deleted successfully.");
  	            return "File deleted successfully.";
  	        } else {
  	            // No rows were affected, meaning the data with the given filename wasn't found
  	            System.out.println("No matching data found for deletion.");
  	          return "No matching data found for deletion.";
  	        }
  	    } catch (Exception e) {
  	        // Handle exceptions, e.g., SQLException
  	        System.err.println("Error deleting data: " + e.getMessage());
  	       // customLogger.logError("An error occurred: ", e);
  	        return "Error deleting data ";
  	    }
		

    }
      
    
    else if(table.equals("ACKFILES"))
    {
    	 
 	    
    	 try {
   	        String sql1 = "DELETE FROM " + table + " WHERE FINAME = ?";

   	        int rowsAffected = JDBCTemplate.update(sql1, filename);

   	        if (rowsAffected > 0) {
   	            // Successfully deleted the data
   	            System.out.println("File deleted successfully.");
   	            return "File deleted successfully.";
   	        } else {
   	            // No rows were affected, meaning the data with the given filename wasn't found
   	            System.out.println("No matching data found for deletion.");
   	          return "No matching data found for deletion.";
   	        }
   	    } catch (Exception e) {
   	        // Handle exceptions, e.g., SQLException
   	        System.err.println("Error deleting data: " + e.getMessage());
   	       // customLogger.logError("An error occurred: ", e);
   	        return "Error deleting data ";
   	    }
		/*
		 * try { String sql="select * from ACKFILES where FINAME = ?";
		 * 
		 * return JDBCTemplate.queryForObject( sql, new Object[]{filename}, (rs, rowNum)
		 * -> { TBL_INWFILE fileEntity = new TBL_INWFILE();
		 * fileEntity.setFINAME(filename); return fileEntity; } );
		 * 
		 * 
		 * }
		 * 
		 * catch(EmptyResultDataAccessException e) {
		 * System.out.println("The exceptionis the:---------------->>>>"+e); return
		 * null;
		 * 
		 * 
		 * }
		 */
   }
	
   else
   {
	   
	   try {
	        String sql1 = "DELETE FROM " + "UPLOADFILES" + " WHERE FINAME = ?";
      
	        int rowsAffected = JDBCTemplate.update(sql1, filename);

	        if (rowsAffected > 0) {
	            // Successfully deleted the data
	            System.out.println("Data deleted successfully.");
	            return "Data deleted successfully.";
	        } else {
	            // No rows were affected, meaning the data with the given filename wasn't found
	            System.out.println("No matching data found for deletion.");
	            return "No matching data found for deletion.";
	        }
	    } catch (Exception e) {
	        // Handle exceptions, e.g., SQLException
	        System.err.println("Error deleting data: " + e.getMessage());
//	        customLogger.logError("An error occurred: ", e);
	        return "Error deleting data:";
	    }
		/*
		 * try { String sql="select * from UPLOADFILES where FINAME = ?";
		 * 
		 * return JDBCTemplate.queryForObject( sql, new Object[]{filename}, (rs, rowNum)
		 * -> { TBL_INWFILE fileEntity = new TBL_INWFILE();
		 * fileEntity.setFINAME(filename); return fileEntity; } );
		 * 
		 * } catch(EmptyResultDataAccessException e) {
		 * System.out.println("The exceptionis the:---------------->>>>"+e); return
		 * null;
		 * 
		 * }
		 */
   }
	
	
}

    // Function to Delete the Files
	public String Delete(String filename)
	{ 
		String sql = "DELETE FROM file_table WHERE FINAME = ?";
		JDBCTemplate.update(sql, filename);

		return "Deleted";    
	}

   // Function To Update data in database
	public void Update_db(CONFIGVALUE cfg)
	{
		 String sql1 = "UPDATE TBL_CONFIG SET configvalue = ? where configkey='SS_HOST_ADDR'";  
		 JDBCTemplate.update(sql1, cfg.getSS_HOST_ADDR());
		 
		 String sql2 = "UPDATE TBL_CONFIG SET configvalue = ? where configkey='SS_PORTNO'";  
		 JDBCTemplate.update(sql2, cfg.getSS_PORTNO());
		 
		 String sql3 = "UPDATE TBL_CONFIG SET configvalue = ? where configkey='SS_PASS'";  
		 JDBCTemplate.update(sql3, cfg.getSS_PASS());
		 
		 
		 String sql4 = "UPDATE TBL_CONFIG SET configvalue = ? where configkey='SS_USERNAME'";  
		 JDBCTemplate.update(sql4, cfg.getSS_USERNAME());
		 
		 
		 String sql5 = "UPDATE TBL_CONFIG SET configvalue = ? where configkey='SS_PASSPHRASE'";  
		 JDBCTemplate.update(sql5, cfg.getSS_PASSPHRASE());
		 
		 String sql6 = "UPDATE TBL_CONFIG SET configvalue = ? where configkey='SS_SSHPRKEYFILE'";  
		 JDBCTemplate.update(sql6, cfg.getSS_SSHPRKEYFILE());

	}

}
