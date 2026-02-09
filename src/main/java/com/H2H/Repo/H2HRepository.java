package com.H2H.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.H2H.Entity.TBL_CONFIG;


// First Repository for First Entity
@Repository
public interface H2HRepository extends JpaRepository<TBL_CONFIG,Integer> {

// update Data
	
}







