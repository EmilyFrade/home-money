package com.homemoney.services.user;

import com.homemoney.model.user.User;
import com.homemoney.repositories.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    public User findCurrentUserByUsername(Authentication authentication) {
        String username = authentication.getName(); 
        return userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void saveAndAuthenticate(User user, HttpServletRequest request) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), Collections.emptyList());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, user.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
    }

    public void update(Long id, User updatedUser) {
        User existingUser = findById(id);

        if (existingUser != null) {
            existingUser.setName(updatedUser.getName());
            existingUser.setUsername(updatedUser.getUsername());

            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            userRepository.save(existingUser);
        }
    }

    public List<User> findByResidenceId(Long residenceId) {
        return userRepository.findByResidenceId(residenceId);
    }
}
