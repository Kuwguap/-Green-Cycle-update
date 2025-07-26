package com.greencycle.Backend.leaderboard;

import com.greencycle.Backend.user.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "leaderboard_entries")
public class LeaderboardEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "period")
    private LeaderboardPeriod period;
    
    @Column(name = "cycle_balance")
    private BigDecimal cycleBalance;
    
    @Column(name = "cleanup_count")
    private Integer cleanupCount = 0;
    
    @Column(name = "spot_adoptions")
    private Integer spotAdoptions = 0;
    
    @Column(name = "marketplace_sales")
    private Integer marketplaceSales = 0;
    
    @Column(name = "rank")
    private Integer rank;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Constructors
    public LeaderboardEntry() {}
    
    public LeaderboardEntry(User user, LeaderboardPeriod period) {
        this.user = user;
        this.period = period;
        this.cycleBalance = BigDecimal.ZERO;
        this.rank = 0;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public LeaderboardPeriod getPeriod() {
        return period;
    }
    
    public void setPeriod(LeaderboardPeriod period) {
        this.period = period;
    }
    
    public BigDecimal getCycleBalance() {
        return cycleBalance;
    }
    
    public void setCycleBalance(BigDecimal cycleBalance) {
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
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Helper methods
    public void incrementCleanupCount() {
        this.cleanupCount++;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void incrementSpotAdoptions() {
        this.spotAdoptions++;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void incrementMarketplaceSales() {
        this.marketplaceSales++;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addCycleBalance(BigDecimal amount) {
        this.cycleBalance = this.cycleBalance.add(amount);
        this.updatedAt = LocalDateTime.now();
    }
}