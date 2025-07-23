package com.greencycle.Backend.bin;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bins")
public class Bin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "qr_code_id", unique = true, nullable = false)
    private String qrCodeId;

    @Column(name = "location_name", nullable = false)
    private String locationName;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private String status = "EMPTY";

    @Column(name = "last_reported")
    private LocalDateTime lastReported;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getQrCodeId() { return qrCodeId; }
    public void setQrCodeId(String qrCodeId) { this.qrCodeId = qrCodeId; }
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getLastReported() { return lastReported; }
    public void setLastReported(LocalDateTime lastReported) { this.lastReported = lastReported; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
} 