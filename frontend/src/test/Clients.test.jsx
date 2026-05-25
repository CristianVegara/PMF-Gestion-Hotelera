import { render, screen, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { describe, test, expect, vi, beforeEach } from 'vitest';
import Clients from '../pages/Clients';

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return { ...actual, useNavigate: () => mockNavigate };
});

const mockClientsData = [
  { id: 1, dni: '12345678A', nombre: 'Juan Pérez', telefono: '600111222', correo: 'juan@mail.com' },
  { id: 2, dni: '87654321B', nombre: 'Ana Gómez', telefono: '600333444', correo: 'ana@mail.com' }
];

describe('Pruebas en el componente <Clients />', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    localStorage.clear();
  });

  test('Renderizar la tabla con clientes y ocultar botones de admin si el usuario es un USER', async () => {
    localStorage.setItem('role', 'ROLE_USER');
    
    const mockFetch = vi.fn().mockResolvedValueOnce({
      json: () => Promise.resolve(mockClientsData),
    });
    vi.stubGlobal('fetch', mockFetch);

    render(
      <BrowserRouter>
        <Clients />
      </BrowserRouter>
    );

    expect(await screen.findByText('Juan Pérez')).toBeInTheDocument();
    expect(screen.getByText('Ana Gómez')).toBeInTheDocument();

    expect(screen.queryByText('Nuevo Cliente')).not.toBeInTheDocument();
    expect(screen.queryByText('Editar')).not.toBeInTheDocument();
    expect(screen.queryByText('Eliminar')).not.toBeInTheDocument();
  });

  test('Mostrar todas las acciones de gestión si el usuario es un ADMIN', async () => {
    localStorage.setItem('role', 'ROLE_ADMIN');
    
    const mockFetch = vi.fn().mockResolvedValueOnce({
      json: () => Promise.resolve(mockClientsData),
    });
    vi.stubGlobal('fetch', mockFetch);

    render(
      <BrowserRouter>
        <Clients />
      </BrowserRouter>
    );

    expect(await screen.findByText('Juan Pérez')).toBeInTheDocument();

    expect(screen.getByText('Nuevo Cliente')).toBeInTheDocument();
    expect(screen.getAllByText('Editar')[0]).toBeInTheDocument();
    expect(screen.getAllByText('Eliminar')[0]).toBeInTheDocument();
  });

  test('Filtrar la lista de clientes cuando se escribe en el buscador de DNI', async () => {
    localStorage.setItem('role', 'ROLE_ADMIN');
    
    const mockFetch = vi.fn().mockResolvedValueOnce({
      json: () => Promise.resolve(mockClientsData),
    });
    vi.stubGlobal('fetch', mockFetch);

    render(
      <BrowserRouter>
        <Clients />
      </BrowserRouter>
    );

    expect(await screen.findByText('Juan Pérez')).toBeInTheDocument();
    
    const searchInput = screen.getByPlaceholderText('Ej: 12345678A');
    fireEvent.change(searchInput, { target: { value: '87654321B' } });

    expect(screen.queryByText('Juan Pérez')).not.toBeInTheDocument();
    expect(screen.getByText('Ana Gómez')).toBeInTheDocument();
  });
});