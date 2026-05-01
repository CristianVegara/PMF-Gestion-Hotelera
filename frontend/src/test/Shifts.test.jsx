import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { vitest } from 'vitest';
import Shifts from '../pages/Shifts';
import { BrowserRouter } from 'react-router-dom';

vitest.mock('react-calendar', () => ({
  default: (props) => (
    <div>
      <button onClick={() => props.onChange(new Date(2026, 0, 1))}>
        change
      </button>
    </div>
  )
}));

const mockShifts = [
  {
    id: 1,
    fecha: '2026-01-01T00:00:00',
    employee: { nombre: 'Juan', apellido: 'Pérez' },
    schedule: {
      horaEntrada: '08:00',
      horaSalida: '16:00',
      nombreTurno: 'Mañana'
    },
    observaciones: 'Test'
  }
];

const mockEmployees = [{ id: 1, nombre: 'Juan', apellido: 'Pérez' }];
const mockSchedules = [
  { id: 1, nombreTurno: 'Mañana', horaEntrada: '08:00', horaSalida: '16:00' }
];

beforeEach(() => {
  global.fetch = vitest.fn((url) => {
    if (url.includes('/api/shifts')) {
      return Promise.resolve({
        ok: true,
        json: async () => mockShifts
      });
    }

    if (url.includes('/api/employees')) {
      return Promise.resolve({
        ok: true,
        json: async () => mockEmployees
      });
    }

    if (url.includes('/api/schedules')) {
      return Promise.resolve({
        ok: true,
        json: async () => mockSchedules
      });
    }

    return Promise.resolve({
      ok: true,
      json: async () => []
    });
  });
});

afterEach(() => {
  vitest.clearAllMocks();
});

const renderUI = () =>
  render(
    <BrowserRouter>
      <Shifts />
    </BrowserRouter>
  );

test('renderiza correctamente la página', async () => {
  renderUI();
  expect(await screen.findByText(/Gestión de Turnos/i)).toBeInTheDocument();
});

test('carga datos desde API y los muestra', async () => {
  renderUI();
  expect(await screen.findByText(/Juan Pérez/i)).toBeInTheDocument();
  expect(await screen.findByText(/Mañana/i)).toBeInTheDocument();
});

test('permite rellenar formulario', async () => {
  renderUI();

  await screen.findByText(/Gestión de Turnos/i);

  const selects = screen.getAllByRole('combobox');

  fireEvent.change(selects[0], { target: { value: '1' } });
  fireEvent.change(selects[1], { target: { value: '1' } });

  const dateInput = screen.getByRole('textbox', { hidden: true });
  const notesInput = screen.getByPlaceholderText(/Opcional/i);

  fireEvent.change(dateInput, {
    target: { value: '2026-01-01' }
  });

  fireEvent.change(notesInput, {
    target: { value: 'Notas test' }
  });

  expect(selects[0].value).toBe('1');
  expect(selects[1].value).toBe('1');
});

test('muestra estado vacío', async () => {
  global.fetch = vitest.fn(() =>
    Promise.resolve({ ok: true, json: async () => [] })
  );

  renderUI();

  expect(
    await screen.findByText(/Sin turnos asignados/i)
  ).toBeInTheDocument();
});

test('envía formulario correctamente', async () => {
  global.fetch = vitest.fn((url) => {
    if (url.includes('/api/shifts')) {
      return Promise.resolve({
        ok: true,
        json: async () => mockShifts
      });
    }

    if (url.includes('/api/employees')) {
      return Promise.resolve({
        ok: true,
        json: async () => mockEmployees
      });
    }

    if (url.includes('/api/schedules')) {
      return Promise.resolve({
        ok: true,
        json: async () => mockSchedules
      });
    }

    // POST /api/shifts
    return Promise.resolve({
      ok: true,
      json: async () => ({})
    });
  });

  renderUI();

  await screen.findByText(/Gestión de Turnos/i);

  const selects = screen.getAllByRole('combobox');

  fireEvent.change(selects[0], { target: { value: '1' } });
  fireEvent.change(selects[1], { target: { value: '1' } });

  const dateInput = screen.getByRole('textbox', { hidden: true });

  fireEvent.change(dateInput, {
    target: { value: '2026-01-01' }
  });

  const submitButton = screen.getByRole('button', {
    name: /Asignar/i
  });

  fireEvent.click(submitButton);

  await waitFor(() => {
    expect(global.fetch).toHaveBeenCalled();
  });
});