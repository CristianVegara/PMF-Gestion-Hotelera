import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { vitest } from 'vitest';
import ClientForm from '../pages/ClientForm';
import { BrowserRouter } from 'react-router-dom';

const mockNavigate = vi.fn();

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
    useParams: () => ({ id: null })
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

const renderComponent = () =>
  render(
    <BrowserRouter>
      <ClientForm />
    </BrowserRouter>
  );

  test('renderiza formulario en modo creación', () => {
  renderComponent();

  expect(screen.getByText('Nuevo Cliente')).toBeInTheDocument();
  expect(screen.getByText('Guardar')).toBeInTheDocument();
});

test('envía formulario correctamente (crear cliente)', async () => {
  fetch.mockResolvedValueOnce({
    status: 201,
    json: async () => ({ mensaje: 'Cliente creado' })
  });

  renderComponent();

  fireEvent.change(screen.getByLabelText('DNI'), {
    target: { value: '123A' }
  });

  fireEvent.change(screen.getByLabelText('Nombre Completo'), {
    target: { value: 'Juan' }
  });

  fireEvent.change(screen.getByLabelText('Teléfono'), {
    target: { value: '111' }
  });

  fireEvent.change(screen.getByLabelText('Correo Electrónico'), {
    target: { value: 'juan@test.com' }
  });

  fireEvent.click(screen.getByText('Guardar'));

  await waitFor(() => {
    expect(alert).toHaveBeenCalledWith('Cliente creado');
    expect(mockNavigate).toHaveBeenCalledWith('/clients');
  });
});


test('muestra errores si el backend devuelve 400', async () => {
  fetch.mockResolvedValueOnce({
    status: 400,
    json: async () => ({
      errors: ['DNI inválido', 'Nombre obligatorio']
    })
  });

  renderComponent();

  fireEvent.click(screen.getByText('Guardar'));

  await waitFor(() => {
    expect(screen.getByText('DNI inválido')).toBeInTheDocument();
    expect(screen.getByText('Nombre obligatorio')).toBeInTheDocument();
  });
});

test('carga datos del cliente al editar', async () => {
  fetch.mockResolvedValueOnce({
    ok: true,
    json: async () => mockClient
  });

  renderComponent();

  await waitFor(() => {
    expect(screen.getByDisplayValue('123A')).toBeInTheDocument();
    expect(screen.getByDisplayValue('Juan')).toBeInTheDocument();
    expect(screen.getByDisplayValue('111')).toBeInTheDocument();
    expect(screen.getByDisplayValue('juan@test.com')).toBeInTheDocument();
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

  renderComponent();

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

  renderComponent();

  await waitFor(() => screen.getByDisplayValue('Juan'));

  fireEvent.change(screen.getByLabelText('Nombre Completo'), {
    target: { value: '' }
  });

  fireEvent.click(screen.getByText('Actualizar'));

  await waitFor(() => {
    expect(screen.getByText('Nombre inválido')).toBeInTheDocument();
  });
});