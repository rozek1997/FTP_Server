package com.goldenore.fileserver.configuration;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class UserSecurity {

    public boolean hasUserID(Authentication authentication, String username) {
        User user = (User) authentication.getPrincipal();
        return user != null && username.equals(user.getUsername());

    }
}
