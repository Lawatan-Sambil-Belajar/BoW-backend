package com.example.bowbackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@SuperBuilder
@JsonInclude(NON_NULL)
public class BowResultDTO {

    private String strategyType;
    private long executionTimeInMs;
    private Map<String, Integer> bagOfWords;
    private List<ThreadMetricsDTO> threadMetricsDTOList;
}
