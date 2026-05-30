package com.gestionmediterraneo.hotel.services;

import java.math.BigDecimal;

/**
 * DTO for generating an invoice from a booking.
 *
 * @author Gestión Mediterráneo
 */
public class InvoiceGenerationRequest {

    private Long bookingId;
    private BigDecimal taxPercentage;
    private BigDecimal discountPercentage;
    private String memo;

    public InvoiceGenerationRequest() {
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public BigDecimal getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(BigDecimal taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
