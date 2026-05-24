import { render, screen, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { describe, test, expect, vi, beforeEach } from 'vitest';
import ClientsManager from '../pages/ClientsManager';

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return { ...actual, useNavigate: () => mockNavigate };
});

describe('Pruebas en el componente ClientsManager', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    localStorage.clear();
    
    const mockFetch = vi.fn().mockResolvedValue({
      json: () => Promise.resolve([])
    });
    vi.stubGlobal('fetch', mockFetch);
  });

  test('Mostrar el título y ocultar el botón de nueva reserva si el usuario es un USER', async () => {
    localStorage.setItem('role', 'ROLE_USER');

    render(
      <BrowserRouter>
        <ClientsManager />
      </BrowserRouter>
    );

    expect(screen.getByText('Panel de Recepción')).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /nueva reserva/i })).not.toBeInTheDocument();
  });

  test('Mostrar el botón de nueva reserva y redirigir correctamente si el usuario es ADMIN', async () => {
    localStorage.setItem('role', 'ROLE_ADMIN');

    render(
      <BrowserRouter>
        <ClientsManager />
      </BrowserRouter>
    );

    const btnReserva = screen.getByRole('button', { name: /nueva reserva/i });
    expect(btnReserva).toBeInTheDocument();

    fireEvent.click(btnReserva);
    expect(mockNavigate).toHaveBeenCalledWith('/bookings/form');
  });

  test('Cambiar de pestaña de forma correcta.', async () => {
    localStorage.setItem('role', 'ROLE_USER');

    render(
      <BrowserRouter>
        <ClientsManager />
      </BrowserRouter>
    );

    expect(screen.getByRole('button', { name: 'In-House' })).toBeInTheDocument();

    const btnEntradas = screen.getByRole('button', { name: 'Entradas' });
    fireEvent.click(btnEntradas);
    expect(btnEntradas).toHaveClass('active');

    const btnSalidas = screen.getByRole('button', { name: 'Salidas' });
    fireEvent.click(btnSalidas);
    expect(btnSalidas).toHaveClass('active');

    const btnListado = screen.getByRole('button', { name: 'Listado General' });
    fireEvent.click(btnListado);
    expect(btnListado).toHaveClass('active');
  });
});