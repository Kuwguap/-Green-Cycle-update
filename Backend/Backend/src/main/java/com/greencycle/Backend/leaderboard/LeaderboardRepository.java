package com.greencycle.Backend.leaderboard;

import com.greencycle.Backend.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaderboardRepository extends JpaRepository<LeaderboardEntry, Long> {
    
    /**
     * Find a leaderboard entry by user and period
     * @param user The user
     * @param period The leaderboard period
     * @return The leaderboard entry, if found
     */
    Optional<LeaderboardEntry> findByUserAndPeriod(User user, LeaderboardPeriod period);
    
    /**
     * Find all leaderboard entries for a specific period, ordered by rank
     * @param period The leaderboard period
     * @return List of leaderboard entries
     */
    List<LeaderboardEntry> findByPeriodOrderByRankAsc(LeaderboardPeriod period);
    
    /**
     * Find all leaderboard entries for a specific period, paginated and ordered by rank
     * @param period The leaderboard period
     * @param pageable Pagination information
     * @return Page of leaderboard entries
     */
    Page<LeaderboardEntry> findByPeriodOrderByRankAsc(LeaderboardPeriod period, Pageable pageable);
    
    /**
     * Find the top N leaderboard entries for a specific period
     * @param period The leaderboard period
     * @param limit The maximum number of entries to return
     * @return List of leaderboard entries
     */
    @Query("SELECT l FROM LeaderboardEntry l WHERE l.period = ?1 ORDER BY l.rank ASC LIMIT ?2")
    List<LeaderboardEntry> findTopByPeriod(LeaderboardPeriod period, int limit);
    
    /**
     * Find all leaderboard entries for a specific user
     * @param user The user
     * @return List of leaderboard entries
     */
    List<LeaderboardEntry> findByUser(User user);
    
    /**
     * Count the number of entries for a specific period
     * @param period The leaderboard period
     * @return The count
     */
    long countByPeriod(LeaderboardPeriod period);
}