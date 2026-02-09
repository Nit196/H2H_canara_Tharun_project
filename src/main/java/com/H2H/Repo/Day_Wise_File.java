//package com.H2H.Repo;
//
//import java.util.List;
//
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.CrudRepository;
//
//import com.H2H.Entity.ACKFILES;
//
//
//	public interface Day_Wise_File extends CrudRepository<ACKFILES,Integer>
//	{
//			
//		@Query("SELECT uf.FINAME FROM ACKFILES uf WHERE TRUNC(uf.RECIVEDATE) = TRUNC(CURRENT_DATE)")
//		List<String> findFilenameByCurrentDate();
//
//	}
//
//


package com.H2H.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.H2H.Entity.ACKFILES;


	public interface Day_Wise_File extends JpaRepository<ACKFILES,Integer>
	{
			
		// To Fetch file based on currentDate
		@Query("SELECT uf.FINAME FROM ACKFILES uf WHERE TRUNC(uf.RECIVEDATE) = TRUNC(CURRENT_DATE)")
		List<String> findFilenameByCurrentDate();
		
		
		// To fetch file based on Filename
		@Query(value = "SELECT uf.FINAME FROM ACKFILES uf WHERE uf.FINAME = :filename", name = "findFilenameByFilename")
	    String findFilenameByFilename(String filename);

	}



