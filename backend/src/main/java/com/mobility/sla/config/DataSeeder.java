package com.mobility.sla.config;

import com.mobility.sla.entity.SlaConfiguration;
import com.mobility.sla.repository.SlaConfigurationRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class DataSeeder implements ApplicationRunner {
    private final SlaConfigurationRepository slaConfigurationRepository;

    public DataSeeder(SlaConfigurationRepository slaConfigurationRepository) {
        this.slaConfigurationRepository = slaConfigurationRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (slaConfigurationRepository.count() > 0) {
            return;
        }

        SlaConfiguration ota = new SlaConfiguration();
        ota.setMetricKey("OTA_RATE");
        ota.setThreshold(90.0);
        ota.setWarningThreshold(85.0);
        ota.setComparisonOperator("LESS_THAN");
        ota.setSeverity("CRITICAL");
        ota.setScope("overall");
        ota.setActive(true);
        ota.setEffectiveDate(LocalDate.of(2026, 5, 1));

        SlaConfiguration breach = new SlaConfiguration();
        breach.setMetricKey("SLA_BREACH_RATE");
        breach.setThreshold(10.0);
        breach.setWarningThreshold(8.0);
        breach.setComparisonOperator("GREATER_THAN");
        breach.setSeverity("WARNING");
        breach.setScope("overall");
        breach.setActive(true);
        breach.setEffectiveDate(LocalDate.of(2026, 5, 1));

        SlaConfiguration delay = new SlaConfiguration();
        delay.setMetricKey("AVERAGE_DELAY");
        delay.setThreshold(10.0);
        delay.setWarningThreshold(8.0);
        delay.setComparisonOperator("GREATER_THAN");
        delay.setSeverity("WARNING");
        delay.setScope("overall");
        delay.setActive(true);
        delay.setEffectiveDate(LocalDate.of(2026, 5, 1));

        slaConfigurationRepository.save(ota);
        slaConfigurationRepository.save(breach);
        slaConfigurationRepository.save(delay);
    }
}
