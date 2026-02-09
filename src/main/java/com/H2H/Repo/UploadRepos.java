package com.H2H.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.H2H.Entity.UPLOADFILES;

@Repository
public interface UploadRepos extends JpaRepository<UPLOADFILES, Long> {

//	 @Query("SELECT t FROM TBLUPLOADFILE t WHERE t.fileName = :fileName")
//	    boolean findByFileName(@Param("fileName") String fileName);

	@Query(value = "SELECT uf.FINAME FROM UPLOADFILES uf WHERE uf.FINAME = :filename", name = "findFilenameByFilename")
	String findFilenameByFilename(String filename);

// @Query("SELECT uf.FINAME FROM UPLOADFILES uf")
// List<String> findAllFilenames();
	@Query("SELECT uf.FINAME FROM UPLOADFILES uf WHERE uf.FILE_RECEIVE_DATE BETWEEN :startDate AND :endDate")
	List<String> findFilenamesBetweenDates(@Param("startDate") String startDate, @Param("endDate") String endDate);
}


