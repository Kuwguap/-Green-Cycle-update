package com.greencycle.Backend.leaderboard;

import com.greencycle.Backend.user.User;
import com.greencycle.Backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leaderboard")
@CrossOrigin(origins = "*")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Get the leaderboard for a specific period
     * @param period The leaderboard period (WEEKLY, MONTHLY, ALL_TIME)
     * @param page The page number (default: 0)
     * @param size The page size (default: 10)
     * @return Page of leaderboard entries
     */
    @GetMapping
    public ResponseEntity<Page<LeaderboardEntryDTO>> getLeaderboard(
            @RequestParam(defaultValue = "WEEKLY") String period,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            LeaderboardPeriod leaderboardPeriod = LeaderboardPeriod.valueOf(period.toUpperCase());
            Page<LeaderboardEntry> entries = leaderboardService.getLeaderboard(leaderboardPeriod, page, size);
            
            // Convert to DTOs
            Page<LeaderboardEntryDTO> dtos = entries.map(this::convertToDTO);
            
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get the top leaderboard entries for a specific period
     * @param period The leaderboard period (WEEKLY, MONTHLY, ALL_TIME)
     * @param limit The maximum number of entries to return (default: 10)
     * @return List of leaderboard entries
     */
    @GetMapping("/top")
    public ResponseEntity<List<LeaderboardEntryDTO>> getTopLeaderboard(
            @RequestParam(defaultValue = "WEEKLY") String period,
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            LeaderboardPeriod leaderboardPeriod = LeaderboardPeriod.valueOf(period.toUpperCase());
            List<LeaderboardEntry> entries = leaderboardService.getTopLeaderboard(leaderboardPeriod, limit);
            
            // Convert to DTOs
            List<LeaderboardEntryDTO> dtos = entries.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get the authenticated user's rank for a specific period
     * @param period The leaderboard period (WEEKLY, MONTHLY, ALL_TIME)
     * @param userDetails The authenticated user
     * @return The user's rank
     */
    @GetMapping("/my-rank")
    public ResponseEntity<Map<String, Integer>> getMyRank(
            @RequestParam(defaultValue = "WEEKLY") String period,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            // Get the user
            Optional<User> userOpt = userRepository.findByEmail(userDetails.getUsername());
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            User user = userOpt.get();
            
            LeaderboardPeriod leaderboardPeriod = LeaderboardPeriod.valueOf(period.toUpperCase());
            int rank = leaderboardService.getUserRank(user, leaderboardPeriod);
            
            Map<String, Integer> response = new HashMap<>();
            response.put("rank", rank);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get the authenticated user's leaderboard entries for all periods
     * @param userDetails The authenticated user
     * @return The user's leaderboard entries
     */
    @GetMapping("/my-entries")
    public ResponseEntity<Map<String, LeaderboardEntryDTO>> getMyEntries(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Get the user
        Optional<User> userOpt = userRepository.findByEmail(userDetails.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        User user = userOpt.get();
        
        Map<String, LeaderboardEntryDTO> response = new HashMap<>();
        
        // Get entries for all periods
        for (LeaderboardPeriod period : LeaderboardPeriod.values()) {
            Optional<LeaderboardEntry> entryOpt = leaderboardService.getUserLeaderboardEntry(user, period);
            entryOpt.ifPresent(entry -> response.put(period.name(), convertToDTO(entry)));
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Manually trigger a recalculation of ranks (admin only)
     * @return Success message
     */
    @PostMapping("/recalculate")
    public ResponseEntity<Map<String, String>> recalculateRanks() {
        leaderboardService.recalculateRanks();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Ranks recalculated successfully");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Convert a LeaderboardEntry to a LeaderboardEntryDTO
     * @param entry The LeaderboardEntry
     * @return The LeaderboardEntryDTO
     */
    private LeaderboardEntryDTO convertToDTO(LeaderboardEntry entry) {
        LeaderboardEntryDTO dto = new LeaderboardEntryDTO();
        dto.setId(entry.getId());
        dto.setUserId(entry.getUser().getId());
        dto.setUsername(entry.getUser().getName());
        dto.setPeriod(entry.getPeriod().name());
        dto.setCycleBalance(entry.getCycleBalance());
        dto.setCleanupCount(entry.getCleanupCount());
        dto.setSpotAdoptions(entry.getSpotAdoptions());
        dto.setMarketplaceSales(entry.getMarketplaceSales());
        dto.setRank(entry.getRank());
        dto.setUpdatedAt(entry.getUpdatedAt());
        return dto;
    }
    
    /**
     * DTO for LeaderboardEntry
     */
    public static class LeaderboardEntryDTO {
        private Long id;
        private Long userId;
        private String username;
        private String period;
        private java.math.BigDecimal cycleBalance;
        private Integer cleanupCount;
        private Integer spotAdoptions;
        private Integer marketplaceSales;
        private Integer rank;
        private java.time.LocalDateTime updatedAt;
        
        // Getters and Setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public Long getUserId() {
            return userId;
        }
        
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPeriod() {
            return period;
        }
        
        public void setPeriod(String period) {
            this.period = period;
        }
        
        public java.math.BigDecimal getCycleBalance() {
            return cycleBalance;
        }
        
        public void setCycleBalance(java.math.BigDecimal cycleBalance) {
            this.cycleBalance = cycleBalance;
        }
        
        public Integer getCleanupCount() {
            return cleanupCount;
        }
        
        public void setCleanupCount(Integer cleanupCount) {
            this.cleanupCount = cleanupCount;
        }
        
        public Integer getSpotAdoptions() {
            return spotAdoptions;
        }
        
        public void setSpotAdoptions(Integer spotAdoptions) {
            this.spotAdoptions = spotAdoptions;
        }
        
        public Integer getMarketplaceSales() {
            return marketplaceSales;
        }
        
        public void setMarketplaceSales(Integer marketplaceSales) {
            this.marketplaceSales = marketplaceSales;
        }
        
        public Integer getRank() {
            return rank;
        }
        
        public void setRank(Integer rank) {
            this.rank = rank;
        }
        
        public java.time.LocalDateTime getUpdatedAt() {
            return updatedAt;
        }
        
        public void setUpdatedAt(java.time.LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}