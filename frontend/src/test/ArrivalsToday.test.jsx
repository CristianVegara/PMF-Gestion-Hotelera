import { render, screen, waitFor } from '@testing-library/react';
import { describe, test, expect, vi, beforeEach } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import ArrivalsToday from '../pages/ArrivalsToday.jsx';

const mockBookings = [
  {
    id: 101,
    fechaEntrada: '2026-05-24',
    checkInStatus: 0, // PENDIENTE
    cliente: { nombre: 'Carlos Soler' },
    habitacion: { number: '102' }
  },
  {
    id: 102,
    fechaEntrada: '2026-05-24',
    checkInStatus: 1, // DENTRO
    cliente: { nombre: 'Ana Mena' },
    habitacion: { number: '205' }
  }
];

const renderComponent = (date) => {
  return render(
    <MemoryRouter>
      <ArrivalsToday date={date} />
    </MemoryRouter>
  );
};

describe('Componente de entradas del día', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    localStorage.clear();
    localStorage.setItem('user_token', 'token_pruebas_entradas');
  });

  test('Muestra el indicador de carga inicial antes de pintar la tabla', () => {
    vi.stubGlobal('fetch', vi.fn().mockImplementation(() => new Promise(() => {})));

    renderComponent('2026-05-24');

    expect(screen.getByText(/Cargando.../i)).toBeInTheDocument();
  });

  test('Renderiza las reservas recibidas de la API filtradas por la fecha correspondiente', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(mockBookings)
    }));

    renderComponent('2026-05-24');

    const filaCarlos = await screen.findByText('Carlos Soler');
    expect(filaCarlos).toBeInTheDocument();
    expect(screen.getByText('#102')).toBeInTheDocument();
    expect(screen.getByText('PENDIENTE')).toBeInTheDocument();

    expect(screen.getByText('Ana Mena')).toBeInTheDocument();
    expect(screen.getByText('#205')).toBeInTheDocument();
    expect(screen.getByText('DENTRO')).toBeInTheDocument();
  });

  test('Ordena las entradas de forma que las pendientes aparezcan al principio', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(mockBookings)
    }));

    renderComponent('2026-05-24');

    await screen.findByText('Carlos Soler');

    const celdasHuespedes = screen.getAllByRole('row').slice(1).map(row => row.children[1].textContent);
    
    expect(celdasHuespedes[0]).toBe('Carlos Soler'); // PENDIENTE (0) debe ir primero
    expect(celdasHuespedes[1]).toBe('Ana Mena');    // DENTRO (1) debe ir después
  });

  test('Controla los errores de la API vaciando la lista', async () => {
    vi.stubGlobal('fetch', vi.fn().mockRejectedValue(new Error('Error de conexión')));

    renderComponent('2026-05-24');

    await waitFor(() => {
      expect(screen.queryByText(/Cargando.../i)).not.toBeInTheDocument();
    });

    expect(screen.queryByText('Carlos Soler')).not.toBeInTheDocument();
    expect(screen.queryByText('Ana Mena')).not.toBeInTheDocument();
  });
});