package com.gestionmediterraneo.hotel.services;

import java.math.BigDecimal;
import java.util.List;

public class RevenueReportResponse {

    private List<RevenueReportDTO> breakdown;
    private BigDecimal totalGross;
    private BigDecimal totalNet;
    private BigDecimal totalTax;

    public RevenueReportResponse() {
    }

    public RevenueReportResponse(List<RevenueReportDTO> breakdown, BigDecimal totalGross, BigDecimal totalNet, BigDecimal totalTax) {
        this.breakdown = breakdown;
        this.totalGross = totalGross;
        this.totalNet = totalNet;
        this.totalTax = totalTax;
    }

    public List<RevenueReportDTO> getBreakdown() {
        return breakdown;
    }

    public void setBreakdown(List<RevenueReportDTO> breakdown) {
        this.breakdown = breakdown;
    }

    public BigDecimal getTotalGross() {
        return totalGross;
    }

    public void setTotalGross(BigDecimal totalGross) {
        this.totalGross = totalGross;
    }

    public BigDecimal getTotalNet() {
        return totalNet;
    }

    public void setTotalNet(BigDecimal totalNet) {
        this.totalNet = totalNet;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }
}
