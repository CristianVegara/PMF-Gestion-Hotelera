import { render, screen, fireEvent } from '@testing-library/react';
import { describe, test, expect, vi, beforeEach } from 'vitest';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import ActivityDetail from '../pages/Activity.jsx';

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
    useParams: () => ({ id: '12' })
  };
});

const mockActivity = {
  id: 12,
  descripcion: 'Clase de Yoga Frontal',
  fechaComienzo: '2026-06-15T10:00:00',
  fechaFin: '2026-06-15T11:30:00',
  maxParticipantes: 4,
  clients: [
    { id: 1, nombre: 'Juan Pérez', dni: '11223344A' },
    { id: 2, nombre: 'María López', dni: '55667788B' }
  ]
};

const renderComponent = () => {
  return render(
    <MemoryRouter initialEntries={['/activities/12']}>
      <Routes>
        <Route path="/activities/:id" element={<ActivityDetail />} />
      </Routes>
    </MemoryRouter>
  );
};

describe('Detalle de actividad hotelera', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    localStorage.clear();
    localStorage.setItem('user_token', 'token_actividades');
  });

  test('Muestra el mensaje de carga mientras espera la respuesta del servidor', () => {
    vi.stubGlobal('fetch', vi.fn().mockImplementation(() => new Promise(() => {})));

    renderComponent();

    expect(screen.getByText(/Cargando actividad.../i)).toBeInTheDocument();
  });

  test('Muestra un mensaje si el identificador no corresponde a ninguna actividad', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: false
    }));

    renderComponent();

    const mensajeError = await screen.findByText(/Error: No se pudo encontrar la actividad/i);
    expect(mensajeError).toBeInTheDocument();
  });

  test('Muestra el título, horario formateado, badges de plazas y lista de huéspedes', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(mockActivity)
    }));

    renderComponent();

    const titulo = await screen.findByText('Clase de Yoga Frontal');
    expect(titulo).toBeInTheDocument();

    expect(screen.getByText('15/06/2026 10:00h - 11:30h')).toBeInTheDocument();

    expect(screen.getByText('2')).toBeInTheDocument();
    expect(screen.getByText(/4 plazas/i)).toBeInTheDocument();

    expect(screen.getByText('Juan Pérez')).toBeInTheDocument();
    expect(screen.getByText('11223344A')).toBeInTheDocument();
    expect(screen.getByText('María López')).toBeInTheDocument();

    const plazasLibres = screen.getAllByText('Plaza Libre');
    expect(plazasLibres).toHaveLength(2);
  });

  test('Redirecciona a la ficha del huésped al pulsar sobre un hueco ocupado', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(mockActivity)
    }));

    renderComponent();

    const tarjetaJuan = await screen.findByText('Juan Pérez');
    fireEvent.click(tarjetaJuan.closest('.activity-slot'));

    expect(mockNavigate).toHaveBeenCalledWith('/clients/edit/1');
  });

  test('Regresa al listado previo al pulsar el botón de volver', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(mockActivity)
    }));

    renderComponent();

    const botonVolver = await screen.findByRole('button', { name: /Volver al listado/i });
    fireEvent.click(botonVolver);

    expect(mockNavigate).toHaveBeenCalledWith(-1);
  });
});