package com.gestionmediterraneo.hotel.services;

import java.math.BigDecimal;

public class RevenueReportDTO {

    private String category;
    private BigDecimal totalAmount;
    private BigDecimal totalGross;
    private BigDecimal totalNet;
    private BigDecimal totalTax;

    public RevenueReportDTO() {
    }

    public RevenueReportDTO(String category, BigDecimal totalAmount, BigDecimal totalGross, BigDecimal totalNet, BigDecimal totalTax) {
        this.category = category;
        this.totalAmount = totalAmount;
        this.totalGross = totalGross;
        this.totalNet = totalNet;
        this.totalTax = totalTax;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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
