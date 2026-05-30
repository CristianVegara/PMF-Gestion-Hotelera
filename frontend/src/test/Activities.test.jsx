import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, test, expect, vi, beforeEach } from 'vitest';
import { MemoryRouter } from 'react-router-dom';

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate
  };
});

const mockActivitiesList = [
  {
    id: 1,
    descripcion: 'Torneo de Pádel',
    precio: 15.0,
    fechaComienzo: '2026-05-24T10:00:00',
    fechaFin: '2026-05-24T14:00:00'
  },
  {
    id: 2,
    descripcion: 'Cata de Vinos',
    precio: 30.0,
    fechaComienzo: '2026-05-25T18:00:00',
    fechaFin: '2026-05-25T20:00:00'
  }
];

describe('Panel general de actividades', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    localStorage.clear();
    vi.resetModules(); 
  });

  test('Recupera las actividades y las lista filtradas según el día seleccionado', async () => {
    localStorage.setItem('user_token', 'token_seguro_actividades');
    localStorage.setItem('role', 'ROLE_ADMIN');

    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(mockActivitiesList)
    }));

    const Activities = (await import('../pages/Activities.jsx')).default;

    render(
      <MemoryRouter>
        <Activities />
      </MemoryRouter>
    );

    const actividadDia = await screen.findByText('Torneo de Pádel');
    expect(actividadDia).toBeInTheDocument();
    expect(screen.getByText('15€')).toBeInTheDocument();
    expect(screen.queryByText('Cata de Vinos')).not.toBeInTheDocument();
  });

  test('Envía los datos correctos añadiendo los segundos requeridos por el backend', async () => {
    localStorage.setItem('user_token', 'token_seguro_actividades');
    localStorage.setItem('role', 'ROLE_ADMIN');

    const mockPostFetch = vi.fn().mockResolvedValue({ ok: true });
    
    vi.stubGlobal('fetch', vi.fn().mockImplementation((url, config) => {
      if (config?.method === 'POST') return mockPostFetch(url, config);
      return Promise.resolve({ ok: true, json: () => Promise.resolve(mockActivitiesList) });
    }));

    const Activities = (await import('../pages/Activities.jsx')).default;

    const { container } = render(
      <MemoryRouter>
        <Activities />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByPlaceholderText('Ej: Clase de Yoga'), { target: { value: 'Aquagym Extremo' } });
    fireEvent.change(screen.getByPlaceholderText('0.00'), { target: { value: '12.50' } });
    
    const inputsFecha = container.querySelectorAll('input[type="datetime-local"]');
    fireEvent.change(inputsFecha[0], { target: { value: '2026-06-10T09:00' } });
    fireEvent.change(inputsFecha[1], { target: { value: '2026-06-10T10:30' } });

    const botonEnviar = screen.getByRole('button', { name: /Crear actividad/i });
    fireEvent.click(botonEnviar);

    await waitFor(() => {
      expect(mockPostFetch).toHaveBeenCalledWith('http://localhost:8080/api/activities', expect.objectContaining({
        method: 'POST',
        body: expect.stringContaining('"fechaComienzo":"2026-06-10T09:00:00"')
      }));
    });
  });

  test('Solicita confirmación y borra el evento de la lista', async () => {
    localStorage.setItem('user_token', 'token_seguro_actividades');
    localStorage.setItem('role', 'ROLE_ADMIN');

    vi.spyOn(window, 'confirm').mockReturnValue(true);
    const mockDeleteFetch = vi.fn().mockResolvedValue({ ok: true });

    vi.stubGlobal('fetch', vi.fn().mockImplementation((url, config) => {
      if (config?.method === 'DELETE') return mockDeleteFetch(url, config);
      return Promise.resolve({ ok: true, json: () => Promise.resolve(mockActivitiesList) });
    }));

    const Activities = (await import('../pages/Activities.jsx')).default;

    render(
      <MemoryRouter>
        <Activities />
      </MemoryRouter>
    );

    const botonEliminar = await screen.findByRole('button', { name: /Eliminar/i });
    fireEvent.click(botonEliminar);

    expect(window.confirm).toHaveBeenCalledWith('¿Eliminar esta actividad?');
    expect(mockDeleteFetch).toHaveBeenCalledWith('http://localhost:8080/api/activities/1', expect.any(Object));
  });

  test('Oculta los botones de gestión si el usuario tiene un rol sin permisos', async () => {
    localStorage.setItem('user_token', 'token_seguro_actividades');
    localStorage.setItem('role', 'ROLE_USER');

    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(mockActivitiesList)
    }));

    const Activities = (await import('../pages/Activities.jsx')).default;

    render(
      <MemoryRouter>
        <Activities />
      </MemoryRouter>
    );

    await screen.findByText('Torneo de Pádel');

    expect(screen.queryByRole('button', { name: /Crear actividad/i })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /Eliminar/i })).not.toBeInTheDocument();
  });
});