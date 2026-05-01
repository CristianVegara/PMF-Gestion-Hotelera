import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import Clients from '../pages/Clients';
import { BrowserRouter } from 'react-router-dom';
import { vitest } from 'vitest';

global.fetch = vitest.fn();
global.confirm = vitest.fn();

const mockClients = [
  { id: 1, dni: '123A', nombre: 'Juan', telefono: '111', correo: 'juan@test.com' },
  { id: 2, dni: '456B', nombre: 'Ana', telefono: '222', correo: 'ana@test.com' }
];

const renderComponent = () =>
  render(
    <BrowserRouter>
      <Clients />
    </BrowserRouter>
  );

beforeEach(() => {
  fetch.mockClear();
  global.confirm.mockClear();
});

test('carga y muestra clientes', async () => {
  fetch.mockResolvedValueOnce({
    json: async () => mockClients
  });

  renderComponent();

  expect(fetch).toHaveBeenCalled();

  await waitFor(() => {
    expect(screen.getByText('Juan')).toBeInTheDocument();
    expect(screen.getByText('Ana')).toBeInTheDocument();
  });
});

test('elimina cliente de la lista después de confirmar', async () => {
  fetch
    .mockResolvedValueOnce({
      json: async () => mockClients
    })
    .mockResolvedValueOnce({ ok: true })
    .mockResolvedValueOnce({
      json: async () => [mockClients[1]]
    });

  global.confirm.mockReturnValue(true);

  renderComponent();

  await waitFor(() => screen.getByText('Juan'));

  const deleteButtons = screen.getAllByText('Eliminar');
  fireEvent.click(deleteButtons[0]);

  await waitFor(() => {
    expect(screen.queryByText('Juan')).not.toBeInTheDocument();
    expect(screen.getByText('Ana')).toBeInTheDocument();
  });
});

test('no elimina el cliente si el usuario cancela en el popup', async () => {
  fetch.mockResolvedValueOnce({
    json: async () => mockClients
  });

  global.confirm.mockReturnValue(false);

  renderComponent();

  await waitFor(() => screen.getByText('Juan'));

  const deleteButtons = screen.getAllByText('Eliminar');
  fireEvent.click(deleteButtons[0]);

  await waitFor(() => {
    expect(screen.getByText('Juan')).toBeInTheDocument();
    expect(screen.getByText('Ana')).toBeInTheDocument();
  });

  expect(fetch).toHaveBeenCalledTimes(1);
});