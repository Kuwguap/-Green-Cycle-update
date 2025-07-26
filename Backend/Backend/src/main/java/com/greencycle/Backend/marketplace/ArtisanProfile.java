package com.greencycle.Backend.marketplace;

import com.greencycle.Backend.user.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "artisan_profiles")
public class ArtisanProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "business_name")
    private String businessName;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @ElementCollection
    @CollectionTable(name = "artisan_specialties", joinColumns = @JoinColumn(name = "artisan_id"))
    @Column(name = "specialty")
    private List<String> specialties = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "artisan_portfolio", joinColumns = @JoinColumn(name = "artisan_id"))
    @Column(name = "portfolio_url")
    private List<String> portfolioUrls = new ArrayList<>();
    
    @Column(name = "rating", precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.ZERO;
    
    @Column(name = "verified")
    private Boolean verified = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Constructors
    public ArtisanProfile() {}
    
    public ArtisanProfile(User user, String businessName, String description) {
        this.user = user;
        this.businessName = businessName;
        this.description = description;
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
    
    public String getBusinessName() {
        return businessName;
    }
    
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<String> getSpecialties() {
        return specialties;
    }
    
    public void setSpecialties(List<String> specialties) {
        this.specialties = specialties;
    }
    
    public List<String> getPortfolioUrls() {
        return portfolioUrls;
    }
    
    public void setPortfolioUrls(List<String> portfolioUrls) {
        this.portfolioUrls = portfolioUrls;
    }
    
    public BigDecimal getRating() {
        return rating;
    }
    
    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }
    
    public Boolean getVerified() {
        return verified;
    }
    
    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Helper methods
    public void addSpecialty(String specialty) {
        this.specialties.add(specialty);
    }
    
    public void removeSpecialty(String specialty) {
        this.specialties.remove(specialty);
    }
    
    public void addPortfolioUrl(String portfolioUrl) {
        this.portfolioUrls.add(portfolioUrl);
    }
    
    public void removePortfolioUrl(String portfolioUrl) {
        this.portfolioUrls.remove(portfolioUrl);
    }
    
    public void verify() {
        this.verified = true;
    }
    
    public void unverify() {
        this.verified = false;
    }
    
    public boolean isVerified() {
        return this.verified;
    }
}