package com.gestionmediterraneo.hotel.services;

import com.gestionmediterraneo.hotel.daos.IBookingDAO;
import com.gestionmediterraneo.hotel.entities.Client;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class LoyaltyService {

    private final IBookingDAO bookingDao;

    public LoyaltyService(IBookingDAO bookingDao) {
        this.bookingDao = bookingDao;
    }

    public LoyaltyTier calculateTier(Client client) {
        if (client == null || client.getId() == null) {
            return new LoyaltyTier("Sin rango", 0, 0, 0);
        }

        LocalDate today = LocalDate.now();
        long lastThreeMonths = bookingDao.countByCliente_IdAndFechaEntradaBetween(
                client.getId(),
                today.minusMonths(3),
                today
        );
        long lastSixMonths = bookingDao.countByCliente_IdAndFechaEntradaBetween(
                client.getId(),
                today.minusMonths(6),
                today
        );
        long lastYear = bookingDao.countByCliente_IdAndFechaEntradaBetween(
                client.getId(),
                today.minusMonths(12),
                today
        );
        long totalBookings = bookingDao.countByCliente_Id(client.getId());

        if (lastYear >= 12 || totalBookings >= 18) {
            return new LoyaltyTier("Diamante", 20, lastYear, totalBookings);
        }

        if (lastSixMonths >= 8 || totalBookings >= 10) {
            return new LoyaltyTier("Gold", 15, lastSixMonths, totalBookings);
        }

        if (lastSixMonths >= 5 || totalBookings >= 6) {
            return new LoyaltyTier("Silver", 10, lastSixMonths, totalBookings);
        }

        if (lastThreeMonths >= 3) {
            return new LoyaltyTier("Bronze", 5, lastThreeMonths, totalBookings);
        }

        return new LoyaltyTier("Sin rango", 0, lastThreeMonths, totalBookings);
    }
}
