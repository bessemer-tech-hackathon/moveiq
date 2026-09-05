package com.mobility.sla.repository;

import com.mobility.sla.entity.CaseTimelineEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CaseTimelineEventRepository extends JpaRepository<CaseTimelineEvent, Long> {
    List<CaseTimelineEvent> findByCaseIdOrderByTimestampAsc(Long caseId);
}