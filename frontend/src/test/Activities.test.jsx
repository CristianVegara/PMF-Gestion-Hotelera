import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { vitest } from 'vitest';
import Activities from '../pages/Activities';
import { BrowserRouter } from 'react-router-dom';

const mockNavigate = vitest.fn();
vitest.mock('react-router-dom', async () => {
  const actual = await vitest.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate
  };
});

global.fetch = vitest.fn();
global.confirm = vitest.fn();

const mockActivities = [
  {
    id: 1,
    descripcion: 'Actividad Test',
    precio: 10,
    fechaComienzo: new Date().toISOString(),
    fechaFin: new Date().toISOString()
  }
];

const renderComponent = () =>
  render(
    <BrowserRouter>
      <Activities />
    </BrowserRouter>
  );

beforeEach(() => {
  vitest.clearAllMocks();
});

test('carga y muestra actividades al iniciar', async () => {
  fetch.mockResolvedValueOnce({
    ok: true,
    json: async () => mockActivities
  });

  renderComponent();

  await waitFor(() => {
    expect(screen.getByText('Actividad Test')).toBeInTheDocument();
  });
});

test('crea una actividad y limpia el formulario', async () => {
  fetch
    .mockResolvedValueOnce({ ok: true, json: async () => [] })
    .mockResolvedValueOnce({ ok: true, json: async () => ({}) })
    .mockResolvedValueOnce({ ok: true, json: async () => mockActivities });

  renderComponent();

  fireEvent.change(screen.getByPlaceholderText('Ej: Clase de Yoga'), {
    target: { value: 'Nueva' }
  });
  fireEvent.change(screen.getByPlaceholderText('0.00'), {
    target: { value: '20' }
  });

  const inputs = document.querySelectorAll('input[type="datetime-local"]');
  fireEvent.change(inputs[0], { target: { value: '2026-05-01T10:00' } });
  fireEvent.change(inputs[1], { target: { value: '2026-05-01T11:00' } });

  fireEvent.click(screen.getByText('Crear actividad'));

  await waitFor(() => {
    expect(screen.getByPlaceholderText('Ej: Clase de Yoga').value).toBe('');
    expect(fetch).toHaveBeenCalledTimes(3);
  });
});

test('elimina actividad si el usuario confirma', async () => {
  fetch
    .mockResolvedValueOnce({ ok: true, json: async () => mockActivities })
    .mockResolvedValueOnce({ ok: true, json: async () => ({}) })
    .mockResolvedValueOnce({ ok: true, json: async () => [] });

  global.confirm.mockReturnValue(true);

  renderComponent();

  const deleteBtn = await screen.findByText('Eliminar');
  fireEvent.click(deleteBtn);

  await waitFor(() => {
    expect(screen.queryByText('Actividad Test')).not.toBeInTheDocument();
  });
});

test('no elimina actividad si el usuario cancela', async () => {
  fetch.mockResolvedValueOnce({
    ok: true,
    json: async () => mockActivities
  });

  global.confirm.mockReturnValue(false);

  renderComponent();

  const deleteBtn = await screen.findByText('Eliminar');
  fireEvent.click(deleteBtn);

  expect(fetch).toHaveBeenCalledTimes(1);
  expect(screen.getByText('Actividad Test')).toBeInTheDocument();
});

test('navega al detalle al hacer click en la actividad', async () => {
  fetch.mockResolvedValueOnce({
    ok: true,
    json: async () => mockActivities
  });

  renderComponent();

  const card = await screen.findByText('Actividad Test');
  fireEvent.click(card.closest('.clickable-card'));

  expect(mockNavigate).toHaveBeenCalledWith('/activities/1');
});