import { render, screen, fireEvent } from '@testing-library/react';
import { BrowserRouter, useParams, useLocation } from 'react-router-dom';
import { describe, test, expect, vi, beforeEach } from 'vitest';
import ClientDetails from '../pages/ClientDetails';

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
    useParams: vi.fn(),
    useLocation: vi.fn(),
  };
});

const mockClientWithData = {
  id: 1,
  nombre: 'Alejandro Sanz',
  dni: '12345678A',
  telefono: '600123456',
  correo: 'alejandro@email.com',
  bookings: [
    {
      id: 10,
      fechaEntrada: '2026-06-01T14:00:00',
      fechaSalida: '2026-06-10T10:00:00',
      estado: 'CONFIRMADA',
      habitacion: { number: '101' }
    },
    {
      id: 11,
      fechaEntrada: '2026-05-01T14:00:00',
      fechaSalida: '2026-05-05T10:00:00',
      estado: 'TERMINADA',
      habitacion: { number: '202' }
    }
  ],
  activities: [
    {
      id: 50,
      descripcion: 'Masaje Spa',
      fechaComienzo: '2026-06-03T17:00:00',
      fechaFin: '2026-06-03T18:00:00',
      precio: 45.0
    }
  ]
};

describe('Pruebas en el componente ClientDetails', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    localStorage.clear();
    vi.mocked(useParams).mockReturnValue({ id: '1' });
    vi.mocked(useLocation).mockReturnValue({ search: '' });
  });

  test('Mostrar el estado de carga al principio', () => {
    const mockFetch = vi.fn().mockImplementation(() => new Promise(() => {}));
    vi.stubGlobal('fetch', mockFetch);

    render(
      <BrowserRouter>
        <ClientDetails />
      </BrowserRouter>
    );

    expect(screen.getByText('Cargando...')).toBeInTheDocument();
  });

  test('Mostrar un mensaje de error si el cliente no se encuentra', async () => {
    const mockFetch = vi.fn().mockResolvedValueOnce({
      json: () => Promise.resolve(null),
    });
    vi.stubGlobal('fetch', mockFetch);

    render(
      <BrowserRouter>
        <ClientDetails />
      </BrowserRouter>
    );

    expect(await screen.findByText('Cliente no encontrado.')).toBeInTheDocument();
  });

  test('Renderizar la información del perfil, contadores e historiales', async () => {
      const mockFetch = vi.fn().mockResolvedValueOnce({
        json: () => Promise.resolve(mockClientWithData),
      });
      vi.stubGlobal('fetch', mockFetch);

      render(
        <BrowserRouter>
          <ClientDetails />
        </BrowserRouter>
      );

      expect(await screen.findByRole('heading', { name: 'Alejandro Sanz' })).toBeInTheDocument();
      expect(screen.getByText('12345678A')).toBeInTheDocument();
      expect(screen.getByText('alejandro@email.com')).toBeInTheDocument();

      expect(screen.getByText('Reservas')).toBeInTheDocument();
      expect(screen.getByText('Activas')).toBeInTheDocument();
      expect(screen.getByText('Terminadas')).toBeInTheDocument();

      expect(screen.getByText('Masaje Spa')).toBeInTheDocument();
      
      expect(screen.getByText((content) => content.includes('45') && content.includes('€'))).toBeInTheDocument();
    });

  test('Alternar la clase row-selected-persist al hacer clic en una reserva', async () => {
    const mockFetch = vi.fn().mockResolvedValueOnce({
      json: () => Promise.resolve(mockClientWithData),
    });
    vi.stubGlobal('fetch', mockFetch);

    render(
      <BrowserRouter>
        <ClientDetails />
      </BrowserRouter>
    );

    const filaReserva = await screen.findByText('101');
    const trElement = filaReserva.closest('tr');

    expect(trElement).not.toHaveClass('row-selected-persist');

    fireEvent.click(trElement);
    expect(trElement).toHaveClass('row-selected-persist');

    fireEvent.click(trElement);
    expect(trElement).not.toHaveClass('row-selected-persist');
  });

  test('Redirigir al listado anterior al pulsar el botón de Volver', async () => {
    const mockFetch = vi.fn().mockResolvedValueOnce({
      json: () => Promise.resolve(mockClientWithData),
    });
    vi.stubGlobal('fetch', mockFetch);

    render(
      <BrowserRouter>
        <ClientDetails />
      </BrowserRouter>
    );

    const btnVolver = await screen.findByRole('button', { name: /volver/i });
    fireEvent.click(btnVolver);

    expect(mockNavigate).toHaveBeenCalledWith(-1);
  });
});