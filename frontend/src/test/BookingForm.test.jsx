import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, test, expect, vi, beforeEach } from 'vitest';
import BookingForm from '../pages/BookingForm.jsx';

const mockNavigate = vi.fn();
vi.mock('react-router-dom', () => ({
  useNavigate: () => mockNavigate
}));

const mockClients = [
  { id: 1, nombre: 'Alejandro Sanz', dni: '12345678A' },
  { id: 2, nombre: 'Beatriz Luengo', dni: '87654321B' }
];

describe('Formulario de reservas', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    localStorage.clear();
    localStorage.setItem('user_token', 'booking_form_token');
    vi.spyOn(window, 'alert').mockImplementation(() => {});

    vi.stubGlobal('fetch', vi.fn().mockImplementation((url) => {
      if (url.includes('/api/clients')) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve(mockClients)
        });
      }
      return Promise.resolve({ ok: true });
    }));
  });

  test('Muestra la lista de clientes y permite filtrarlos usando el buscador', async () => {
    render(<BookingForm />);

    expect(screen.getByText(/Cargando lista de clientes.../i)).toBeInTheDocument();

    const optionAlejandro = await screen.findByText(/Alejandro Sanz — DNI: 12345678A/i);
    expect(optionAlejandro).toBeInTheDocument();
    expect(screen.getByText(/Beatriz Luengo/i)).toBeInTheDocument();

    const inputBuscar = screen.getByPlaceholderText('Escribe para filtrar...');
    fireEvent.change(inputBuscar, { target: { value: 'Beatriz' } });

    expect(screen.queryByText(/Alejandro Sanz/i)).not.toBeInTheDocument();
    expect(screen.getByText(/Beatriz Luengo/i)).toBeInTheDocument();
  });

  test('Muestra un aviso si se intenta enviar el formulario vacío', async () => {
    const { container } = render(<BookingForm />);
    
    await screen.findByText(/Alejandro Sanz/i);

    const form = container.querySelector('form');
    fireEvent.submit(form);

    expect(window.alert).toHaveBeenCalledWith('Por favor, selecciona un huésped.');
    expect(mockNavigate).not.toHaveBeenCalled();
  });

  test('Guarda la reserva correctamente con los datos y fechas formateadas', async () => {
    const mockPostFetch = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve({ mensaje: 'Reserva guardada' })
    });
    
    vi.stubGlobal('fetch', vi.fn().mockImplementation((url, config) => {
      if (url.includes('/api/clients')) {
        return Promise.resolve({ ok: true, json: () => Promise.resolve(mockClients) });
      }
      return mockPostFetch(url, config);
    }));

    render(<BookingForm />);

    const selectBox = await screen.findByRole('listbox');
    fireEvent.change(selectBox, { target: { value: '1' } });

    const inputEntrada = screen.getByPlaceholderText('Seleccionar entrada');
    const inputSalida = screen.getByPlaceholderText('Seleccionar salida');

    fireEvent.change(inputEntrada, { target: { value: '10/06/2026' } });
    fireEvent.change(inputSalida, { target: { value: '15/06/2026' } });

    const comboboxes = screen.getAllByRole('combobox');
    const selectRoomType = comboboxes.find(el => el.className.includes('choice-box')) || comboboxes[0];
    
    fireEvent.change(selectRoomType, { target: { value: '2' } });

    const btnSubmit = screen.getByRole('button', { name: /Confirmar Reserva/i });
    fireEvent.click(btnSubmit);

    await waitFor(() => {
      expect(mockPostFetch).toHaveBeenCalledWith('http://localhost:8080/api/bookings', expect.objectContaining({
        method: 'POST',
        body: expect.stringContaining('"roomType":2')
      }));
      
      const enviadoPayload = JSON.parse(mockPostFetch.mock.calls[0][1].body);
      expect(enviadoPayload.fechaEntrada).toBe('2026-06-10');
      expect(enviadoPayload.fechaSalida).toBe('2026-06-15');
      
      expect(window.alert).toHaveBeenCalledWith('Reserva creada correctamente.');
      expect(mockNavigate).toHaveBeenCalledWith('/bookings');
    });
  });
});