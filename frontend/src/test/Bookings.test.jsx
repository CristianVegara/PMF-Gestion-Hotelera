import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, test, expect, vi, beforeEach } from 'vitest';
import Bookings from '../pages/Bookings.jsx';

const mockNavigate = vi.fn();
vi.mock('react-router-dom', () => ({
  useNavigate: () => mockNavigate
}));

const mockBookings = [
  {
    id: 101,
    cliente: { id: 1, nombre: 'Ana López' },
    habitacion: { number: 204 },
    fechaEntrada: '2026-05-15',
    fechaSalida: '2026-05-20',
    estado: 'CONFIRMADA'
  },
  {
    id: 102,
    cliente: { id: 2, nombre: 'Carlos Ruiz' },
    habitacion: { number: 101 },
    fechaEntrada: '2025-12-01',
    fechaSalida: '2025-12-05',
    estado: 'TERMINADA'
  }
];

describe('Pruebas en Bookings', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    localStorage.clear();
    localStorage.setItem('user_token', 'valid_token');
    
    vi.stubGlobal('fetch', vi.fn().mockImplementation((url) => {
      if (url.includes('/api/bookings')) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve(mockBookings)
        });
      }
      return Promise.resolve({ ok: true });
    }));
  });

  test('Cargar y listar las reservas filtrando por el año actual inicialmente', async () => {
      localStorage.setItem('role', 'ROLE_ADMIN');
      render(<Bookings />);
      expect(screen.getByText(/Cargando reservas.../i)).toBeInTheDocument();//Pantalla de carga
      expect(await screen.findByText('Ana López')).toBeInTheDocument();
      expect(screen.queryByText('Carlos Ruiz')).not.toBeInTheDocument();
      
      expect(screen.getByText(/Filtradas:/i)).toBeInTheDocument();
      expect(screen.getByText(/de 2/i)).toBeInTheDocument();
    });

  test('Ocultar los botones de acción si el usuario tiene el rol USER', async () => {
    localStorage.setItem('role', 'ROLE_USER');
    render(<Bookings />);

    expect(await screen.findByText('Ana López')).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /Editar/i })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /Borrar/i })).not.toBeInTheDocument();
  });

  test('Abrir el modal de edición y enviar un PUT al actualizar una reserva', async () => {
    localStorage.setItem('role', 'ROLE_ADMIN');
    
    const mockFetch = vi.fn().mockImplementation((url) => {
      if (url.endsWith('/api/bookings')) {
        return Promise.resolve({ ok: true, json: () => Promise.resolve(mockBookings) });
      }
      return Promise.resolve({ ok: true });
    });
    vi.stubGlobal('fetch', mockFetch);

    render(<Bookings />);
	
    const btnEditar = await screen.findByRole('button', { name: /Editar/i });
    fireEvent.click(btnEditar);

    expect(screen.getByRole('heading', { name: /Editar Reserva #101/i })).toBeInTheDocument();
	
    const selectEstado = screen.getByRole('combobox');
    fireEvent.change(selectEstado, { target: { value: 'TERMINADA' } });

    const btnGuardar = screen.getByRole('button', { name: /Guardar/i });
    fireEvent.click(btnGuardar);

    await waitFor(() => {
      expect(mockFetch).toHaveBeenCalledWith('http://localhost:8080/api/bookings/101', expect.objectContaining({
        method: 'PUT',
        body: expect.stringContaining('"estado":"TERMINADA"')
      }));
    });
  });

  test('Limpiar los filtros al pulsar el botón "Limpiar"', async () => {
    localStorage.setItem('role', 'ROLE_ADMIN');
    render(<Bookings />);

    expect(await screen.findByText('Ana López')).toBeInTheDocument();

    const inputAño = screen.getByPlaceholderText('Año');
    
    fireEvent.change(inputAño, { target: { value: '' } });
    expect(screen.getByText('Carlos Ruiz')).toBeInTheDocument(); 

    const btnLimpiar = screen.getByRole('button', { name: /Limpiar/i });
    fireEvent.click(btnLimpiar);

    expect(inputAño.value).toBe(new Date().getFullYear().toString());
    expect(screen.queryByText('Carlos Ruiz')).not.toBeInTheDocument();
  });
});