import { render, screen, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { describe, test, expect, vi, beforeEach } from 'vitest';
import DeparturesToday from '../pages/DeparturesToday';

describe('Pruebas en el componente DeparturesToday', () => {
  const mockDate = '2026-05-24';

  const mockBookingsData = [
    {
      id: 101,
      fechaSalida: '2026-05-24',
      checkInStatus: 'DENTRO',
      habitacion: { number: '105' },
      cliente: { nombre: 'María Lopez' }
    },
    {
      id: 102,
      fechaSalida: '2026-05-24',
      checkInStatus: 'DENTRO',
      habitacion: { number: '204' },
      cliente: { nombre: 'Carlos Mendoza' }
    }
  ];

  beforeEach(() => {
    vi.restoreAllMocks();
    localStorage.clear();
  });

  test('Mostrar el estado de carga al renderizar', () => {
    const mockFetch = vi.fn().mockImplementation(() => new Promise(() => {}));
    vi.stubGlobal('fetch', mockFetch);

    render(
      <BrowserRouter>
        <DeparturesToday date={mockDate} />
      </BrowserRouter>
    );
    expect(screen.getByText('Cargando...')).toBeInTheDocument();
  });

  test('Renderizar la lista de salidas filtradas correctamente tras la carga', async () => {
    const mockFetch = vi.fn().mockResolvedValueOnce({
      json: () => Promise.resolve(mockBookingsData),
    });
    vi.stubGlobal('fetch', mockFetch);

    render(
      <BrowserRouter>
        <DeparturesToday date={mockDate} />
      </BrowserRouter>
    );

    expect(await screen.findByText('María Lopez')).toBeInTheDocument();
    expect(screen.getByText('Carlos Mendoza')).toBeInTheDocument();

    expect(screen.getByText('#105')).toBeInTheDocument();
    expect(screen.getByText('#204')).toBeInTheDocument();
  });

  test('Renderizar una lista vacía si el backend responde con un error o no es un Array', async () => {
    const mockFetch = vi.fn().mockRejectedValueOnce(new Error('Network error'));
    vi.stubGlobal('fetch', mockFetch);

    render(
      <BrowserRouter>
        <DeparturesToday date={mockDate} />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.queryByText('Cargando...')).not.toBeInTheDocument();
    });
    
    expect(screen.queryByText('María Lopez')).not.toBeInTheDocument();
  });

  test('Enlaces de cada reserva deben apuntar a la URL correcta', async () => {
    const mockFetch = vi.fn().mockResolvedValueOnce({
      json: () => Promise.resolve(mockBookingsData),
    });
    vi.stubGlobal('fetch', mockFetch);

    render(
      <BrowserRouter>
        <DeparturesToday date={mockDate} />
      </BrowserRouter>
    );

    const links = await screen.findAllByRole('link', { name: 'Booking' });
   
    expect(links[0]).toHaveAttribute('href', '/bookings/101');
    expect(links[1]).toHaveAttribute('href', '/bookings/102');
  });
});