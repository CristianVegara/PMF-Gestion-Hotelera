import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { vitest } from 'vitest';
import RoomForm from '../pages/RoomsForm';
import { BrowserRouter } from 'react-router-dom';

const mockNavigate = vitest.fn();
const mockUseParams = vitest.fn();

vitest.mock('react-router-dom', async () => {
  const actual = await vitest.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
    useParams: () => mockUseParams()
  };
});

beforeEach(() => {
  vitest.clearAllMocks();
});

const renderUI = (params = {}) => {
  mockUseParams.mockReturnValue(params);

  return render(
    <BrowserRouter>
      <RoomForm />
    </BrowserRouter>
  );
};

test('renderiza formulario nuevo', () => {
  renderUI({});
  expect(screen.getByText(/Nueva Habitación/i)).toBeInTheDocument();
});

test('renderiza formulario edición y carga datos', async () => {
  global.fetch = vitest.fn(() =>
    Promise.resolve({
      json: async () => ({
        number: 101,
        type: 'Suite',
        price: 120,
        available: true
      })
    })
  );

  renderUI({ id: '1' });

  expect(await screen.findByDisplayValue('101')).toBeInTheDocument();
  expect(await screen.findByDisplayValue('Suite')).toBeInTheDocument();
  expect(await screen.findByDisplayValue('120')).toBeInTheDocument();
});

test('permite cambiar inputs', () => {
  renderUI({});

  const numberInput = screen.getByLabelText(/Número/i);
  const typeInput = screen.getByLabelText(/Tipo/i);
  const priceInput = screen.getByLabelText(/Precio/i);
  const checkbox = screen.getByRole('checkbox');

  fireEvent.change(numberInput, { target: { value: '202' } });
  fireEvent.change(typeInput, { target: { value: 'Doble' } });
  fireEvent.change(priceInput, { target: { value: '99.99' } });
  fireEvent.click(checkbox);

  expect(numberInput.value).toBe('202');
  expect(typeInput.value).toBe('Doble');
  expect(priceInput.value).toBe('99.99');
  expect(checkbox.checked).toBe(false);
});

test('envía formulario POST', async () => {
  global.fetch = vitest.fn(() =>
    Promise.resolve({
      status: 201,
      json: async () => ({ mensaje: 'Creado' })
    })
  );

  window.alert = vitest.fn();

  renderUI({});

  fireEvent.change(screen.getByLabelText(/Número/i), {
    target: { value: '101' }
  });

  fireEvent.change(screen.getByLabelText(/Tipo/i), {
    target: { value: 'Suite' }
  });

  fireEvent.change(screen.getByLabelText(/Precio/i), {
    target: { value: '120' }
  });

  fireEvent.click(screen.getByText(/Guardar/i));

  await waitFor(() => {
    expect(global.fetch).toHaveBeenCalled();
    expect(window.alert).toHaveBeenCalledWith('Creado');
    expect(mockNavigate).toHaveBeenCalledWith('/rooms');
  });
});


test('muestra errores de validación', async () => {
  global.fetch = vitest.fn(() =>
    Promise.resolve({
      status: 400,
      json: async () => ({
        errors: ['Error 1', 'Error 2']
      })
    })
  );

  renderUI({});

  fireEvent.click(screen.getByText(/Guardar/i));

  expect(await screen.findByText(/Error 1/i)).toBeInTheDocument();
  expect(await screen.findByText(/Error 2/i)).toBeInTheDocument();
});