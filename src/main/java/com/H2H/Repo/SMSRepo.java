package com.H2H.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.H2H.Entity.SMSConnection;


public interface SMSRepo extends JpaRepository<SMSConnection,Long>  
{
	 // String findFilenameByFilename(String filename);

}
