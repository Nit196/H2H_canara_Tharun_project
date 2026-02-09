package com.H2H.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.H2H.Entity.ACKFILES;

public interface AckRepos extends JpaRepository<ACKFILES, Long> {

	@Query(value = "SELECT uf.FINAME FROM ACKFILES uf WHERE uf.FINAME = :filename", name = "findACKByFilename")
	String findACKByFilename(String filename);

	@Query(value = "SELECT uf.FINAME FROM ACKFILES uf WHERE uf.FINAME = :filename", name = "findACKByFilename")
	String findACKSsByFilename(String filename);
}
