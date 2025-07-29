package com.greencycle.Backend.leaderboard;

import com.greencycle.Backend.blockchain.BlockchainService;
import com.greencycle.Backend.user.User;
import com.greencycle.Backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@Service
public class LeaderboardService {

    @Autowired
    private LeaderboardRepository leaderboardRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BlockchainService blockchainService;
    
    /**
     * Get the top leaderboard entries for a specific period
     * @param period The leaderboard period
     * @param limit The maximum number of entries to return
     * @return List of leaderboard entries
     */
    public List<LeaderboardEntry> getTopLeaderboard(LeaderboardPeriod period, int limit) {
        return leaderboardRepository.findTopByPeriod(period, limit);
    }
    
    /**
     * Get paginated leaderboard entries for a specific period
     * @param period The leaderboard period
     * @param page The page number
     * @param size The page size
     * @return Page of leaderboard entries
     */
    public Page<LeaderboardEntry> getLeaderboard(LeaderboardPeriod period, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return leaderboardRepository.findByPeriodOrderByRankAsc(period, pageable);
    }
    
    /**
     * Get a user's leaderboard entry for a specific period
     * @param user The user
     * @param period The leaderboard period
     * @return The leaderboard entry, if found
     */
    public Optional<LeaderboardEntry> getUserLeaderboardEntry(User user, LeaderboardPeriod period) {
        return leaderboardRepository.findByUserAndPeriod(user, period);
    }
    
    /**
     * Get a user's rank for a specific period
     * @param user The user
     * @param period The leaderboard period
     * @return The user's rank, or 0 if not found
     */
    public int getUserRank(User user, LeaderboardPeriod period) {
        Optional<LeaderboardEntry> entry = leaderboardRepository.findByUserAndPeriod(user, period);
        return entry.map(LeaderboardEntry::getRank).orElse(0);
    }
    
    /**
     * Update a user's leaderboard entry when they earn rewards
     * @param user The user
     * @param amount The amount of tokens earned
     */
    @Transactional
    public void updateUserRewards(User user, BigDecimal amount) {
        // Update all periods
        for (LeaderboardPeriod period : LeaderboardPeriod.values()) {
            LeaderboardEntry entry = getOrCreateLeaderboardEntry(user, period);
            entry.addCycleBalance(amount);
            leaderboardRepository.save(entry);
        }
        
        // Recalculate ranks for all periods
        recalculateRanks();
    }
    
    /**
     * Update a user's leaderboard entry when they complete a cleanup
     * @param user The user
     */
    @Transactional
    public void incrementCleanupCount(User user) {
        // Update all periods
        for (LeaderboardPeriod period : LeaderboardPeriod.values()) {
            LeaderboardEntry entry = getOrCreateLeaderboardEntry(user, period);
            entry.incrementCleanupCount();
            leaderboardRepository.save(entry);
        }
        
        // Recalculate ranks for all periods
        recalculateRanks();
    }
    
    /**
     * Update a user's leaderboard entry when they adopt a spot
     * @param user The user
     */
    @Transactional
    public void incrementSpotAdoptions(User user) {
        // Update all periods
        for (LeaderboardPeriod period : LeaderboardPeriod.values()) {
            LeaderboardEntry entry = getOrCreateLeaderboardEntry(user, period);
            entry.incrementSpotAdoptions();
            leaderboardRepository.save(entry);
        }
        
        // Recalculate ranks for all periods
        recalculateRanks();
    }
    
    /**
     * Update a user's leaderboard entry when they make a marketplace sale
     * @param user The user
     */
    @Transactional
    public void incrementMarketplaceSales(User user) {
        // Update all periods
        for (LeaderboardPeriod period : LeaderboardPeriod.values()) {
            LeaderboardEntry entry = getOrCreateLeaderboardEntry(user, period);
            entry.incrementMarketplaceSales();
            leaderboardRepository.save(entry);
        }
        
        // Recalculate ranks for all periods
        recalculateRanks();
    }
    
    /**
     * Get or create a leaderboard entry for a user and period
     * @param user The user
     * @param period The leaderboard period
     * @return The leaderboard entry
     */
    private LeaderboardEntry getOrCreateLeaderboardEntry(User user, LeaderboardPeriod period) {
        return leaderboardRepository.findByUserAndPeriod(user, period)
                .orElseGet(() -> {
                    LeaderboardEntry entry = new LeaderboardEntry(user, period);
                    
                    // If it's ALL_TIME, get the token balance from the blockchain
                    if (period == LeaderboardPeriod.ALL_TIME && user.getWalletAddress() != null) {
                        try {
                            BigDecimal balance = blockchainService.getTokenBalance(user.getWalletAddress());
                            entry.setCycleBalance(balance);
                        } catch (Exception e) {
                            // If there's an error, just use 0
                            entry.setCycleBalance(BigDecimal.ZERO);
                        }
                    }
                    
                    return leaderboardRepository.save(entry);
                });
    }
    
    /**
     * Recalculate ranks for all periods
     */
    @Transactional
    public void recalculateRanks() {
        for (LeaderboardPeriod period : LeaderboardPeriod.values()) {
            List<LeaderboardEntry> entries = leaderboardRepository.findByPeriodOrderByRankAsc(period);
            
            // Sort by cycle balance (descending)
            entries.sort((a, b) -> b.getCycleBalance().compareTo(a.getCycleBalance()));
            
            // Update ranks
            for (int i = 0; i < entries.size(); i++) {
                LeaderboardEntry entry = entries.get(i);
                entry.setRank(i + 1);
                leaderboardRepository.save(entry);
            }
        }
    }
    
    /**
     * Reset weekly leaderboard every Monday at midnight
     */
    @Scheduled(cron = "0 0 0 * * MON")
    @Transactional
    public void resetWeeklyLeaderboard() {
        // Get all weekly entries
        List<LeaderboardEntry> entries = leaderboardRepository.findByPeriodOrderByRankAsc(LeaderboardPeriod.WEEKLY);
        
        // Reset all entries
        for (LeaderboardEntry entry : entries) {
            entry.setCycleBalance(BigDecimal.ZERO);
            entry.setCleanupCount(0);
            entry.setSpotAdoptions(0);
            entry.setMarketplaceSales(0);
            entry.setRank(0);
            entry.setUpdatedAt(LocalDateTime.now());
            leaderboardRepository.save(entry);
        }
    }
    
    /**
     * Reset monthly leaderboard on the first day of each month
     */
    @Scheduled(cron = "0 0 0 1 * *")
    @Transactional
    public void resetMonthlyLeaderboard() {
        // Get all monthly entries
        List<LeaderboardEntry> entries = leaderboardRepository.findByPeriodOrderByRankAsc(LeaderboardPeriod.MONTHLY);
        
        // Reset all entries
        for (LeaderboardEntry entry : entries) {
            entry.setCycleBalance(BigDecimal.ZERO);
            entry.setCleanupCount(0);
            entry.setSpotAdoptions(0);
            entry.setMarketplaceSales(0);
            entry.setRank(0);
            entry.setUpdatedAt(LocalDateTime.now());
            leaderboardRepository.save(entry);
        }
    }
    
    /**
     * Sync all-time leaderboard with blockchain token balances
     */
    @Scheduled(cron = "0 0 0 * * *") // Daily at midnight
    @Transactional
    public void syncAllTimeLeaderboard() {
        // Get all users
        List<User> users = userRepository.findAll();
        
        // Update token balances
        for (User user : users) {
            if (user.getWalletAddress() != null) {
                try {
                    BigDecimal balance = blockchainService.getTokenBalance(user.getWalletAddress());
                    
                    // Update all-time entry
                    LeaderboardEntry entry = getOrCreateLeaderboardEntry(user, LeaderboardPeriod.ALL_TIME);
                    entry.setCycleBalance(balance);
                    entry.setUpdatedAt(LocalDateTime.now());
                    leaderboardRepository.save(entry);
                } catch (Exception e) {
                    // Ignore errors and continue with next user
                }
            }
        }
        
        // Recalculate ranks
        recalculateRanks();
    }
}