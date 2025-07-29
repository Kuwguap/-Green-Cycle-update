package com.greencycle.Backend.auth;

import com.greencycle.Backend.user.User;
import com.greencycle.Backend.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/oauth2")
public class OAuth2AuthController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    @Autowired
    public OAuth2AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    
    @GetMapping("/success")
    public ResponseEntity<?> oauth2LoginSuccess(@AuthenticationPrincipal OAuth2User oauth2User) {
        try {
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");
            
            if (email == null) {
                return ResponseEntity.badRequest().body("Email not provided by OAuth2 provider");
            }
            
            // Check if user exists, if not create new user
            Optional<User> existingUser = userService.findByEmail(email);
            User user;
            
            if (existingUser.isPresent()) {
                user = existingUser.get();
            } else {
                // Create new user with OAuth2 data
                user = userService.createOAuth2User(email, name != null ? name : "OAuth2 User");
            }
            
            // Generate JWT token
            String token = jwtUtil.generateToken(user.getEmail());
            
            return ResponseEntity.ok(new JwtResponse(token));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("OAuth2 authentication failed: " + e.getMessage());
        }
    }
    
    @GetMapping("/failure")
    public ResponseEntity<?> oauth2LoginFailure() {
        return ResponseEntity.status(401).body("OAuth2 authentication failed");
    }
}