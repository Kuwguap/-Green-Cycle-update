package com.greencycle.Backend.marketplace;

import com.greencycle.Backend.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtisanProfileRepository extends JpaRepository<ArtisanProfile, Long> {
    
    /**
     * Find an artisan profile by user
     * @param user The user
     * @return The artisan profile, if found
     */
    Optional<ArtisanProfile> findByUser(User user);
    
    /**
     * Find all verified artisan profiles
     * @return List of verified artisan profiles
     */
    List<ArtisanProfile> findByVerifiedTrue();
    
    /**
     * Find all verified artisan profiles, paginated
     * @param pageable Pagination information
     * @return Page of verified artisan profiles
     */
    Page<ArtisanProfile> findByVerifiedTrue(Pageable pageable);
    
    /**
     * Find all artisan profiles with a specific specialty
     * @param specialty The specialty to filter by
     * @return List of artisan profiles
     */
    @Query("SELECT a FROM ArtisanProfile a JOIN a.specialties s WHERE s = :specialty")
    List<ArtisanProfile> findBySpecialty(@Param("specialty") String specialty);
    
    /**
     * Find all artisan profiles with a specific specialty, paginated
     * @param specialty The specialty to filter by
     * @param pageable Pagination information
     * @return Page of artisan profiles
     */
    @Query("SELECT a FROM ArtisanProfile a JOIN a.specialties s WHERE s = :specialty")
    Page<ArtisanProfile> findBySpecialty(@Param("specialty") String specialty, Pageable pageable);
    
    /**
     * Search for artisan profiles by business name or description
     * @param searchTerm The search term
     * @return List of artisan profiles
     */
    @Query("SELECT a FROM ArtisanProfile a WHERE " +
            "LOWER(a.businessName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<ArtisanProfile> searchArtisans(@Param("searchTerm") String searchTerm);
    
    /**
     * Search for artisan profiles by business name or description, paginated
     * @param searchTerm The search term
     * @param pageable Pagination information
     * @return Page of artisan profiles
     */
    @Query("SELECT a FROM ArtisanProfile a WHERE " +
            "LOWER(a.businessName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<ArtisanProfile> searchArtisans(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Check if a user has an artisan profile
     * @param user The user
     * @return True if the user has an artisan profile, false otherwise
     */
    boolean existsByUser(User user);
    
    /**
     * Find top rated artisan profiles
     * @param limit The maximum number of profiles to return
     * @return List of top rated artisan profiles
     */
    @Query("SELECT a FROM ArtisanProfile a WHERE a.verified = true ORDER BY a.rating DESC")
    List<ArtisanProfile> findTopRatedArtisans(Pageable pageable);
}