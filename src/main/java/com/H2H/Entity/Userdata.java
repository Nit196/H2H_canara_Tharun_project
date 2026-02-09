package com.H2H.Entity;

import java.util.Collection;
import java.util.Collections;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
public class Userdata implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    //private String Rolebase;
   public Userdata()  {} 
  
    
    
public Userdata(String username, String password) {
        this.username = username;
        this.password = password;
    }
    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAccountNonExpired1() {
        return true;
    }

    

    public boolean isAccountNonLocked1() {
        return true;
    }

   
    public boolean isCredentialsNonExpired1() {
        return true;
    }

    

    public boolean isEnabled1() {
        return true;
    }

   

    // UserDetails interface methods

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Return your authorities if needed
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
