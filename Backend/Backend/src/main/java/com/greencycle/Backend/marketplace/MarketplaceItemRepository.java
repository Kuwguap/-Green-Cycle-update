package com.greencycle.Backend.marketplace;

import com.greencycle.Backend.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MarketplaceItemRepository extends JpaRepository<MarketplaceItem, Long> {
    
    /**
     * Find all marketplace items with a specific status
     * @param status The status to filter by
     * @return List of marketplace items
     */
    List<MarketplaceItem> findByStatus(MarketplaceItemStatus status);
    
    /**
     * Find all marketplace items with a specific status, paginated
     * @param status The status to filter by
     * @param pageable Pagination information
     * @return Page of marketplace items
     */
    Page<MarketplaceItem> findByStatus(MarketplaceItemStatus status, Pageable pageable);
    
    /**
     * Find all marketplace items in a specific category
     * @param category The category to filter by
     * @return List of marketplace items
     */
    List<MarketplaceItem> findByCategory(String category);
    
    /**
     * Find all marketplace items in a specific category, paginated
     * @param category The category to filter by
     * @param pageable Pagination information
     * @return Page of marketplace items
     */
    Page<MarketplaceItem> findByCategory(String category, Pageable pageable);
    
    /**
     * Find all marketplace items posted by a specific user
     * @param user The user who posted the items
     * @return List of marketplace items
     */
    List<MarketplaceItem> findByPostedByUser(User user);
    
    /**
     * Find all marketplace items posted by a specific user, paginated
     * @param user The user who posted the items
     * @param pageable Pagination information
     * @return Page of marketplace items
     */
    Page<MarketplaceItem> findByPostedByUser(User user, Pageable pageable);
    
    /**
     * Find all marketplace items in a specific category with a specific status
     * @param category The category to filter by
     * @param status The status to filter by
     * @return List of marketplace items
     */
    List<MarketplaceItem> findByCategoryAndStatus(String category, MarketplaceItemStatus status);
    
    /**
     * Find all marketplace items in a specific category with a specific status, paginated
     * @param category The category to filter by
     * @param status The status to filter by
     * @param pageable Pagination information
     * @return Page of marketplace items
     */
    Page<MarketplaceItem> findByCategoryAndStatus(String category, MarketplaceItemStatus status, Pageable pageable);
    
    /**
     * Find all marketplace items within a specific radius of a location
     * @param latitude The latitude of the location
     * @param longitude The longitude of the location
     * @param radiusKm The radius in kilometers
     * @return List of marketplace items
     */
    @Query(value = "SELECT * FROM marketplace_items " +
            "WHERE (6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) * " +
            "cos(radians(longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
            "sin(radians(latitude)))) <= :radiusKm", nativeQuery = true)
    List<MarketplaceItem> findItemsNearLocation(
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("radiusKm") Double radiusKm);
    
    /**
     * Find all marketplace items within a specific radius of a location, paginated
     * @param latitude The latitude of the location
     * @param longitude The longitude of the location
     * @param radiusKm The radius in kilometers
     * @param pageable Pagination information
     * @return Page of marketplace items
     */
    @Query(value = "SELECT * FROM marketplace_items " +
            "WHERE (6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) * " +
            "cos(radians(longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
            "sin(radians(latitude)))) <= :radiusKm", 
            countQuery = "SELECT count(*) FROM marketplace_items " +
                    "WHERE (6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) * " +
                    "cos(radians(longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
                    "sin(radians(latitude)))) <= :radiusKm",
            nativeQuery = true)
    Page<MarketplaceItem> findItemsNearLocation(
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("radiusKm") Double radiusKm,
            Pageable pageable);
    
    /**
     * Search for marketplace items by title or description
     * @param searchTerm The search term
     * @return List of marketplace items
     */
    @Query("SELECT m FROM MarketplaceItem m WHERE " +
            "LOWER(m.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(m.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<MarketplaceItem> searchItems(@Param("searchTerm") String searchTerm);
    
    /**
     * Search for marketplace items by title or description, paginated
     * @param searchTerm The search term
     * @param pageable Pagination information
     * @return Page of marketplace items
     */
    @Query("SELECT m FROM MarketplaceItem m WHERE " +
            "LOWER(m.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(m.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<MarketplaceItem> searchItems(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Count the number of items sold by a user
     * @param user The user
     * @return The count
     */
    long countByPostedByUserAndStatus(User user, MarketplaceItemStatus status);
}