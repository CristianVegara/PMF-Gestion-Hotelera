import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { vitest } from 'vitest';
import ActivityDetail from '../pages/Activity';
import { MemoryRouter, Routes, Route } from 'react-router-dom';

const mockNavigate = vitest.fn();
vitest.mock('react-router-dom', async () => {
  const actual = await vitest.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate
  };
});

global.fetch = vitest.fn();

const mockActivity = {
  id: 1,
  descripcion: 'Surf Class',
  fechaComienzo: '2026-06-01T10:00:00',
  fechaFin: '2026-06-01T12:00:00',
  maxParticipantes: 5,
  clients: [
    { id: 10, nombre: 'Marcos', dni: '111X' },
    { id: 11, nombre: 'Julia', dni: '222Y' }
  ]
};

const renderComponent = () =>
  render(
    <MemoryRouter initialEntries={['/activities/1']}>
      <Routes>
        <Route path="/activities/:id" element={<ActivityDetail />} />
      </Routes>
    </MemoryRouter>
  );

beforeEach(() => {
  vitest.clearAllMocks();
});

test('muestra el cargando inicialmente', () => {
  fetch.mockReturnValue(new Promise(() => {}));
  renderComponent();
  expect(screen.getByText('Cargando actividad...')).toBeInTheDocument();
});

test('carga y muestra los detalles de la actividad y plazas libres', async () => {
  fetch.mockResolvedValueOnce({
    ok: true,
    json: async () => mockActivity
  });

  renderComponent();

  await waitFor(() => {
    expect(screen.getByText('Surf Class')).toBeInTheDocument();
    expect(screen.getByText('Marcos')).toBeInTheDocument();
    expect(screen.getByText('Julia')).toBeInTheDocument();
  });

  expect(screen.getByText('2')).toBeInTheDocument(); // plazas ocupadas
  expect(screen.getByText('5 plazas')).toBeInTheDocument(); // total
  
  const plazasLibres = screen.getAllByText('Plaza Libre');
  expect(plazasLibres.length).toBe(3);
});

test('muestra mensaje de error si la respuesta no es ok', async () => {
  fetch.mockResolvedValueOnce({
    ok: false
  });

  renderComponent();

  await waitFor(() => {
    expect(screen.getByText('Error: No se pudo encontrar la actividad')).toBeInTheDocument();
  });
});

test('navega a la edición del cliente al hacer click en su plaza', async () => {
  fetch.mockResolvedValueOnce({
    ok: true,
    json: async () => mockActivity
  });

  renderComponent();

  const clienteCard = await screen.findByText('Marcos');
  fireEvent.click(clienteCard.closest('.occupied'));

  expect(mockNavigate).toHaveBeenCalledWith('/clients/edit/10');
});

test('el botón volver regresa a la página anterior', async () => {
  fetch.mockResolvedValueOnce({
    ok: true,
    json: async () => mockActivity
  });

  renderComponent();

  const btnBack = await screen.findByText('← Volver al listado');
  fireEvent.click(btnBack);

  expect(mockNavigate).toHaveBeenCalledWith(-1);
});