package com.H2H.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Users_Table {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int Id;
	
	private String Username;
	private String Password;
	private String Roll;
	
	

	public String getUsername() {
		return Username;
	}

	public void setUsername(String username) {
		Username = username;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public String getRoll() {
		return Roll;
	}

	public void setRoll(String roll) {
		Roll = roll;
	}

	
	
	@Override
	public String toString() {
		return "Users_Table [Username=" + Username + ", Password=" + Password + ", Roll=" + Roll + "]";
	}
	
	
	
	
	

}
