import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { vitest } from 'vitest';
import Invoice from '../pages/Invoice';
import { BrowserRouter } from 'react-router-dom';

global.fetch = vitest.fn();
global.confirm = vitest.fn();

const mockInvoices = [
  {
    id: 1,
    concepto: 'Reserva Suite',
    noches: 2,
    loyaltyRank: 'Bronze',
    discountAmount: 10.00,
    discountPercentage: 5,
    total: 200.50,
    cliente: { nombre: 'Carlos', dni: '123X' }
  },
  {
    id: 2,
    concepto: 'Estancia Estándar',
    noches: 1,
    loyaltyRank: 'Sin rango',
    discountAmount: 0,
    discountPercentage: 0,
    total: 80.00,
    cliente: { nombre: 'Marta', dni: '456Y' }
  }
];

const renderComponent = () =>
  render(
    <BrowserRouter>
      <Invoice />
    </BrowserRouter>
  );

beforeEach(() => {
  vitest.clearAllMocks();
});

test('carga y muestra las facturas al iniciar', async () => {
  fetch.mockResolvedValueOnce({
    ok: true,
    json: async () => mockInvoices
  });

  renderComponent();

  expect(fetch).toHaveBeenCalledWith('/api/invoice?sortBy=id&direction=asc');

  await waitFor(() => {
    expect(screen.getByText('Reserva Suite')).toBeInTheDocument();
    expect(screen.getByText('Carlos')).toBeInTheDocument();
    expect(screen.getByText('Bronze')).toBeInTheDocument();
    expect(screen.getByText('10.00 € (5%)')).toBeInTheDocument();
    expect(screen.getByText('200.50 €')).toBeInTheDocument();
  });
});

test('cambia el ordenamiento al seleccionar una opción diferente', async () => {
  fetch.mockResolvedValue({
    ok: true,
    json: async () => []
  });

  renderComponent();

  const selectSort = screen.getByLabelText(/Ordenar por:/i);
  fireEvent.change(selectSort, { target: { value: 'total' } });

  await waitFor(() => {
    expect(fetch).toHaveBeenCalledWith('/api/invoice?sortBy=total&direction=asc');
  });
});

test('elimina una factura tras confirmar el aviso', async () => {
  fetch
    .mockResolvedValueOnce({ ok: true, json: async () => mockInvoices })
    .mockResolvedValueOnce({ ok: true })
    .mockResolvedValueOnce({ ok: true, json: async () => [mockInvoices[1]] });

  global.confirm.mockReturnValue(true);

  renderComponent();

  const deleteButtons = await screen.findAllByText('Eliminar');
  fireEvent.click(deleteButtons[0]);

  expect(global.confirm).toHaveBeenCalledWith('¿Eliminar factura?');

  await waitFor(() => {
    expect(screen.queryByText('Reserva Suite')).not.toBeInTheDocument();
    expect(screen.getByText('Estancia Estándar')).toBeInTheDocument();
  });
});

test('no elimina la factura si el usuario cancela el confirm', async () => {
  fetch.mockResolvedValueOnce({
    ok: true,
    json: async () => mockInvoices
  });

  global.confirm.mockReturnValue(false);

  renderComponent();

  const deleteButtons = await screen.findAllByText('Eliminar');
  fireEvent.click(deleteButtons[0]);

  expect(fetch).toHaveBeenCalledTimes(1);
  expect(screen.getByText('Reserva Suite')).toBeInTheDocument();
});
