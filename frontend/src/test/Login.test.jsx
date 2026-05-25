import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { describe, test, expect, vi, beforeEach } from 'vitest';
import Login from '../pages/Login';
import { login } from '../services/authService';

vi.mock('../services/authService', () => ({
  login: vi.fn(),
}));

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

describe('Pruebas en el componente Login', () => {
  const mockSetUser = vi.fn();

  beforeEach(() => {
    localStorage.clear();
    vi.clearAllMocks();
  });

  test('Mostrar el formulario de login con sus campos vacíos', () => {
    render(
      <BrowserRouter>
        <Login setUser={mockSetUser} />
      </BrowserRouter>
    );

    expect(screen.getByPlaceholderText('Usuario')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Contraseña')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /iniciar sesión/i })).toBeInTheDocument();
  });

  test('Mostrar un mensaje de error si se intenta enviar vacío', async () => {
    render(
      <BrowserRouter>
        <Login setUser={mockSetUser} />
      </BrowserRouter>
    );

    const boton = screen.getByRole('button', { name: /iniciar sesión/i });
    fireEvent.click(boton);

    expect(await screen.findByText('Rellena todos los campos')).toBeInTheDocument();
  });

  test('Iniciar sesión con éxito y guardar los datos si las credenciales son correctas', async () => {
    login.mockResolvedValueOnce({ token: 'fake-jwt-123', role: 'ADMIN' });

    render(
      <BrowserRouter>
        <Login setUser={mockSetUser} />
      </BrowserRouter>
    );

    fireEvent.change(screen.getByPlaceholderText('Usuario'), { target: { value: 'recepcionista' } });
    fireEvent.change(screen.getByPlaceholderText('Contraseña'), { target: { value: 'password123' } });

    fireEvent.click(screen.getByRole('button', { name: /iniciar sesión/i }));
    expect(login).toHaveBeenCalledWith('recepcionista', 'password123');
	
    await waitFor(() => {
      expect(localStorage.getItem('token')).toBe('fake-jwt-123');
      expect(localStorage.getItem('role')).toBe('ADMIN');
      expect(mockSetUser).toHaveBeenCalledWith({ username: 'recepcionista', role: 'ADMIN' });
      expect(mockNavigate).toHaveBeenCalledWith('/rooms');
    });
  });

  test('Mostrar error si las credenciales fallan en el servidor', async () => {
    login.mockRejectedValueOnce(new Error('Unauthorized'));

    render(
      <BrowserRouter>
        <Login setUser={mockSetUser} />
      </BrowserRouter>
    );

    fireEvent.change(screen.getByPlaceholderText('Usuario'), { target: { value: 'error_user' } });
    fireEvent.change(screen.getByPlaceholderText('Contraseña'), { target: { value: 'wrong_pass' } });
    fireEvent.click(screen.getByRole('button', { name: /iniciar sesión/i }));

    expect(await screen.findByText('Credenciales incorrectas o error de conexión con el servidor')).toBeInTheDocument();
  });
});