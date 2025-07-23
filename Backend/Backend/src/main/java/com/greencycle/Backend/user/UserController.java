package com.greencycle.Backend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    // Get current user profile
    @GetMapping("/me")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).body("Unauthorized");
        Optional<User> userOpt = userRepository.findByEmail(userDetails.getUsername());
        if (userOpt.isEmpty()) return ResponseEntity.status(404).body("User not found");
        User user = userOpt.get();
        // Exclude password from response
        UserProfileResponse resp = new UserProfileResponse(user.getId(), user.getEmail(), user.getName(), user.getWalletAddress(), user.getRoles());
        return ResponseEntity.ok(resp);
    }

    // Update current user profile (name only)
    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UpdateProfileRequest req) {
        if (userDetails == null) return ResponseEntity.status(401).body("Unauthorized");
        Optional<User> userOpt = userRepository.findByEmail(userDetails.getUsername());
        if (userOpt.isEmpty()) return ResponseEntity.status(404).body("User not found");
        User user = userOpt.get();
        user.setName(req.getName());
        userRepository.save(user);
        UserProfileResponse resp = new UserProfileResponse(user.getId(), user.getEmail(), user.getName(), user.getWalletAddress(), user.getRoles());
        return ResponseEntity.ok(resp);
    }
}

class UserProfileResponse {
    private Long id;
    private String email;
    private String name;
    private String walletAddress;
    private java.util.Set<String> roles;
    public UserProfileResponse(Long id, String email, String name, String walletAddress, java.util.Set<String> roles) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.walletAddress = walletAddress;
        this.roles = roles;
    }
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getWalletAddress() { return walletAddress; }
    public java.util.Set<String> getRoles() { return roles; }
}

class UpdateProfileRequest {
    private String name;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
} 