package com.mladenov.projectmanagement.util;

import com.mladenov.projectmanagement.auth.service.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserPrincipalUtil {
    public static Long getCurrentLoggedUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        }

        return null;
    }

    public static boolean isUserAdmin(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(s -> s.equals("ROLE_ADMIN"));
        }

        return false;
    }
}
