package com.greencycle.Backend.bin;

import java.time.LocalDateTime;

public class BinReportResponse {
    private Long id;
    private Long binId;
    private Long userId;
    private String reportType;
    private String photoUrl;
    private String description;
    private LocalDateTime createdAt;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBinId() { return binId; }
    public void setBinId(Long binId) { this.binId = binId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
} 