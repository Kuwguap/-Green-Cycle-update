package com.greencycle.Backend.dump;

import com.greencycle.Backend.bin.IpfsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/dumps")
public class DumpReportController {
    private final DumpReportService dumpReportService;
    private final IpfsService ipfsService;

    @Autowired
    public DumpReportController(DumpReportService dumpReportService, IpfsService ipfsService) {
        this.dumpReportService = dumpReportService;
        this.ipfsService = ipfsService;
    }

    @PostMapping("/report/image")
    public ResponseEntity<DumpReportResponse> submitDumpReportWithImage(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "reportType", required = false) String reportType,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestParam("image") MultipartFile image
    ) throws IOException {
        String photoUrl = ipfsService.uploadImage(image);
        DumpReportRequest request = new DumpReportRequest();
        request.setUserId(userId);
        request.setReportType(reportType);
        request.setDescription(description);
        request.setLocation(location);
        request.setLatitude(latitude);
        request.setLongitude(longitude);
        DumpReportResponse response = dumpReportService.submitReport(request, photoUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reports")
    public ResponseEntity<List<DumpReportResponse>> getAllDumpReports() {
        List<DumpReportResponse> reports = dumpReportService.getAllReports();
        return ResponseEntity.ok(reports);
    }
} 