package com.greencycle.Backend.marketplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/marketplace")
@CrossOrigin(origins = "*")
public class MarketplaceController {

    @Autowired
    private MarketplaceService marketplaceService;
    
    /**
     * Get all marketplace items, paginated
     * @param page The page number (default: 0)
     * @param size The page size (default: 10)
     * @return Page of marketplace items
     */
    @GetMapping("/items")
    public ResponseEntity<Page<MarketplaceItemDTO>> getAllItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<MarketplaceItem> items = marketplaceService.getAllItems(page, size);
        Page<MarketplaceItemDTO> dtos = items.map(this::convertToItemDTO);
        
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Get marketplace items by status, paginated
     * @param status The status to filter by (AVAILABLE, RESERVED, SOLD)
     * @param page The page number (default: 0)
     * @param size The page size (default: 10)
     * @return Page of marketplace items
     */
    @GetMapping("/items/status/{status}")
    public ResponseEntity<Page<MarketplaceItemDTO>> getItemsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            MarketplaceItemStatus itemStatus = MarketplaceItemStatus.valueOf(status.toUpperCase());
            Page<MarketplaceItem> items = marketplaceService.getItemsByStatus(itemStatus, page, size);
            Page<MarketplaceItemDTO> dtos = items.map(this::convertToItemDTO);
            
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get marketplace items by category, paginated
     * @param category The category to filter by
     * @param page The page number (default: 0)
     * @param size The page size (default: 10)
     * @return Page of marketplace items
     */
    @GetMapping("/items/category/{category}")
    public ResponseEntity<Page<MarketplaceItemDTO>> getItemsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<MarketplaceItem> items = marketplaceService.getItemsByCategory(category, page, size);
        Page<MarketplaceItemDTO> dtos = items.map(this::convertToItemDTO);
        
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Get marketplace items near a location, paginated
     * @param latitude The latitude of the location
     * @param longitude The longitude of the location
     * @param radiusKm The radius in kilometers (default: 10.0)
     * @param page The page number (default: 0)
     * @param size The page size (default: 10)
     * @return Page of marketplace items
     */
    @GetMapping("/items/nearby")
    public ResponseEntity<Page<MarketplaceItemDTO>> getItemsNearLocation(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam(defaultValue = "10.0") Double radiusKm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<MarketplaceItem> items = marketplaceService.getItemsNearLocation(latitude, longitude, radiusKm, page, size);
        Page<MarketplaceItemDTO> dtos = items.map(this::convertToItemDTO);
        
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Search for marketplace items, paginated
     * @param query The search query
     * @param page The page number (default: 0)
     * @param size The page size (default: 10)
     * @return Page of marketplace items
     */
    @GetMapping("/items/search")
    public ResponseEntity<Page<MarketplaceItemDTO>> searchItems(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<MarketplaceItem> items = marketplaceService.searchItems(query, page, size);
        Page<MarketplaceItemDTO> dtos = items.map(this::convertToItemDTO);
        
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Get a marketplace item by ID
     * @param id The ID of the item
     * @return The item, if found
     */
    @GetMapping("/items/{id}")
    public ResponseEntity<MarketplaceItemDTO> getItem(@PathVariable Long id) {
        Optional<MarketplaceItem> itemOpt = marketplaceService.getItem(id);
        
        if (itemOpt.isPresent()) {
            MarketplaceItemDTO dto = convertToItemDTO(itemOpt.get());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get marketplace items posted by the authenticated user, paginated
     * @param userDetails The authenticated user
     * @param page The page number (default: 0)
     * @param size The page size (default: 10)
     * @return Page of marketplace items
     */
    @GetMapping("/items/my-items")
    public ResponseEntity<Page<MarketplaceItemDTO>> getMyItems(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<MarketplaceItem> items = marketplaceService.getItemsByUser(userDetails.getUsername(), page, size);
        Page<MarketplaceItemDTO> dtos = items.map(this::convertToItemDTO);
        
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Create a new marketplace item
     * @param request The item creation request
     * @param userDetails The authenticated user
     * @return The created item
     */
    @PostMapping("/items")
    public ResponseEntity<MarketplaceItemDTO> createItem(
            @RequestBody MarketplaceService.CreateItemRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        MarketplaceItem item = marketplaceService.createItem(request, userDetails.getUsername());
        MarketplaceItemDTO dto = convertToItemDTO(item);
        
        return ResponseEntity.ok(dto);
    }
    
    /**
     * Update an existing marketplace item
     * @param id The ID of the item to update
     * @param request The item update request
     * @param userDetails The authenticated user
     * @return The updated item
     */
    @PutMapping("/items/{id}")
    public ResponseEntity<MarketplaceItemDTO> updateItem(
            @PathVariable Long id,
            @RequestBody MarketplaceService.UpdateItemRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            MarketplaceItem item = marketplaceService.updateItem(id, request, userDetails.getUsername());
            MarketplaceItemDTO dto = convertToItemDTO(item);
            
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Delete a marketplace item
     * @param id The ID of the item to delete
     * @param userDetails The authenticated user
     * @return Success message
     */
    @DeleteMapping("/items/{id}")
    public ResponseEntity<Map<String, String>> deleteItem(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            marketplaceService.deleteItem(id, userDetails.getUsername());
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Item deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get all verified artisan profiles, paginated
     * @param page The page number (default: 0)
     * @param size The page size (default: 10)
     * @return Page of artisan profiles
     */
    @GetMapping("/artisans")
    public ResponseEntity<Page<ArtisanProfileDTO>> getVerifiedArtisanProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<ArtisanProfile> profiles = marketplaceService.getVerifiedArtisanProfiles(page, size);
        Page<ArtisanProfileDTO> dtos = profiles.map(this::convertToProfileDTO);
        
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Search for artisan profiles, paginated
     * @param query The search query
     * @param page The page number (default: 0)
     * @param size The page size (default: 10)
     * @return Page of artisan profiles
     */
    @GetMapping("/artisans/search")
    public ResponseEntity<Page<ArtisanProfileDTO>> searchArtisanProfiles(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<ArtisanProfile> profiles = marketplaceService.searchArtisanProfiles(query, page, size);
        Page<ArtisanProfileDTO> dtos = profiles.map(this::convertToProfileDTO);
        
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Get artisan profiles by specialty, paginated
     * @param specialty The specialty to filter by
     * @param page The page number (default: 0)
     * @param size The page size (default: 10)
     * @return Page of artisan profiles
     */
    @GetMapping("/artisans/specialty/{specialty}")
    public ResponseEntity<Page<ArtisanProfileDTO>> getArtisanProfilesBySpecialty(
            @PathVariable String specialty,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<ArtisanProfile> profiles = marketplaceService.getArtisanProfilesBySpecialty(specialty, page, size);
        Page<ArtisanProfileDTO> dtos = profiles.map(this::convertToProfileDTO);
        
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Get top rated artisan profiles
     * @param limit The maximum number of profiles to return (default: 5)
     * @return List of artisan profiles
     */
    @GetMapping("/artisans/top-rated")
    public ResponseEntity<List<ArtisanProfileDTO>> getTopRatedArtisanProfiles(
            @RequestParam(defaultValue = "5") int limit) {
        
        List<ArtisanProfile> profiles = marketplaceService.getTopRatedArtisanProfiles(limit);
        List<ArtisanProfileDTO> dtos = profiles.stream()
                .map(this::convertToProfileDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Get the authenticated user's artisan profile
     * @param userDetails The authenticated user
     * @return The artisan profile, if found
     */
    @GetMapping("/artisans/my-profile")
    public ResponseEntity<ArtisanProfileDTO> getMyArtisanProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Optional<ArtisanProfile> profileOpt = marketplaceService.getArtisanProfileByUser(userDetails.getUsername());
        
        if (profileOpt.isPresent()) {
            ArtisanProfileDTO dto = convertToProfileDTO(profileOpt.get());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Create or update the authenticated user's artisan profile
     * @param request The artisan profile request
     * @param userDetails The authenticated user
     * @return The created or updated artisan profile
     */
    @PostMapping("/artisans/my-profile")
    public ResponseEntity<ArtisanProfileDTO> createOrUpdateArtisanProfile(
            @RequestBody MarketplaceService.ArtisanProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        ArtisanProfile profile = marketplaceService.createOrUpdateArtisanProfile(request, userDetails.getUsername());
        ArtisanProfileDTO dto = convertToProfileDTO(profile);
        
        return ResponseEntity.ok(dto);
    }
    
    /**
     * Verify an artisan profile (admin only)
     * @param id The ID of the profile to verify
     * @return Success message
     */
    @PostMapping("/artisans/{id}/verify")
    public ResponseEntity<Map<String, String>> verifyArtisanProfile(@PathVariable Long id) {
        try {
            marketplaceService.verifyArtisanProfile(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Artisan profile verified successfully");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Unverify an artisan profile (admin only)
     * @param id The ID of the profile to unverify
     * @return Success message
     */
    @PostMapping("/artisans/{id}/unverify")
    public ResponseEntity<Map<String, String>> unverifyArtisanProfile(@PathVariable Long id) {
        try {
            marketplaceService.unverifyArtisanProfile(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Artisan profile unverified successfully");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Update an artisan profile's rating
     * @param id The ID of the profile to update
     * @param rating The new rating
     * @return Success message
     */
    @PostMapping("/artisans/{id}/rating")
    public ResponseEntity<Map<String, String>> updateArtisanProfileRating(
            @PathVariable Long id,
            @RequestParam BigDecimal rating) {
        
        try {
            marketplaceService.updateArtisanProfileRating(id, rating);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Artisan profile rating updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Convert a MarketplaceItem to a MarketplaceItemDTO
     * @param item The MarketplaceItem
     * @return The MarketplaceItemDTO
     */
    private MarketplaceItemDTO convertToItemDTO(MarketplaceItem item) {
        MarketplaceItemDTO dto = new MarketplaceItemDTO();
        dto.setId(item.getId());
        dto.setTitle(item.getTitle());
        dto.setDescription(item.getDescription());
        dto.setCategory(item.getCategory());
        dto.setConditionRating(item.getConditionRating());
        dto.setLatitude(item.getLatitude());
        dto.setLongitude(item.getLongitude());
        dto.setPhotoUrls(item.getPhotoUrls());
        dto.setStatus(item.getStatus().name());
        dto.setPostedByUserId(item.getPostedByUser().getId());
        dto.setPostedByUserName(item.getPostedByUser().getName());
        dto.setPrice(item.getPrice());
        dto.setCreatedAt(item.getCreatedAt());
        return dto;
    }
    
    /**
     * Convert an ArtisanProfile to an ArtisanProfileDTO
     * @param profile The ArtisanProfile
     * @return The ArtisanProfileDTO
     */
    private ArtisanProfileDTO convertToProfileDTO(ArtisanProfile profile) {
        ArtisanProfileDTO dto = new ArtisanProfileDTO();
        dto.setId(profile.getId());
        dto.setUserId(profile.getUser().getId());
        dto.setUserName(profile.getUser().getName());
        dto.setBusinessName(profile.getBusinessName());
        dto.setDescription(profile.getDescription());
        dto.setSpecialties(profile.getSpecialties());
        dto.setPortfolioUrls(profile.getPortfolioUrls());
        dto.setRating(profile.getRating());
        dto.setVerified(profile.getVerified());
        dto.setCreatedAt(profile.getCreatedAt());
        return dto;
    }
    
    /**
     * DTO for MarketplaceItem
     */
    public static class MarketplaceItemDTO {
        private Long id;
        private String title;
        private String description;
        private String category;
        private Integer conditionRating;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private List<String> photoUrls;
        private String status;
        private Long postedByUserId;
        private String postedByUserName;
        private BigDecimal price;
        private java.time.LocalDateTime createdAt;
        
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
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public Long getPostedByUserId() {
            return postedByUserId;
        }
        
        public void setPostedByUserId(Long postedByUserId) {
            this.postedByUserId = postedByUserId;
        }
        
        public String getPostedByUserName() {
            return postedByUserName;
        }
        
        public void setPostedByUserName(String postedByUserName) {
            this.postedByUserName = postedByUserName;
        }
        
        public BigDecimal getPrice() {
            return price;
        }
        
        public void setPrice(BigDecimal price) {
            this.price = price;
        }
        
        public java.time.LocalDateTime getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(java.time.LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }
    
    /**
     * DTO for ArtisanProfile
     */
    public static class ArtisanProfileDTO {
        private Long id;
        private Long userId;
        private String userName;
        private String businessName;
        private String description;
        private List<String> specialties;
        private List<String> portfolioUrls;
        private BigDecimal rating;
        private Boolean verified;
        private java.time.LocalDateTime createdAt;
        
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
        
        public String getUserName() {
            return userName;
        }
        
        public void setUserName(String userName) {
            this.userName = userName;
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
        
        public java.time.LocalDateTime getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(java.time.LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }
}