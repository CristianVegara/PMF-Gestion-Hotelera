package com.gestionmediterraneo.hotel.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.gestionmediterraneo.hotel.daos.IClientDAO;
import com.gestionmediterraneo.hotel.entities.Client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private IClientDAO clientDao;

    @InjectMocks
    private ClientServiceImp clientService;

    @Test
    void shouldReturnAllClients() {
        List<Client> clients = Arrays.asList(new Client(), new Client());

        when(clientDao.findAll()).thenReturn(clients);

        List<Client> result = clientService.findAll();

        assertEquals(2, result.size());
        verify(clientDao, times(1)).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoClientsExist() {
        when(clientDao.findAll()).thenReturn(List.of());

        List<Client> result = clientService.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldSaveClient() {
        Client client = new Client();

        when(clientDao.save(client)).thenReturn(client);

        Client result = clientService.save(client);

        assertNotNull(result);
        verify(clientDao, times(1)).save(client);
    }

    @Test
    void shouldThrowExceptionWhenSaveFails() {
        Client client = new Client();

        when(clientDao.save(client)).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> {
            clientService.save(client);
        });
    }

    @Test
    void shouldReturnClientWhenIdExists() {
        Client client = new Client();
        client.setId(1L);

        when(clientDao.findById(1L)).thenReturn(Optional.of(client));

        Client result = clientService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(clientDao).findById(1L);
    }

    @Test
    void shouldReturnNullWhenClientDoesNotExist() {
        when(clientDao.findById(1L)).thenReturn(Optional.empty());

        Client result = clientService.findById(1L);

        assertNull(result);
        verify(clientDao).findById(1L);
    }

    @Test
    void shouldReturnClientWhenDniExists() {
        Client client = new Client();
        client.setDni("12345678A");

        when(clientDao.findByDni("12345678A")).thenReturn(client);

        Client result = clientService.findByDni("12345678A");

        assertNotNull(result);
        assertEquals("12345678A", result.getDni());
    }

    @Test
    void shouldReturnNullWhenDniDoesNotExist() {
        when(clientDao.findByDni("12345678A")).thenReturn(null);

        Client result = clientService.findByDni("12345678A");

        assertNull(result);
    }

    @Test
    void shouldDeleteClient() {
        Client client = new Client();

        Client result = clientService.delete(client);

        verify(clientDao, times(1)).delete(client);
        assertEquals(client, result);
    }

    @Test
    void shouldThrowExceptionWhenDeleteFails() {
        Client client = new Client();

        doThrow(new RuntimeException("DB error")).when(clientDao).delete(client);

        assertThrows(RuntimeException.class, () -> {
            clientService.delete(client);
        });
    }

    @Test
    void shouldReturnSortedClientsAsc() {
        List<Client> clients = Arrays.asList(new Client());

        when(clientDao.findAll(any(Sort.class))).thenReturn(clients);

        List<Client> result = clientService.findAllSorted("id", "asc");

        assertEquals(1, result.size());
        verify(clientDao).findAll(any(Sort.class));
    }

    @Test
    void shouldReturnSortedClientsDesc() {
        List<Client> clients = Arrays.asList(new Client());

        when(clientDao.findAll(any(Sort.class))).thenReturn(clients);

        List<Client> result = clientService.findAllSorted("id", "desc");

        assertEquals(1, result.size());
        verify(clientDao).findAll(any(Sort.class));
    }

    @Test
    void shouldCallFindAllWithSort() {
        when(clientDao.findAll(any(Sort.class))).thenReturn(List.of());

        clientService.findAllSorted("nombre", "desc");

        verify(clientDao).findAll(any(Sort.class));
    }
}