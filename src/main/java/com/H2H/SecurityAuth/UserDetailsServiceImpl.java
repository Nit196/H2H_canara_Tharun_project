package com.H2H.SecurityAuth;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.H2H.Entity.User;
import com.H2H.Repo.UserRepository;


@Service
public class UserDetailsServiceImpl  
{
    @Autowired
    private UserRepository userRepository;

 
    public void updateUserLoginStatus(String username, boolean isLoggedIn) 
    {
        User user = userRepository.findByUsername(username);
        if (user != null)
        {
        	System.out.println("User found with name:-"+user);
            user.setLoggedIn(isLoggedIn);
            System.out.println("updating the stage...");
            userRepository.save(user);
        }
    }
}