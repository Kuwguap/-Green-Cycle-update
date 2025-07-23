package com.greencycle.Backend.bin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/bins")
public class BinController {
    private final BinService binService;
    private final IpfsService ipfsService;

    @Autowired
    public BinController(BinService binService, IpfsService ipfsService) {
        this.binService = binService;
        this.ipfsService = ipfsService;
    }

    @PostMapping
    public ResponseEntity<Bin> registerBin(@RequestBody Bin bin) {
        Bin saved = binService.registerBin(bin);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/report")
    public ResponseEntity<BinReportResponse> submitReport(@RequestBody BinReportRequest request) {
        BinReportResponse response = binService.submitReport(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/report/image")
    public ResponseEntity<BinReportResponse> submitReportWithImage(
            @RequestParam("qrCodeId") String qrCodeId,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "reportType", required = false) String reportType,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("image") MultipartFile image
    ) throws IOException {
        String photoUrl = ipfsService.uploadImage(image);
        BinReportRequest request = new BinReportRequest();
        request.setQrCodeId(qrCodeId);
        request.setUserId(userId);
        request.setReportType(reportType);
        request.setPhotoUrl(photoUrl);
        request.setDescription(description);
        BinReportResponse response = binService.submitReport(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{binId}/reports")
    public ResponseEntity<List<BinReportResponse>> getReportsForBin(@PathVariable Long binId) {
        List<BinReportResponse> reports = binService.getReportsForBin(binId);
        return ResponseEntity.ok(reports);
    }
} 