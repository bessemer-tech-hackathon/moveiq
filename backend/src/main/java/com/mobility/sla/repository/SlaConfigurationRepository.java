package com.mobility.sla.repository;

import com.mobility.sla.entity.SlaConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SlaConfigurationRepository extends JpaRepository<SlaConfiguration, Long> {
    Optional<SlaConfiguration> findByMetricKey(String metricKey);
}