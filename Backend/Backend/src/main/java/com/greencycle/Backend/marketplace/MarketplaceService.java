package com.greencycle.Backend.marketplace;

import com.greencycle.Backend.leaderboard.LeaderboardService;
import com.greencycle.Backend.user.User;
import com.greencycle.Backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MarketplaceService {

    @Autowired
    private MarketplaceItemRepository marketplaceItemRepository;
    
    @Autowired
    private ArtisanProfileRepository artisanProfileRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LeaderboardService leaderboardService;
    
    /**
     * Create a new marketplace item
     * @param request The item creation request
     * @param userEmail The email of the user creating the item
     * @return The created item
     */
    @Transactional
    public MarketplaceItem createItem(CreateItemRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        MarketplaceItem item = new MarketplaceItem();
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setCategory(request.getCategory());
        item.setConditionRating(request.getConditionRating());
        item.setLatitude(request.getLatitude());
        item.setLongitude(request.getLongitude());
        item.setPhotoUrls(request.getPhotoUrls());
        item.setPostedByUser(user);
        item.setPrice(request.getPrice());
        item.setCreatedAt(LocalDateTime.now());
        item.setStatus(MarketplaceItemStatus.AVAILABLE);
        
        return marketplaceItemRepository.save(item);
    }
    
    /**
     * Update an existing marketplace item
     * @param id The ID of the item to update
     * @param request The item update request
     * @param userEmail The email of the user updating the item
     * @return The updated item
     */
    @Transactional
    public MarketplaceItem updateItem(Long id, UpdateItemRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        MarketplaceItem item = marketplaceItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        
        // Check if the user is the owner of the item
        if (!item.getPostedByUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You are not authorized to update this item");
        }
        
        // Update the item
        if (request.getTitle() != null) {
            item.setTitle(request.getTitle());
        }
        
        if (request.getDescription() != null) {
            item.setDescription(request.getDescription());
        }
        
        if (request.getCategory() != null) {
            item.setCategory(request.getCategory());
        }
        
        if (request.getConditionRating() != null) {
            item.setConditionRating(request.getConditionRating());
        }
        
        if (request.getLatitude() != null) {
            item.setLatitude(request.getLatitude());
        }
        
        if (request.getLongitude() != null) {
            item.setLongitude(request.getLongitude());
        }
        
        if (request.getPhotoUrls() != null) {
            item.setPhotoUrls(request.getPhotoUrls());
        }
        
        if (request.getPrice() != null) {
            item.setPrice(request.getPrice());
        }
        
        if (request.getStatus() != null) {
            item.setStatus(request.getStatus());
            
            // If the item is marked as sold, update the leaderboard
            if (request.getStatus() == MarketplaceItemStatus.SOLD) {
                leaderboardService.incrementMarketplaceSales(user);
            }
        }
        
        return marketplaceItemRepository.save(item);
    }
    
    /**
     * Get a marketplace item by ID
     * @param id The ID of the item
     * @return The item, if found
     */
    public Optional<MarketplaceItem> getItem(Long id) {
        return marketplaceItemRepository.findById(id);
    }
    
    /**
     * Get all marketplace items, paginated
     * @param page The page number
     * @param size The page size
     * @return Page of marketplace items
     */
    public Page<MarketplaceItem> getAllItems(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return marketplaceItemRepository.findAll(pageable);
    }
    
    /**
     * Get marketplace items by status, paginated
     * @param status The status to filter by
     * @param page The page number
     * @param size The page size
     * @return Page of marketplace items
     */
    public Page<MarketplaceItem> getItemsByStatus(MarketplaceItemStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return marketplaceItemRepository.findByStatus(status, pageable);
    }
    
    /**
     * Get marketplace items by category, paginated
     * @param category The category to filter by
     * @param page The page number
     * @param size The page size
     * @return Page of marketplace items
     */
    public Page<MarketplaceItem> getItemsByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return marketplaceItemRepository.findByCategory(category, pageable);
    }
    
    /**
     * Get marketplace items by user, paginated
     * @param userEmail The email of the user
     * @param page The page number
     * @param size The page size
     * @return Page of marketplace items
     */
    public Page<MarketplaceItem> getItemsByUser(String userEmail, int page, int size) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return marketplaceItemRepository.findByPostedByUser(user, pageable);
    }
    
    /**
     * Search for marketplace items, paginated
     * @param searchTerm The search term
     * @param page The page number
     * @param size The page size
     * @return Page of marketplace items
     */
    public Page<MarketplaceItem> searchItems(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return marketplaceItemRepository.searchItems(searchTerm, pageable);
    }
    
    /**
     * Get marketplace items near a location, paginated
     * @param latitude The latitude of the location
     * @param longitude The longitude of the location
     * @param radiusKm The radius in kilometers
     * @param page The page number
     * @param size The page size
     * @return Page of marketplace items
     */
    public Page<MarketplaceItem> getItemsNearLocation(BigDecimal latitude, BigDecimal longitude, Double radiusKm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return marketplaceItemRepository.findItemsNearLocation(latitude, longitude, radiusKm, pageable);
    }
    
    /**
     * Delete a marketplace item
     * @param id The ID of the item to delete
     * @param userEmail The email of the user deleting the item
     */
    @Transactional
    public void deleteItem(Long id, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        MarketplaceItem item = marketplaceItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        
        // Check if the user is the owner of the item
        if (!item.getPostedByUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You are not authorized to delete this item");
        }
        
        marketplaceItemRepository.delete(item);
    }
    
    /**
     * Create or update an artisan profile
     * @param request The artisan profile request
     * @param userEmail The email of the user
     * @return The created or updated artisan profile
     */
    @Transactional
    public ArtisanProfile createOrUpdateArtisanProfile(ArtisanProfileRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Check if the user already has an artisan profile
        Optional<ArtisanProfile> existingProfile = artisanProfileRepository.findByUser(user);
        
        ArtisanProfile profile;
        if (existingProfile.isPresent()) {
            profile = existingProfile.get();
        } else {
            profile = new ArtisanProfile();
            profile.setUser(user);
            profile.setCreatedAt(LocalDateTime.now());
        }
        
        // Update the profile
        profile.setBusinessName(request.getBusinessName());
        profile.setDescription(request.getDescription());
        profile.setSpecialties(request.getSpecialties());
        profile.setPortfolioUrls(request.getPortfolioUrls());
        
        return artisanProfileRepository.save(profile);
    }
    
    /**
     * Get an artisan profile by user email
     * @param userEmail The email of the user
     * @return The artisan profile, if found
     */
    public Optional<ArtisanProfile> getArtisanProfileByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return artisanProfileRepository.findByUser(user);
    }
    
    /**
     * Get all verified artisan profiles, paginated
     * @param page The page number
     * @param size The page size
     * @return Page of verified artisan profiles
     */
    public Page<ArtisanProfile> getVerifiedArtisanProfiles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("rating").descending());
        return artisanProfileRepository.findByVerifiedTrue(pageable);
    }
    
    /**
     * Search for artisan profiles, paginated
     * @param searchTerm The search term
     * @param page The page number
     * @param size The page size
     * @return Page of artisan profiles
     */
    public Page<ArtisanProfile> searchArtisanProfiles(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("rating").descending());
        return artisanProfileRepository.searchArtisans(searchTerm, pageable);
    }
    
    /**
     * Get artisan profiles by specialty, paginated
     * @param specialty The specialty to filter by
     * @param page The page number
     * @param size The page size
     * @return Page of artisan profiles
     */
    public Page<ArtisanProfile> getArtisanProfilesBySpecialty(String specialty, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("rating").descending());
        return artisanProfileRepository.findBySpecialty(specialty, pageable);
    }
    
    /**
     * Get top rated artisan profiles
     * @param limit The maximum number of profiles to return
     * @return List of top rated artisan profiles
     */
    public List<ArtisanProfile> getTopRatedArtisanProfiles(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("rating").descending());
        return artisanProfileRepository.findTopRatedArtisans(pageable);
    }
    
    /**
     * Verify an artisan profile (admin only)
     * @param id The ID of the profile to verify
     */
    @Transactional
    public void verifyArtisanProfile(Long id) {
        ArtisanProfile profile = artisanProfileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Artisan profile not found"));
        
        profile.verify();
        artisanProfileRepository.save(profile);
    }
    
    /**
     * Unverify an artisan profile (admin only)
     * @param id The ID of the profile to unverify
     */
    @Transactional
    public void unverifyArtisanProfile(Long id) {
        ArtisanProfile profile = artisanProfileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Artisan profile not found"));
        
        profile.unverify();
        artisanProfileRepository.save(profile);
    }
    
    /**
     * Update an artisan profile's rating
     * @param id The ID of the profile to update
     * @param rating The new rating
     */
    @Transactional
    public void updateArtisanProfileRating(Long id, BigDecimal rating) {
        ArtisanProfile profile = artisanProfileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Artisan profile not found"));
        
        profile.setRating(rating);
        artisanProfileRepository.save(profile);
    }
    
    // Request classes
    
    public static class CreateItemRequest {
        private String title;
        private String description;
        private String category;
        private Integer conditionRating;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private List<String> photoUrls;
        private BigDecimal price;
        
        // Getters and Setters
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
        
        public BigDecimal getPrice() {
            return price;
        }
        
        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }
    
    public static class UpdateItemRequest {
        private String title;
        private String description;
        private String category;
        private Integer conditionRating;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private List<String> photoUrls;
        private BigDecimal price;
        private MarketplaceItemStatus status;
        
        // Getters and Setters
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
        
        public BigDecimal getPrice() {
            return price;
        }
        
        public void setPrice(BigDecimal price) {
            this.price = price;
        }
        
        public MarketplaceItemStatus getStatus() {
            return status;
        }
        
        public void setStatus(MarketplaceItemStatus status) {
            this.status = status;
        }
    }
    
    public static class ArtisanProfileRequest {
        private String businessName;
        private String description;
        private List<String> specialties;
        private List<String> portfolioUrls;
        
        // Getters and Setters
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
    }
}