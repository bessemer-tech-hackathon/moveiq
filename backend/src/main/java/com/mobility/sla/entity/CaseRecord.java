package com.mobility.sla.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class CaseRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String caseNumber;

    private String metricKey;
    private String title;
    private String businessUnit;
    private String vendor;
    private String route;
    private String office;
    private String shift;
    private double breachValue;
    private double slaValue;
    private String severity;
    private String status;
    private LocalDate detectedDate;
    private LocalDate deadline;
    private int escalationLevel;

    @Column(length = 4000)
    private String rootCause;

    private int affectedTrips;
    private int affectedEmployees;
    private double estimatedCostImpact;

    @Column(length = 12000)
    private String investigationReport;

    @Column(length = 8000)
    private String accountabilityNotice;

    @Column(length = 8000)
    private String escalationReport;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCaseNumber() { return caseNumber; }
    public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }

    public String getMetricKey() { return metricKey; }
    public void setMetricKey(String metricKey) { this.metricKey = metricKey; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBusinessUnit() { return businessUnit; }
    public void setBusinessUnit(String businessUnit) { this.businessUnit = businessUnit; }

    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }

    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }

    public String getOffice() { return office; }
    public void setOffice(String office) { this.office = office; }

    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }

    public double getBreachValue() { return breachValue; }
    public void setBreachValue(double breachValue) { this.breachValue = breachValue; }

    public double getSlaValue() { return slaValue; }
    public void setSlaValue(double slaValue) { this.slaValue = slaValue; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getDetectedDate() { return detectedDate; }
    public void setDetectedDate(LocalDate detectedDate) { this.detectedDate = detectedDate; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public int getEscalationLevel() { return escalationLevel; }
    public void setEscalationLevel(int escalationLevel) { this.escalationLevel = escalationLevel; }

    public String getRootCause() { return rootCause; }
    public void setRootCause(String rootCause) { this.rootCause = rootCause; }

    public int getAffectedTrips() { return affectedTrips; }
    public void setAffectedTrips(int affectedTrips) { this.affectedTrips = affectedTrips; }

    public int getAffectedEmployees() { return affectedEmployees; }
    public void setAffectedEmployees(int affectedEmployees) { this.affectedEmployees = affectedEmployees; }

    public double getEstimatedCostImpact() { return estimatedCostImpact; }
    public void setEstimatedCostImpact(double estimatedCostImpact) { this.estimatedCostImpact = estimatedCostImpact; }

    public String getInvestigationReport() { return investigationReport; }
    public void setInvestigationReport(String investigationReport) { this.investigationReport = investigationReport; }

    public String getAccountabilityNotice() { return accountabilityNotice; }
    public void setAccountabilityNotice(String accountabilityNotice) { this.accountabilityNotice = accountabilityNotice; }

    public String getEscalationReport() { return escalationReport; }
    public void setEscalationReport(String escalationReport) { this.escalationReport = escalationReport; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
}