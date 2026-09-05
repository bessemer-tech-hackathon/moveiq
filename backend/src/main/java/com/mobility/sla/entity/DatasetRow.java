package com.mobility.sla.entity;

import jakarta.persistence.*;

@Entity
@Table(indexes = @Index(name = "idx_dataset_name", columnList = "datasetName"))
public class DatasetRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String datasetName;
    private long rowNumber;
    @Lob
    @Column(nullable = false, length = 20000)
    private String valuesJson;
    public Long getId() { return id; }
    public String getDatasetName() { return datasetName; }
    public void setDatasetName(String datasetName) { this.datasetName = datasetName; }
    public long getRowNumber() { return rowNumber; }
    public void setRowNumber(long rowNumber) { this.rowNumber = rowNumber; }
    public String getValuesJson() { return valuesJson; }
    public void setValuesJson(String valuesJson) { this.valuesJson = valuesJson; }
}