package com.greencycle.Backend.bin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BinService {
    private final BinRepository binRepository;
    private final BinReportRepository binReportRepository;

    @Autowired
    public BinService(BinRepository binRepository, BinReportRepository binReportRepository) {
        this.binRepository = binRepository;
        this.binReportRepository = binReportRepository;
    }

    public Bin registerBin(Bin bin) {
        return binRepository.save(bin);
    }

    public BinReportResponse submitReport(BinReportRequest request) {
        Optional<Bin> binOpt = binRepository.findByQrCodeId(request.getQrCodeId());
        Bin bin = binOpt.orElseThrow(() -> new RuntimeException("Bin not found"));
        BinReport report = new BinReport();
        report.setBin(bin);
        report.setUserId(request.getUserId());
        report.setReportType(request.getReportType());
        report.setPhotoUrl(request.getPhotoUrl());
        report.setDescription(request.getDescription());
        BinReport saved = binReportRepository.save(report);
        bin.setLastReported(saved.getCreatedAt());
        bin.setStatus(request.getReportType() != null ? request.getReportType() : bin.getStatus());
        binRepository.save(bin);
        return toResponse(saved);
    }

    public List<BinReportResponse> getReportsForBin(Long binId) {
        List<BinReport> reports = binReportRepository.findAllByBin_Id(binId);
        return reports.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private BinReportResponse toResponse(BinReport report) {
        BinReportResponse resp = new BinReportResponse();
        resp.setId(report.getId());
        resp.setBinId(report.getBin().getId());
        resp.setUserId(report.getUserId());
        resp.setReportType(report.getReportType());
        resp.setPhotoUrl(report.getPhotoUrl());
        resp.setDescription(report.getDescription());
        resp.setCreatedAt(report.getCreatedAt());
        return resp;
    }
} 