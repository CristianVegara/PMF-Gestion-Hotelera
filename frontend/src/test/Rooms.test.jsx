import { render, screen, fireEvent  } from '@testing-library/react';
import { describe, test, expect, vi, beforeEach } from 'vitest';
import Rooms from '../pages/Rooms';

const mockNavigate = vi.fn();
vi.mock('react-router-dom', () => ({
  useNavigate: () => mockNavigate
}));

const mockRooms = [
  { id: 101, number: '101', type: 'Deluxe Suite', status: 'Disponible' },
  { id: 102, number: '102', type: 'Estándar', status: 'Ocupada' }
];

describe('Pruebas en el componente Rooms', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    localStorage.clear();
    localStorage.setItem('user_token', 'mock_token');

    vi.stubGlobal('fetch', vi.fn().mockImplementation((url) => {
      if (url.includes('/api/rooms')) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve(mockRooms)
        });
      }
      return Promise.resolve({ ok: true, json: () => Promise.resolve([]) });
    }));
  });

  test('Cargar el listado de habitaciones y renderizar las tarjetas correctamente', async () => {
    render(<Rooms />);

    expect(await screen.findByRole('heading', { name: /Estado de Habitaciones \(2\)/i })).toBeInTheDocument();

    expect(screen.getByText('Habitación 101')).toBeInTheDocument();
    expect(screen.getByText('Deluxe Suite')).toBeInTheDocument();
    expect(screen.getByText('Disponible')).toBeInTheDocument();

    expect(screen.getByText('Habitación 102')).toBeInTheDocument();
    expect(screen.getByText('Ocupada')).toBeInTheDocument();
  });

  test('Redirigir al detalle de la habitación al hacer clic en una tarjeta', async () => {
    render(<Rooms />);

    const tarjetaHabitacion = await screen.findByTestId('room-card-101');
    
    fireEvent.click(tarjetaHabitacion);

    expect(mockNavigate).toHaveBeenCalledWith('/rooms/101');
  });
});