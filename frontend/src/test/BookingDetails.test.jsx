import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, test, expect, vi, beforeEach } from 'vitest';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import BookingDetails from '../pages/BookingDetails.jsx';

const mockBookingPendiente = {
  id: 45,
  roomType: 'SUITE',
  estado: 'CONFIRMADA',
  checkInStatus: 'PENDIENTE',
  fechaEntrada: '2026-05-24',
  fechaSalida: '2026-05-30',
  cliente: { id: 10, nombre: 'Lucía Villalón', dni: '44556677D' },
  habitacion: null
};

const mockRoomLibre = {
  id: 202,
  number: '305',
  type: 'SUITE',
  price: 150,
  status: 'LIBRE'
};

const mockBookingDentro = {
  ...mockBookingPendiente,
  checkInStatus: 'DENTRO',
  fechaSalida: new Date().toISOString().split('T')[0],
  habitacion: {
    id: 202,
    number: '305',
    type: 'SUITE',
    price: 150,
    status: 'OCUPADA'
  }
};

const renderComponent = (id) => {
  return render(
    <MemoryRouter initialEntries={[`/bookings/${id}`]}>
      <Routes>
        <Route path="/bookings/:id" element={<BookingDetails />} />
      </Routes>
    </MemoryRouter>
  );
};

describe('Vista de detalles de reserva', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    localStorage.clear();
    localStorage.setItem('user_token', 'token_pruebas_detalles');
  });

  test('Muestra la pantalla de carga y pinta los datos del cliente y la estancia', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(mockBookingPendiente)
    }));

    renderComponent(45);

    expect(screen.getByText(/Cargando reserva.../i)).toBeInTheDocument();

    const nombreCliente = await screen.findByText('Lucía Villalón');
    expect(nombreCliente).toBeInTheDocument();
    expect(screen.getByText('DNI: 44556677D')).toBeInTheDocument();
    expect(screen.getByText('SUITE')).toBeInTheDocument();
    expect(screen.getByText('Habitación Nº --- (No asignada)')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Dar Entrada/i })).toBeInTheDocument();
  });

  test('Asigna una habitación vacía y actualiza el estado al hacer Check-In', async () => {
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    vi.spyOn(window, 'alert').mockImplementation(() => {});

    const mockFetch = vi.fn().mockImplementation((url, config) => {
      if (url.includes('/api/bookings/45') && config?.method === 'PUT') {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve({
            ...mockBookingPendiente,
            checkInStatus: 'DENTRO',
            habitacion: { ...mockRoomLibre, status: 'OCUPADA' }
          })
        });
      }
      if (url.includes('/api/bookings/45')) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve(mockBookingPendiente)
        });
      }
      if (url.includes('/api/rooms/free/SUITE')) {
        return Promise.resolve({
          ok: true,
          status: 200,
          json: () => Promise.resolve(mockRoomLibre)
        });
      }
      if (url.includes('/api/rooms/202')) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve({ ...mockRoomLibre, status: 'OCUPADA' })
        });
      }
      return Promise.resolve({
        ok: true,
        json: () => Promise.resolve({})
      });
    });
    vi.stubGlobal('fetch', mockFetch);

    renderComponent(45);

    const botonCheckIn = await screen.findByRole('button', { name: /Dar Entrada/i });
    fireEvent.click(botonCheckIn);

    await waitFor(() => {
      expect(window.confirm).toHaveBeenCalled();
      expect(window.alert).toHaveBeenCalledWith('Check-In exitoso. Se asignó la habitación 305.');
    });
  });

  test('Cancela el servicio de lavandería o habitación y marca salida al hacer Check-Out', async () => {
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    vi.spyOn(window, 'alert').mockImplementation(() => {});

    const mockFetch = vi.fn().mockImplementation((url) => {
      if (url.includes('/api/bookings/45')) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve(mockBookingDentro)
        });
      }
      return Promise.resolve({
        ok: true,
        json: () => Promise.resolve({ ...mockBookingDentro, checkInStatus: 'FUERA' })
      });
    });
    vi.stubGlobal('fetch', mockFetch);

    renderComponent(45);

    const botonCheckOut = await screen.findByRole('button', { name: /Dar Salida/i });
    fireEvent.click(botonCheckOut);

    await waitFor(() => {
      expect(window.confirm).toHaveBeenCalled();
      expect(window.alert).toHaveBeenCalledWith('Check-Out exitoso. La reserva ha finalizado.');
    });
  });
});