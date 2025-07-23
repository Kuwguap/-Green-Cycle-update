package com.greencycle.Backend.dump;

import com.greencycle.Backend.bin.IpfsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DumpReportService {
    private final DumpReportRepository dumpReportRepository;
    private final IpfsService ipfsService;

    @Autowired
    public DumpReportService(DumpReportRepository dumpReportRepository, IpfsService ipfsService) {
        this.dumpReportRepository = dumpReportRepository;
        this.ipfsService = ipfsService;
    }

    public DumpReportResponse submitReport(DumpReportRequest request, String photoUrl) {
        DumpReport report = new DumpReport();
        report.setUserId(request.getUserId());
        report.setPhotoUrl(photoUrl);
        report.setReportType(request.getReportType());
        report.setDescription(request.getDescription());
        report.setLocation(request.getLocation());
        report.setLatitude(request.getLatitude());
        report.setLongitude(request.getLongitude());
        DumpReport saved = dumpReportRepository.save(report);
        return toResponse(saved);
    }

    public List<DumpReportResponse> getAllReports() {
        return dumpReportRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    private DumpReportResponse toResponse(DumpReport report) {
        DumpReportResponse resp = new DumpReportResponse();
        resp.setId(report.getId());
        resp.setUserId(report.getUserId());
        resp.setPhotoUrl(report.getPhotoUrl());
        resp.setReportType(report.getReportType());
        resp.setDescription(report.getDescription());
        resp.setLocation(report.getLocation());
        resp.setLatitude(report.getLatitude());
        resp.setLongitude(report.getLongitude());
        resp.setCreatedAt(report.getCreatedAt());
        return resp;
    }
} 