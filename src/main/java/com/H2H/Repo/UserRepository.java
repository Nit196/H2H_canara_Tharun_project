package com.H2H.Repo;


import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.H2H.Entity.User;



//public interface UserRepository extends JpaRepository<User, Long> {
////	Userdata findByUsername(String username);
//	
//	@Query("select u from User u where u.username= :username")	
//	public User getUserByUserName(@Param("username") String username);
//		
//	
//}





// Remove it later
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}