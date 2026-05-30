package com.gestionmediterraneo.hotel.services;

/**
 * Represents a client's loyalty tier based on booking history.
 *
 * <p>The tier includes the rank name, discount percentage,
 * recent booking count, and total booking count.</p>
 *
 * @author Gestión Mediterráneo
 * @see LoyaltyService
 */
public class LoyaltyTier {

    private final String rank;
    private final double discountPercentage;
    private final long recentBookings;
    private final long totalBookings;

    public LoyaltyTier(String rank, double discountPercentage, long recentBookings, long totalBookings) {
        this.rank = rank;
        this.discountPercentage = discountPercentage;
        this.recentBookings = recentBookings;
        this.totalBookings = totalBookings;
    }

    public String getRank() {
        return rank;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public long getRecentBookings() {
        return recentBookings;
    }

    public long getTotalBookings() {
        return totalBookings;
    }
}
