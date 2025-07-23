package com.greencycle.Backend.bin;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BinReportRepository extends JpaRepository<BinReport, Long> {
    List<BinReport> findAllByBin_Id(Long binId);
} 