import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { vitest } from 'vitest';
import ClientForm from '../pages/ClientForm';
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
global.alert = vitest.fn();

const mockClient = {
  dni: '123A',
  nombre: 'Juan',
  telefono: '111',
  correo: 'juan@test.com'
};

const renderCreate = () =>
  render(
    <MemoryRouter initialEntries={['/clients/new']}>
      <Routes>
        <Route path="/clients/new" element={<ClientForm />} />
      </Routes>
    </MemoryRouter>
  );

const renderEdit = () =>
  render(
    <MemoryRouter initialEntries={['/clients/edit/1']}>
      <Routes>
        <Route path="/clients/edit/:id" element={<ClientForm />} />
      </Routes>
    </MemoryRouter>
  );

beforeEach(() => {
  vitest.clearAllMocks();
});


test('renderiza formulario en modo creación', () => {
  renderCreate();

  expect(screen.getByText('Nuevo Cliente')).toBeInTheDocument();
  expect(screen.getByText('Guardar')).toBeInTheDocument();
});

test('envía formulario correctamente (crear cliente)', async () => {
  fetch.mockResolvedValueOnce({
    status: 201,
    json: async () => ({ mensaje: 'Cliente creado' })
  });
  renderCreate();

  fireEvent.change(screen.getByLabelText('DNI'), {
    target: { value: mockClient.dni }
  });
  fireEvent.change(screen.getByLabelText('Nombre Completo'), {
    target: { value: mockClient.nombre }
  });
  fireEvent.change(screen.getByLabelText('Teléfono'), {
    target: { value: mockClient.telefono }
  });
  fireEvent.change(screen.getByLabelText('Correo Electrónico'), {
    target: { value: mockClient.correo }
  });
  fireEvent.click(screen.getByText('Guardar'));

  await waitFor(() => {
    expect(alert).toHaveBeenCalledWith('Cliente creado');
    expect(mockNavigate).toHaveBeenCalledWith('/clients');
  });
});

test('no envía el formulario si falta el DNI', async () => {
  renderCreate();

  fireEvent.change(screen.getByLabelText('Nombre Completo'), {
    target: { value: mockClient.nombre }
  });
  fireEvent.change(screen.getByLabelText('Teléfono'), {
    target: { value: mockClient.telefono }
  });
  fireEvent.change(screen.getByLabelText('Correo Electrónico'), {
    target: { value: mockClient.correo }
  });

  fireEvent.click(screen.getByText('Guardar'));

  await waitFor(() => {
    expect(fetch).not.toHaveBeenCalled();
  });
});

test('muestra errores si el backend devuelve 400', async () => {
  fetch.mockResolvedValueOnce({
    status: 400,
    json: async () => ({
      errors: ['DNI inválido', 'Nombre obligatorio']
    })
  });
  renderCreate();

  fireEvent.change(screen.getByLabelText('DNI'), {
    target: { value: mockClient.dni }
  });
  fireEvent.change(screen.getByLabelText('Nombre Completo'), {
    target: { value: mockClient.nombre }
  });
  fireEvent.change(screen.getByLabelText('Teléfono'), {
    target: { value: mockClient.telefono }
  });
  fireEvent.change(screen.getByLabelText('Correo Electrónico'), {
    target: { value: mockClient.correo }
  });

  fireEvent.click(screen.getByText('Guardar'));

  expect(await screen.findByText('DNI inválido')).toBeInTheDocument();
  expect(await screen.findByText('Nombre obligatorio')).toBeInTheDocument();
});

test('carga datos del cliente al editar', async () => {
  fetch.mockResolvedValueOnce({
    ok: true,
    json: async () => mockClient
  });

  renderEdit();

  await waitFor(() => {
    expect(screen.getByDisplayValue('Juan')).toBeInTheDocument();
    expect(screen.getByDisplayValue('123A')).toBeInTheDocument();
  });
});

test('edita cliente correctamente', async () => {
  fetch
    .mockResolvedValueOnce({
      ok: true,
      json: async () => mockClient
    })
    .mockResolvedValueOnce({
      status: 200,
      json: async () => ({ mensaje: 'Cliente actualizado' })
    });

  renderEdit();
  await waitFor(() => screen.getByDisplayValue('Juan'));

  fireEvent.change(screen.getByLabelText('Nombre Completo'), {
    target: { value: 'Juan modificado' }
  });

  fireEvent.click(screen.getByText('Actualizar'));

  await waitFor(() => {
    expect(alert).toHaveBeenCalledWith('Cliente actualizado');
    expect(mockNavigate).toHaveBeenCalledWith('/clients');
  });
});

test('muestra errores al editar cliente', async () => {
  fetch
    .mockResolvedValueOnce({
      ok: true,
      json: async () => mockClient
    })
    .mockResolvedValueOnce({
      status: 400,
      json: async () => ({
        errors: ['Nombre inválido']
      })
    });

  renderEdit();

  await waitFor(() => screen.getByDisplayValue('Juan'));

  fireEvent.click(screen.getByText('Actualizar'));

  await waitFor(() => {
    expect(screen.getByText('Nombre inválido')).toBeInTheDocument();
  });
});