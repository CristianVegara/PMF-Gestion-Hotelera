package com.gestionmediterraneo.hotel.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import com.gestionmediterraneo.hotel.daos.IBookingDAO;
import com.gestionmediterraneo.hotel.entities.Client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoyaltyServiceTest {

    @Mock
    private IBookingDAO bookingDao;

    @InjectMocks
    private LoyaltyService loyaltyService;

    @Test
    void shouldReturnNoTierWhenClientHasNoBookings() {
        Client client = clientWithId(1L);

        LoyaltyTier tier = loyaltyService.calculateTier(client);

        assertEquals("Sin rango", tier.getRank());
        assertEquals(0, tier.getDiscountPercentage());
        verify(bookingDao).countByCliente_Id(1L);
    }

    @Test
    void shouldReturnBronzeForThreeBookingsInLastThreeMonths() {
        Client client = clientWithId(1L);
        when(bookingDao.countByCliente_IdAndFechaEntradaBetween(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(3L, 3L, 3L);
        when(bookingDao.countByCliente_Id(1L)).thenReturn(3L);

        LoyaltyTier tier = loyaltyService.calculateTier(client);

        assertEquals("Bronze", tier.getRank());
        assertEquals(5, tier.getDiscountPercentage());
        assertEquals(3, tier.getRecentBookings());
        assertEquals(3, tier.getTotalBookings());
    }

    @Test
    void shouldReturnSilverForFiveBookingsInLastSixMonths() {
        Client client = clientWithId(2L);
        when(bookingDao.countByCliente_IdAndFechaEntradaBetween(eq(2L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(2L, 5L, 5L);
        when(bookingDao.countByCliente_Id(2L)).thenReturn(5L);

        LoyaltyTier tier = loyaltyService.calculateTier(client);

        assertEquals("Silver", tier.getRank());
        assertEquals(10, tier.getDiscountPercentage());
        assertEquals(5, tier.getRecentBookings());
    }

    @Test
    void shouldReturnGoldForEightBookingsInLastSixMonths() {
        Client client = clientWithId(3L);
        when(bookingDao.countByCliente_IdAndFechaEntradaBetween(eq(3L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(3L, 8L, 8L);
        when(bookingDao.countByCliente_Id(3L)).thenReturn(8L);

        LoyaltyTier tier = loyaltyService.calculateTier(client);

        assertEquals("Gold", tier.getRank());
        assertEquals(15, tier.getDiscountPercentage());
        assertEquals(8, tier.getRecentBookings());
    }

    @Test
    void shouldReturnDiamondForTwelveBookingsInLastYear() {
        Client client = clientWithId(4L);
        when(bookingDao.countByCliente_IdAndFechaEntradaBetween(eq(4L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(3L, 8L, 12L);
        when(bookingDao.countByCliente_Id(4L)).thenReturn(12L);

        LoyaltyTier tier = loyaltyService.calculateTier(client);

        assertEquals("Diamante", tier.getRank());
        assertEquals(20, tier.getDiscountPercentage());
        assertEquals(12, tier.getRecentBookings());
    }

    @Test
    void shouldReturnNoTierWhenClientIsNull() {
        LoyaltyTier tier = loyaltyService.calculateTier(null);

        assertEquals("Sin rango", tier.getRank());
        assertEquals(0, tier.getDiscountPercentage());
        assertEquals(0, tier.getRecentBookings());
        assertEquals(0, tier.getTotalBookings());
    }

    private Client clientWithId(Long id) {
        Client client = new Client();
        client.setId(id);
        return client;
    }
}
