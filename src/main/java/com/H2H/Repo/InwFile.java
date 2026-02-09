package com.H2H.Repo;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.H2H.Entity.TBL_INWFILE;


	public interface InwFile extends JpaRepository<TBL_INWFILE,Integer>
	{
		
		// To fetch file based on Filename(HQL Query)
		@Query(value = "SELECT uf.FINAME FROM TBL_INWFILE uf WHERE uf.FINAME = :filename", name = "findFilenameByFilename")
	    String findFilenameByFilename(String filename);
		
		@Query(value = "SELECT uf.FINAME FROM TBL_INWFILE uf WHERE uf.FINAME = :filename", name = "findInwardsByFilename")
	    String findInwardsByFilename(String filename);
	
		@Query("SELECT uf.FINAME FROM TBL_INWFILE uf WHERE uf.FILE_RECEIVE_DATE BETWEEN :startDate AND :endDate")
		List<String> findFilenamesBetweenDates(@Param("startDate") String startDate, @Param("endDate") String endDate);

	}