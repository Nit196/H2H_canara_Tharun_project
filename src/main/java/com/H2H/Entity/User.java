package com.H2H.Entity;

import java.util.*;

import javax.persistence.*;
 
@Entity
@Table(name = "users")
public class User {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
//   private boolean enabled;
    
    //remove it later
    private boolean loggedIn; // or String sessionToken; 
     
 

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

//	public boolean isEnabled() {
//		return enabled;
//	}
//
//	public void setEnabled(boolean enabled) {
//		this.enabled = enabled;
//	}

 public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
    
}
