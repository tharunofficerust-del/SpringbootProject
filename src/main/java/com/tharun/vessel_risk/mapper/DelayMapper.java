package com.tharun.vessel_risk.mapper;

import org.springframework.stereotype.Component;

import com.tharun.vessel_risk.dto.DelayResponse;
import com.tharun.vessel_risk.entity.DelayReport;

@Component
public class DelayMapper {

    public DelayResponse toResponse(
            DelayReport delayReport) {

        return DelayResponse.builder()
                .id(delayReport.getId())
                .voyageNumber(
                        delayReport.getVesselSchedule()
                                .getVoyageNumber())
                .delayReason(delayReport.getDelayReason())
                .delayHours(delayReport.getDelayHours())
                .reportedPort(delayReport.getReportedPort())
                .remarks(delayReport.getRemarks())
                .reportedDate(delayReport.getReportedDate())
                .build();
    }
}