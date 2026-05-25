import { render, screen, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { describe, test, expect, vi, beforeEach } from 'vitest';
import Navbar from '../components/Navbar';

describe('Pruebas en el componente Navbar', () => {
    const mockSetUser = vi.fn();

    beforeEach(() => {
        localStorage.clear();
        vi.restoreAllMocks();
    });

    test('Mostrar solo la opción de Login si no hay usuario autenticado', () => {
        render(
            <BrowserRouter>
                <Navbar user={null} setUser={mockSetUser} />
            </BrowserRouter>
        );

        expect(screen.getByText('Gestion Mediterraneo')).toBeInTheDocument();

        expect(screen.getByRole('link', { name: /login/i })).toBeInTheDocument();

        expect(screen.queryByText('Recepción')).not.toBeInTheDocument();
        expect(screen.queryByText('Habitaciones')).not.toBeInTheDocument();
    });

    test('Mostrar el menú completo y el nombre si el usuario está autenticado', () => {
        const fakeUser = { username: 'admin_test', role: 'ADMIN' };

        render(
            <BrowserRouter>
                <Navbar user={fakeUser} setUser={mockSetUser} />
            </BrowserRouter>
        );

        expect(screen.getByText('Recepción')).toBeInTheDocument();
        expect(screen.getByText('Facturas')).toBeInTheDocument();
        expect(screen.getByText('Habitaciones')).toBeInTheDocument();
        expect(screen.getByText('Reservas')).toBeInTheDocument();

        expect(screen.getByText('👤 admin_test')).toBeInTheDocument();
    });

    test('Limpiar el localStorage y llamar a setUser al hacer clic en Logout', () => {
        const fakeUser = { username: 'recepcionista1', role: 'USER' };
        
        localStorage.setItem("token", "token-de-prueba");
        localStorage.setItem("user", JSON.stringify(fakeUser));

        render(
            <BrowserRouter>
                <Navbar user={fakeUser} setUser={mockSetUser} />
            </BrowserRouter>
        );

        const botonLogout = screen.getByRole('button', { name: /logout/i });
        fireEvent.click(botonLogout);

        expect(localStorage.getItem("token")).toBeNull();
        expect(localStorage.getItem("user")).toBeNull();
        expect(mockSetUser).toHaveBeenCalledWith(null);
    });
});