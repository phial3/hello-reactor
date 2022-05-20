package com.mine.study.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SteelyardResult {
    private Long id;
    private Double weight;
    private Long reportTime;
    private Long startTime;
    private Long endTime;
}
