package com.mobility.sla.service;

import com.mobility.sla.entity.CaseRecord;
import com.mobility.sla.entity.CaseTimelineEvent;
import com.mobility.sla.repository.CaseRecordRepository;
import com.mobility.sla.repository.CaseTimelineEventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CaseService {
    private final CaseRecordRepository caseRecordRepository;
    private final CaseTimelineEventRepository caseTimelineEventRepository;

    public CaseService(CaseRecordRepository caseRecordRepository, CaseTimelineEventRepository caseTimelineEventRepository) {
        this.caseRecordRepository = caseRecordRepository;
        this.caseTimelineEventRepository = caseTimelineEventRepository;
    }

    public boolean existsOpenCase(String metricKey, String vendor) {
        return caseRecordRepository.findByMetricKeyAndVendorAndStatusNot(metricKey, vendor, "RESOLVED").isPresent();
    }

    public CaseRecord createCase(String metricKey, String title, String businessUnit, String vendor, String route, String office, String shift,
                                double breachValue, double slaValue, String severity, LocalDate detectedDate, LocalDate deadline,
                                int escalationLevel, String rootCause, int affectedTrips, int affectedEmployees, double estimatedCostImpact,
                                String investigationReport, String accountabilityNotice, String escalationReport) {
        CaseRecord caseRecord = new CaseRecord();
        caseRecord.setCaseNumber("CASE-" + (System.currentTimeMillis() % 100000));
        caseRecord.setMetricKey(metricKey);
        caseRecord.setTitle(title);
        caseRecord.setBusinessUnit(businessUnit);
        caseRecord.setVendor(vendor);
        caseRecord.setRoute(route);
        caseRecord.setOffice(office);
        caseRecord.setShift(shift);
        caseRecord.setBreachValue(breachValue);
        caseRecord.setSlaValue(slaValue);
        caseRecord.setSeverity(severity);
        caseRecord.setStatus("OPEN");
        caseRecord.setDetectedDate(detectedDate);
        caseRecord.setDeadline(deadline);
        caseRecord.setEscalationLevel(escalationLevel);
        caseRecord.setRootCause(rootCause);
        caseRecord.setAffectedTrips(affectedTrips);
        caseRecord.setAffectedEmployees(affectedEmployees);
        caseRecord.setEstimatedCostImpact(estimatedCostImpact);
        caseRecord.setInvestigationReport(investigationReport);
        caseRecord.setAccountabilityNotice(accountabilityNotice);
        caseRecord.setEscalationReport(escalationReport);
        LocalDateTime now = LocalDateTime.now();
        caseRecord.setCreatedAt(now);
        caseRecord.setUpdatedAt(now);
        return caseRecordRepository.save(caseRecord);
    }

    public List<CaseRecord> listAll() {
        return caseRecordRepository.findAll();
    }

    public List<CaseRecord> listOpenCases() {
        return caseRecordRepository.findByStatusNot("CLOSED");
    }

    public Optional<CaseRecord> getById(Long id) { return caseRecordRepository.findById(id); }

    public void updateStatus(Long id, String status) {
        caseRecordRepository.findById(id).ifPresent(c -> {
            c.setStatus(status);
            c.setUpdatedAt(LocalDateTime.now());
            caseRecordRepository.save(c);
        });
    }

    public void addTimeline(Long caseId, String eventType, String description, String actor, String evidence, int escalationLevel) {
        CaseTimelineEvent event = new CaseTimelineEvent();
        event.setCaseId(caseId);
        event.setEventType(eventType);
        event.setDescription(description);
        event.setActor(actor);
        event.setEvidence(evidence);
        event.setEscalationLevel(escalationLevel);
        event.setTimestamp(LocalDateTime.now());
        caseTimelineEventRepository.save(event);
    }

    public List<CaseTimelineEvent> timeline(Long caseId) {
        return caseTimelineEventRepository.findByCaseIdOrderByTimestampAsc(caseId);
    }
}
