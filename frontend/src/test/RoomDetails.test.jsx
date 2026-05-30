import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, test, expect, vi, beforeEach } from 'vitest';
import RoomDetails from '../pages/RoomDetails.jsx';

const mockNavigate = vi.fn();
vi.mock('react-router-dom', () => ({
  useParams: () => ({ id: '101' }),
  useNavigate: () => mockNavigate
}));

vi.mock('react-datepicker', () => ({
  __esModule: true,
  default: () => <div data-testid="mock-datepicker">DatePicker Simulado</div>,
  registerLocale: vi.fn()
}));

const mockRoom = { id: 101, number: '101', type: 'SUITE', price: 250, status: 'LIBRE' };
const mockBookings = [
  {
    id: 1,
    fechaEntrada: '2026-05-24',
    fechaSalida: '2026-05-28',
    cliente: { id: 5, nombre: 'Carlos Mendoza' }
  }
];

describe('Pruebas en el componente RoomDetails', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    localStorage.clear();
    localStorage.setItem('user_token', 'token_seguro');
    localStorage.setItem('role', 'ROLE_ADMIN');
    
    vi.spyOn(window, 'confirm').mockImplementation(() => true);
    vi.spyOn(window, 'alert').mockImplementation(() => {});

    vi.stubGlobal('fetch', vi.fn().mockImplementation((url) => {
      if (url.includes('/api/rooms/101')) {
        return Promise.resolve({ json: () => Promise.resolve(mockRoom), ok: true });
      }
      if (url.includes('/api/bookings/room/101')) {
        return Promise.resolve({ json: () => Promise.resolve(mockBookings), ok: true });
      }
      return Promise.resolve({ ok: true });
    }));
  });

  test('Renderiza la información de la habitación y su historial de estancias', async () => {
    render(<RoomDetails />);

    expect(screen.getByText('Cargando...')).toBeInTheDocument();
    expect(await screen.findByRole('heading', { name: 'Habitación 101' })).toBeInTheDocument();

    expect(screen.getByText('SUITE')).toBeInTheDocument();
    expect(screen.getByText('250€')).toBeInTheDocument();
    expect(screen.getByText('LIBRE')).toBeInTheDocument();

    expect(screen.getByText('Carlos Mendoza')).toBeInTheDocument();
  });

  test('Permite cambiar el estado de la habitación al hacer clic en un botón de acción', async () => {
    const mockFetch = vi.fn().mockImplementation((url) => {
      if (url.includes('/api/rooms/101')) {
        return Promise.resolve({ json: () => Promise.resolve(mockRoom), ok: true });
      }
      if (url.includes('/api/bookings/room/101')) {
        return Promise.resolve({ json: () => Promise.resolve(mockBookings), ok: true });
      }
      return Promise.resolve({ ok: true }); 
    });
    vi.stubGlobal('fetch', mockFetch);

    render(<RoomDetails />);
    
    await screen.findByRole('heading', { name: 'Habitación 101' });

    const btnSucia = screen.getByRole('button', { name: 'Marcar Sucia' });
    fireEvent.click(btnSucia);

    expect(window.confirm).toHaveBeenCalledWith('¿Está seguro de cambiar el estado de la habitación a SUCIA?');

    await waitFor(() => {
      expect(mockFetch).toHaveBeenCalledWith(
        'http://localhost:8080/api/rooms/101',
        expect.objectContaining({ method: 'PUT' })
      );
      expect(window.alert).toHaveBeenCalledWith('Estado de la habitación actualizado con éxito.');
    });
  });

  test('No debe mostrar los botones de acción si el usuario tiene rol básico USER', async () => {
    localStorage.setItem('role', 'ROLE_USER'); 

    render(<RoomDetails />);
    await screen.findByRole('heading', { name: 'Habitación 101' });

    expect(screen.queryByRole('button', { name: 'Marcar Sucia' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Fuera de Servicio' })).not.toBeInTheDocument();
  });
});