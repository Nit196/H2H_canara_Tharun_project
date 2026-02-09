package com.H2H.SecurityAuth;



import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;

import com.H2H.Entity.User;
public class CustomUserDetails implements UserDetails

{
        
        
        @Autowired
    private SessionRegistry sessionRegistry; // Autowire the session registry
        
        
    @Autowired
    private final User user;

    public CustomUserDetails(User user){this.user = user;}
  
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() 
   {
      
        return null;
    }
    

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
    
        return true;
    }

    @Override
    public boolean isAccountNonLocked() 
    {
        // You can implement additional checks based on your application logic
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // You can implement additional checks based on your application logic
        return true;
    }

    @Override
    public boolean isEnabled() {
        // You can implement additional checks based on your application logic
        return true;
    }
    
    
  // Custom method to invalidate existing sessions for the user
    public void invalidateSessions() {
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                if (userDetails.getUsername().equals(getUsername())) {
                    for (SessionInformation info : sessionRegistry.getAllSessions(userDetails, true)) {
                        info.expireNow(); // Invalidate the session
                    }
                }
            }
        }
    }
    
 
}
