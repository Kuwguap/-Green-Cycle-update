package com.greencycle.Backend.bin;

public class BinReportRequest {
    private String qrCodeId;
    private Long userId;
    private String reportType;
    private String photoUrl;
    private String description;

    // Getters and setters
    public String getQrCodeId() { return qrCodeId; }
    public void setQrCodeId(String qrCodeId) { this.qrCodeId = qrCodeId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
} 