package com.tharun.vessel_risk.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShipmentPageResponse {

    private List<ShipmentResponse> shipments;

    private int currentPage;

    private int totalPages;

    private long totalElements;

    private boolean last;
}