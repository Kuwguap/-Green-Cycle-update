package com.greencycle.Backend.marketplace;

import com.greencycle.Backend.user.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "marketplace_items")
public class MarketplaceItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "condition_rating")
    private Integer conditionRating; // 1-5 stars
    
    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;
    
    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;
    
    @ElementCollection
    @CollectionTable(name = "marketplace_item_photos", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "photo_url")
    private List<String> photoUrls = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MarketplaceItemStatus status = MarketplaceItemStatus.AVAILABLE;
    
    @ManyToOne
    @JoinColumn(name = "posted_by_user_id")
    private User postedByUser;
    
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Constructors
    public MarketplaceItem() {}
    
    public MarketplaceItem(String title, String description, String category, Integer conditionRating,
                          BigDecimal latitude, BigDecimal longitude, List<String> photoUrls,
                          User postedByUser, BigDecimal price) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.conditionRating = conditionRating;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photoUrls = photoUrls;
        this.postedByUser = postedByUser;
        this.price = price;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public Integer getConditionRating() {
        return conditionRating;
    }
    
    public void setConditionRating(Integer conditionRating) {
        this.conditionRating = conditionRating;
    }
    
    public BigDecimal getLatitude() {
        return latitude;
    }
    
    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }
    
    public BigDecimal getLongitude() {
        return longitude;
    }
    
    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
    
    public List<String> getPhotoUrls() {
        return photoUrls;
    }
    
    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }
    
    public MarketplaceItemStatus getStatus() {
        return status;
    }
    
    public void setStatus(MarketplaceItemStatus status) {
        this.status = status;
    }
    
    public User getPostedByUser() {
        return postedByUser;
    }
    
    public void setPostedByUser(User postedByUser) {
        this.postedByUser = postedByUser;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Helper methods
    public void addPhotoUrl(String photoUrl) {
        this.photoUrls.add(photoUrl);
    }
    
    public void removePhotoUrl(String photoUrl) {
        this.photoUrls.remove(photoUrl);
    }
    
    public boolean isAvailable() {
        return this.status == MarketplaceItemStatus.AVAILABLE;
    }
    
    public boolean isReserved() {
        return this.status == MarketplaceItemStatus.RESERVED;
    }
    
    public boolean isSold() {
        return this.status == MarketplaceItemStatus.SOLD;
    }
    
    public void markAsReserved() {
        this.status = MarketplaceItemStatus.RESERVED;
    }
    
    public void markAsSold() {
        this.status = MarketplaceItemStatus.SOLD;
    }
    
    public void markAsAvailable() {
        this.status = MarketplaceItemStatus.AVAILABLE;
    }
}