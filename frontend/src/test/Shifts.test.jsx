import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, test, expect, vi, beforeEach, afterEach } from 'vitest';
import Shifts from '../pages/Shifts';

const mockShifts = [
  {
    id: 1,
    fecha: '2026-05-24T00:00:00',
    observaciones: 'Cubrir recepción principal',
    employee: { id: 10, nombre: 'Ana', apellido: 'García' },
    schedule: { id: 2, nombreTurno: 'Mañana', horaEntrada: '07:00:00', horaSalida: '15:00:00' }
  }
];

const mockEmployees = [
  { id: 10, name: 'Ana', apellido: 'García' },
  { id: 20, nombre: 'Luis', apellido: 'Pérez' }
];

const mockSchedules = [
  { id: 2, nombreTurno: 'Mañana', horaEntrada: '07:00:00', horaSalida: '15:00:00' }
];

describe('Pruebas en el componente <Shifts />', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    localStorage.clear();
    vi.spyOn(window, 'alert').mockImplementation(() => {});
    vi.spyOn(window, 'confirm').mockImplementation(() => true);

    vi.useFakeTimers({ toFake: ['Date'] });
    vi.setSystemTime(new Date('2026-05-24T12:00:00'));

    vi.stubGlobal('fetch', vi.fn().mockImplementation((url) => {
      if (url.includes('/api/shifts')) return Promise.resolve({ json: () => Promise.resolve(mockShifts) });
      if (url.includes('/api/employees')) return Promise.resolve({ json: () => Promise.resolve(mockEmployees) });
      if (url.includes('/api/schedules')) return Promise.resolve({ json: () => Promise.resolve(mockSchedules) });
      return Promise.resolve({ json: () => Promise.resolve([]) });
    }));
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  test('Debe cargar y renderizar los turnos, empleados y horarios del día seleccionado', async () => {
    const { container } = render(<Shifts token="mock_token" />);

    expect(screen.getByRole('heading', { name: 'Gestión de Turnos' })).toBeInTheDocument();

    await screen.findByRole('button', { name: /asignar/i });

    const empleadoTurno = container.querySelector('.shift-info strong');
    expect(empleadoTurno).toHaveTextContent('Ana García');
    
    expect(screen.getByText('07:00 - 15:00')).toBeInTheDocument();
    expect(screen.getByText('Cubrir recepción principal')).toBeInTheDocument();
  });

  test('Debe enviar el payload correcto al asignar un nuevo turno con éxito', async () => {
    const mockFetch = vi.fn()
      .mockResolvedValueOnce({ json: () => Promise.resolve(mockShifts) })    
      .mockResolvedValueOnce({ json: () => Promise.resolve(mockEmployees) }) 
      .mockResolvedValueOnce({ json: () => Promise.resolve(mockSchedules) }) 
      .mockResolvedValueOnce({ ok: true })                                   
      .mockResolvedValue({ json: () => Promise.resolve([]) });               
    vi.stubGlobal('fetch', mockFetch);

    const { container } = render(<Shifts token="mock_token" />);

    await screen.findByRole('button', { name: /asignar/i });

    const inputs = container.querySelectorAll('.input-group');
    let selectEmpleado, selectTurno, inputFecha, inputNotas;

    inputs.forEach(group => {
      const labelText = group.querySelector('label')?.textContent;
      if (labelText?.includes('Empleado')) selectEmpleado = group.querySelector('select');
      if (labelText?.includes('Turno')) selectTurno = group.querySelector('select');
      if (labelText?.includes('Fecha')) inputFecha = group.querySelector('input');
      if (labelText?.includes('Notas')) inputNotas = group.querySelector('input');
    });

    fireEvent.change(selectEmpleado, { target: { value: '20' } });
    fireEvent.change(selectTurno, { target: { value: '2' } });
    fireEvent.change(inputFecha, { target: { value: '2026-05-25' } });
    fireEvent.change(inputNotas, { target: { value: 'Refuerzo de cocina' } });

    const btnAsignar = screen.getByRole('button', { name: /asignar/i });
    fireEvent.click(btnAsignar);

    await waitFor(() => {
      expect(mockFetch).toHaveBeenCalledWith('http://localhost:8080/api/shifts', expect.objectContaining({
        method: 'POST',
        body: JSON.stringify({
          fecha: '2026-05-25T00:00:00',
          employee: { id: 20 },
          schedule: { id: 2 },
          observaciones: 'Refuerzo de cocina'
         })
      }));
      expect(window.alert).toHaveBeenCalledWith("Turno asignado con éxito");
    });
  });

  test('Debe llamar al endpoint de eliminación si el usuario confirma la acción', async () => {
    const mockFetch = vi.fn()
      .mockResolvedValueOnce({ json: () => Promise.resolve(mockShifts) })
      .mockResolvedValueOnce({ json: () => Promise.resolve(mockEmployees) })
      .mockResolvedValueOnce({ json: () => Promise.resolve(mockSchedules) })
      .mockResolvedValueOnce({ ok: true })
      .mockResolvedValue({ json: () => Promise.resolve([]) });
    vi.stubGlobal('fetch', mockFetch);

    render(<Shifts token="mock_token" />);

    const btnEliminar = await screen.findByRole('button', { name: /eliminar/i });
    fireEvent.click(btnEliminar);

    expect(window.confirm).toHaveBeenCalledWith('¿Eliminar este turno?');
    
    await waitFor(() => {
      expect(mockFetch).toHaveBeenCalledWith('http://localhost:8080/api/shifts/1', expect.objectContaining({
        method: 'DELETE'
      }));
    });
  });
});