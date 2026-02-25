package com.barsik.backend.security;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.barsik.backend.entity.User;
import com.barsik.backend.repository.UserRepository;
//CustomUserDetailsService при загрузке из базы определяет, какие роли есть.
@Service
public class CustomUserDetailsService implements UserDetailsService{
    
    @Autowired private UserRepository userRepository;
    

    @Override
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        if(user.getOwner() !=null) authorities.add(new SimpleGrantedAuthority("ROLE_OWNER"));
        if(user.getSitter() !=null) authorities.add(new SimpleGrantedAuthority("ROLE_SITTER"));
        return new CustomUserDetails(email, user.getPasswordHash(), authorities, user.getId());

    }

}
