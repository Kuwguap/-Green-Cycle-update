package com.greencycle.Backend.bin;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BinRepository extends JpaRepository<Bin, Long> {
    Optional<Bin> findByQrCodeId(String qrCodeId);
} 