import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter, useParams } from 'react-router-dom';
import { describe, test, expect, vi, beforeEach } from 'vitest';
import ClientForm from '../pages/ClientForm';

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
    useParams: vi.fn(),
  };
});

describe('Pruebas en el componente ClientForm', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    localStorage.clear();
    vi.mocked(useParams).mockReturnValue({ id: undefined });
    vi.spyOn(window, 'alert').mockImplementation(() => {});
  });

  test('Debe mostrar el formulario vacío en modo "Nuevo Cliente"', () => {
    render(
      <BrowserRouter>
        <ClientForm />
      </BrowserRouter>
    );

    expect(screen.getByRole('heading', { name: 'Nuevo Cliente' })).toBeInTheDocument();
    expect(screen.getByLabelText('DNI').value).toBe('');
    expect(screen.getByLabelText('Nombre Completo').value).toBe('');
  });

  test('Cargar los datos del cliente si existe un ID en los parámetros de la URL', async () => {
    vi.mocked(useParams).mockReturnValue({ id: '123' });

    const mockClientData = {
      dni: '12345678A',
      nombre: 'Juan Pérez',
      telefono: '600111222',
      correo: 'juan@email.com'
    };

    const mockFetch = vi.fn().mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve(mockClientData),
    });
    vi.stubGlobal('fetch', mockFetch);

    render(
      <BrowserRouter>
        <ClientForm />
      </BrowserRouter>
    );

    expect(screen.getByRole('heading', { name: 'Editar Cliente' })).toBeInTheDocument();

    await waitFor(() => {
      expect(screen.getByLabelText('DNI').value).toBe('12345678A');
      expect(screen.getByLabelText('Nombre Completo').value).toBe('Juan Pérez');
    });
  });

  test('Enviar los datos correctamente y redirigir al crear un cliente con éxito', async () => {
    const mockFetch = vi.fn().mockResolvedValueOnce({
      status: 201,
      json: () => Promise.resolve({ mensaje: 'Cliente guardado correctamente' }),
    });
    vi.stubGlobal('fetch', mockFetch);

    render(
      <BrowserRouter>
        <ClientForm />
      </BrowserRouter>
    );

    fireEvent.change(screen.getByLabelText('DNI'), { target: { value: '99999999Z' } });
    fireEvent.change(screen.getByLabelText('Nombre Completo'), { target: { value: 'Ana Gómez' } });
    fireEvent.change(screen.getByLabelText('Teléfono'), { target: { value: '654321098' } });
    fireEvent.change(screen.getByLabelText('Correo Electrónico'), { target: { value: 'ana@email.com' } });

    const btnGuardar = screen.getByRole('button', { name: 'Guardar' });
    fireEvent.click(btnGuardar);

    await waitFor(() => {
      expect(window.alert).toHaveBeenCalledWith('Cliente guardado correctamente');
      expect(mockNavigate).toHaveBeenCalledWith('/clients');
    });
  });

  test('Renderizar los errores devueltos por el backend si falla la validación (400)', async () => {
    const mockFetch = vi.fn().mockResolvedValueOnce({
      status: 400,
      json: () => Promise.resolve({ errors: ['El DNI ya está registrado.', 'El correo no es válido.'] }),
    });
    vi.stubGlobal('fetch', mockFetch);

    render(
      <BrowserRouter>
        <ClientForm />
      </BrowserRouter>
    );

    fireEvent.change(screen.getByLabelText('DNI'), { target: { value: '12345678A' } });
    fireEvent.change(screen.getByLabelText('Nombre Completo'), { target: { value: 'Test' } });
    fireEvent.change(screen.getByLabelText('Teléfono'), { target: { value: '123' } });
    fireEvent.change(screen.getByLabelText('Correo Electrónico'), { target: { value: 'test@email.com' } });

    const btnGuardar = screen.getByRole('button', { name: 'Guardar' });
    fireEvent.click(btnGuardar);

    expect(await screen.findByText('El DNI ya está registrado.')).toBeInTheDocument();
    expect(screen.getByText('El correo no es válido.')).toBeInTheDocument();
  });
});