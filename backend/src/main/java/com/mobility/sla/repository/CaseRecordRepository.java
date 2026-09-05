package com.mobility.sla.repository;

import com.mobility.sla.entity.CaseRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CaseRecordRepository extends JpaRepository<CaseRecord, Long> {
    List<CaseRecord> findByStatusNot(String status);
    Optional<CaseRecord> findByMetricKeyAndVendorAndStatusNot(String metricKey, String vendor, String status);
}