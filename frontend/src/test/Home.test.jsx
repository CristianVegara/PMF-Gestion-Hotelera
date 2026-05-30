import { render, screen, waitFor } from '@testing-library/react';
import { describe, test, expect, vi, beforeEach } from 'vitest';
import Home from '../pages/Home'; 

describe('Pruebas en el componente Home', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
  });

  test('Mostrar el estado de carga inicial y luego un mensaje del backend con éxito', async () => {
    const mockFetch = vi.fn().mockResolvedValueOnce({
      text: () => Promise.resolve('Conexión Exitosa con Spring Boot'),
    });

    vi.stubGlobal('fetch', mockFetch);

    render(<Home />);

    expect(screen.getByText('Bienvenido al Sistema de Gestión')).toBeInTheDocument();
    
    expect(screen.getByText('Backend Status: Cargando...')).toBeInTheDocument();

    await waitFor(() => {
      expect(screen.getByText('Backend Status: Conexión Exitosa con Spring Boot')).toBeInTheDocument();
    });
    
    expect(mockFetch).toHaveBeenCalledWith('/api/test');
  });

  test('Mostrar un mensaje de error si el fetch al backend falla', async () => {
    const mockFetchFail = vi.fn().mockRejectedValueOnce(new Error('Network error'));
    
    vi.stubGlobal('fetch', mockFetchFail);

    render(<Home />);

    await waitFor(() => {
      expect(screen.getByText('Backend Status: Error de conexión')).toBeInTheDocument();
    });
  });
});