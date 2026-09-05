package com.mobility.sla.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDate;

@Entity
public class SimulationState {
    @Id
    private Long id = 1L;
    private LocalDate operationalDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getOperationalDate() { return operationalDate; }
    public void setOperationalDate(LocalDate operationalDate) { this.operationalDate = operationalDate; }
}