import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { describe, test, expect, vi, beforeEach } from 'vitest';
import UserForm from '../pages/UserForm';

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

const mockEmployees = [
  { id: '10', fullName: 'Carlos Tévez' },
  { id: '20', fullName: 'Lucas Pratto' }
];

describe('Pruebas en el componente UserForm', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    localStorage.clear();
    vi.spyOn(window, 'alert').mockImplementation(() => {});
    
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(mockEmployees)
    }));
  });

  test('Renderizar los campos vacíos del formulario y cargar los empleados', async () => {
    render(
      <BrowserRouter>
        <UserForm />
      </BrowserRouter>
    );

    expect(screen.getByRole('heading', { name: 'Nuevo Usuario' })).toBeInTheDocument();
    expect(screen.getByRole('textbox').value).toBe('');

    const opcionEmpleado = await screen.findByRole('option', { name: 'Carlos Tévez' });
    expect(opcionEmpleado).toBeInTheDocument();
  });

  test('Enviar los datos estructurados y redirigir al crear un usuario con éxito', async () => {
    const mockFetch = vi.fn()
      .mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve(mockEmployees),
      })
      .mockResolvedValueOnce({
        status: 201,
        json: () => Promise.resolve({ mensaje: 'Usuario creado perfectamente' }),
      });
    vi.stubGlobal('fetch', mockFetch);

    const { container } = render(
      <BrowserRouter>
        <UserForm />
      </BrowserRouter>
    );

    await screen.findByRole('option', { name: 'Carlos Tévez' });

    fireEvent.change(screen.getByRole('textbox'), { target: { value: 'admin_hotel' } });
    fireEvent.change(container.querySelector('input[type="password"]'), { target: { value: 'secret123' } });
    fireEvent.change(container.querySelector('select[name="role"]'), { target: { value: 'ADMIN' } });
    fireEvent.change(container.querySelector('select[name="employee"]'), { target: { value: '10' } });

    const btnGuardar = screen.getByRole('button', { name: /guardar/i });
    fireEvent.click(btnGuardar);

    await waitFor(() => {
      expect(mockFetch).toHaveBeenCalledWith('/api/users', expect.objectContaining({
        method: 'POST',
        body: JSON.stringify({
          username: 'admin_hotel',
          passwordHash: 'secret123',
          role: 'ADMIN',
          employee: { id: '10' }
        })
      }));
      expect(window.alert).toHaveBeenCalledWith('Usuario creado perfectamente');
      expect(mockNavigate).toHaveBeenCalledWith('/users');
    });
  });

  test('Mostrar errores de validación si el backend responde con un status 400', async () => {
    const mockFetch = vi.fn()
      .mockResolvedValueOnce({
        ok: true,
        json: () => Promise.resolve(mockEmployees),
      })
      .mockResolvedValueOnce({
        status: 400,
        json: () => Promise.resolve({ errors: ['El nombre de usuario ya existe.', 'La contraseña es muy corta.'] }),
      });
    vi.stubGlobal('fetch', mockFetch);

    const { container } = render(
      <BrowserRouter>
        <UserForm />
      </BrowserRouter>
    );

    await screen.findByRole('option', { name: 'Carlos Tévez' });

    fireEvent.change(screen.getByRole('textbox'), { target: { value: 'test' } });
    fireEvent.change(container.querySelector('input[type="password"]'), { target: { value: '123' } });
    fireEvent.change(container.querySelector('select[name="role"]'), { target: { value: 'USER' } });

    const btnGuardar = screen.getByRole('button', { name: /guardar/i });
    fireEvent.click(btnGuardar);

    expect(await screen.findByText('El nombre de usuario ya existe.')).toBeInTheDocument();
    expect(screen.getByText('La contraseña es muy corta.')).toBeInTheDocument();
  });

  test('Redirigir al listado de usuarios al hacer clic en Cancelar', () => {
    render(
      <BrowserRouter>
        <UserForm />
      </BrowserRouter>
    );

    const btnCancelar = screen.getByRole('button', { name: /cancelar/i });
    fireEvent.click(btnCancelar);

    expect(mockNavigate).toHaveBeenCalledWith('/users');
  });
});