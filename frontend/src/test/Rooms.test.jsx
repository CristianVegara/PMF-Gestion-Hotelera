import { render, screen, fireEvent, waitFor, within } from '@testing-library/react';
import { vitest } from 'vitest';
import React from 'react';
import Rooms from '../pages/Rooms';
import { BrowserRouter } from 'react-router-dom';

global.fetch = vitest.fn();
global.alert = vitest.fn();

const mockRooms = [
  { id: 1, number: '101', type: 'Simple', status: 'DISPONIBLE', price: 50.0 },
  { id: 2, number: '102', type: 'Doble', status: 'OCUPADA', price: 80.0 }
];

const mockClients = [
  { id: 1, nombre: 'Carlos Perez', dni: '12345678X' },
  { id: 2, nombre: 'Ana Garcia', dni: '87654321Y' }
];

const mockBookings = [
  { 
    id: 10, 
    fechaEntrada: '2026-06-01', 
    fechaSalida: '2026-06-05', 
    estado: 'CONFIRMADA',
    cliente: { id: 1, nombre: 'Carlos Perez', dni: '12345678X' }
  }
];

beforeEach(() => {
  vitest.clearAllMocks();
  fetch.mockImplementation((url) => {
    if (url === '/api/rooms') {
      return Promise.resolve({ ok: true, json: async () => mockRooms });
    }
    if (url === '/api/clients') {
      return Promise.resolve({ ok: true, json: async () => mockClients });
    }
    if (url.includes('/api/bookings/room/')) {
      return Promise.resolve({ ok: true, json: async () => mockBookings });
    }
    return Promise.resolve({ ok: true, json: async () => [] });
  });
});

test('renderiza habitaciones', async () => {
  render(
    <BrowserRouter>
      <Rooms />
    </BrowserRouter>
  );
  const roomElement = await screen.findByText('101');
  expect(roomElement).toBeInTheDocument();
  expect(screen.getByText('102')).toBeInTheDocument();
});

test('cambia vista lista a cuadros', async () => {
  render(
    <BrowserRouter>
      <Rooms />
    </BrowserRouter>
  );
  const gridBtn = await screen.findByText('Cuadros');
  fireEvent.click(gridBtn);
  expect(gridBtn).toHaveClass('active');
});

test('muestra historial al seleccionar habitacion', async () => {
  render(
    <BrowserRouter>
      <Rooms />
    </BrowserRouter>
  );
  const roomRow = await screen.findByText('101');
  fireEvent.click(roomRow);
  const historyBtn = await screen.findByText(/Historial/i);
  fireEvent.click(historyBtn);
  const modalTitle = await screen.findByText(/Historial de Habitación/i);
  expect(modalTitle).toBeInTheDocument();
});

test('filtra clientes en el buscador', async () => {
  render(
    <BrowserRouter>
      <Rooms />
    </BrowserRouter>
  );

  fireEvent.click(await screen.findByText('101'));
  fireEvent.click(await screen.findByText(/Asignar/i));
  
  const searchInput = await screen.findByPlaceholderText(/Buscar cliente/i);
  fireEvent.change(searchInput, { target: { value: 'Ana' } });
  
  const searchResults = document.querySelector('.mini-client-list');
  expect(within(searchResults).getByText('Ana Garcia')).toBeInTheDocument();
  expect(within(searchResults).queryByText('Carlos Perez')).not.toBeInTheDocument();
});

test('crea reserva y muestra alert', async () => {
  fetch.mockImplementation((url, options) => {
    if (options?.method === 'POST') {
      return Promise.resolve({ ok: true, json: async () => ({ mensaje: '¡Reserva creada!' }) });
    }
    if (url === '/api/rooms') return Promise.resolve({ ok: true, json: async () => mockRooms });
    if (url === '/api/clients') return Promise.resolve({ ok: true, json: async () => mockClients });
    return Promise.resolve({ ok: true, json: async () => [] });
  });

  render(
    <BrowserRouter>
      <Rooms />
    </BrowserRouter>
  );

  fireEvent.click(await screen.findByText('101'));
  fireEvent.click(await screen.findByText(/Asignar/i));
  
  const searchInput = await screen.findByPlaceholderText(/Buscar cliente/i);
  fireEvent.change(searchInput, { target: { value: 'Carlos' } });
  
  const clientOption = await screen.findByText('Carlos Perez', { selector: '.client-option-item' });
  fireEvent.click(clientOption);

  const inputs = document.querySelectorAll('input[type="date"]');
  fireEvent.change(inputs[0], { target: { value: '2026-12-01' } });
  fireEvent.change(inputs[1], { target: { value: '2026-12-05' } });

  fireEvent.click(screen.getByText(/Confirmar Registro/i));

  await waitFor(() => {
    expect(alert).toHaveBeenCalledWith('¡Reserva creada!');
  });
});