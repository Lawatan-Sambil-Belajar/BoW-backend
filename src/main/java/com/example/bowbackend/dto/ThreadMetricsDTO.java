package com.example.bowbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ThreadMetricsDTO {

    private String name;
    private long executionTimeInMs;
}
