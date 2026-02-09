package com.H2H.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.H2H.Entity.UPLOADFILES;


	public interface Day_Wise_Upload extends JpaRepository<UPLOADFILES,Integer>
	{
			
		@Query("SELECT uf.FINAME FROM UPLOADFILES uf WHERE TRUNC(uf.RECIVEDATE) = TRUNC(CURRENT_DATE)")
		List<String> findFilenameByCurrentDate();

	}

