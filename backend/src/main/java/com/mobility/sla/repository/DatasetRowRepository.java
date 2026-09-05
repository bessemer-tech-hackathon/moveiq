package com.mobility.sla.repository;

import com.mobility.sla.entity.DatasetRow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatasetRowRepository extends JpaRepository<DatasetRow, Long> {
    void deleteByDatasetName(String datasetName);
}