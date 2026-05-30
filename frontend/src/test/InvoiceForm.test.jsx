import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, test, expect, vi, beforeEach } from 'vitest';
import InvoiceForm from '../pages/InvoiceForm.jsx';

const mockNavigate = vi.fn();
let mockParams = {};

vi.mock('react-router-dom', () => ({
  useParams: () => mockParams,
  useNavigate: () => mockNavigate
}));

const mockClients = [
  { id: '1', nombre: 'Juan Pérez', dni: '11111111A', bookings: [] },
  { 
    id: '2', 
    nombre: 'Marta Gómez', 
    dni: '22222222B', 
    bookings: new Array(15).fill({ fechaEntrada: '2026-01-10' })
  }
];

describe('Pruebas en InvoiceForm', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    localStorage.clear();
    localStorage.setItem('user_token', 'form_token');
    mockParams = {}; 
    vi.spyOn(window, 'alert').mockImplementation(() => {});

    vi.stubGlobal('fetch', vi.fn().mockImplementation((url) => {
      if (url.includes('/api/clients')) {
        return Promise.resolve({ json: () => Promise.resolve(mockClients), ok: true });
      }
      return Promise.resolve({ 
        ok: true, 
        json: () => Promise.resolve({ mensaje: 'Factura registrada con éxito' }) 
      });
    }));
  });

  test('Calcular subtotales y totales reactivamente al cambiar noches y precio', async () => {
    render(<InvoiceForm />);

    expect(await screen.findByText('Juan Pérez (11111111A)')).toBeInTheDocument();

    const inputNoches = screen.getByLabelText('Nº de noches');
    const inputPrecio = screen.getByLabelText('Precio por noche (€)');

    fireEvent.change(inputNoches, { target: { value: '3' } });
    fireEvent.change(inputPrecio, { target: { value: '100' } });

    expect(screen.getByTestId('subtotal-val').textContent).toBe('300.00 €');
    expect(screen.getByText(/Rango fidelidad:/i)).toHaveTextContent('Sin rango');
    expect(screen.getByTestId('total-val').textContent).toBe('330.00 €');
  });

  test('Aplicar el descuento VIP correspondiente al cambiar de cliente', async () => {
    render(<InvoiceForm />);
    
    const selectCliente = await screen.findByLabelText('Cliente');
    const inputNoches = screen.getByLabelText('Nº de noches');
    const inputPrecio = screen.getByLabelText('Precio por noche (€)');

    fireEvent.change(inputNoches, { target: { value: '2' } });
    fireEvent.change(inputPrecio, { target: { value: '100' } }); 

    fireEvent.change(selectCliente, { target: { value: '2' } });

    expect(screen.getByText(/Rango fidelidad:/i)).toHaveTextContent('Diamante');
    expect(screen.getByText(/Descuento \(20%\):/i)).toHaveTextContent('-40.00 €');
    expect(screen.getByTestId('total-val').textContent).toBe('176.00 €');
  });

  test('Enviar el payload correcto al backend al enviar el formulario (Creación)', async () => {
      render(<InvoiceForm />);
      
      const selectCliente = await screen.findByLabelText('Cliente');
      
      fireEvent.change(selectCliente, { target: { value: '1' } });
      fireEvent.change(screen.getByLabelText('Concepto'), { target: { value: 'Hospedaje Suite Ejecutiva' } });
      fireEvent.change(screen.getByLabelText('Nº de noches'), { target: { value: '1' } });
      fireEvent.change(screen.getByLabelText('Precio por noche (€)'), { target: { value: '100' } });

      const btnSubmit = screen.getByRole('button', { name: 'Crear Factura' });
      fireEvent.click(btnSubmit);

      await waitFor(() => {
        expect(window.fetch).toHaveBeenCalledWith('/api/invoice', expect.objectContaining({
          method: 'POST',
          body: expect.stringContaining('"concepto":"Hospedaje Suite Ejecutiva"')
        }));
        expect(window.alert).toHaveBeenCalledWith('Factura registrada con éxito');
        expect(mockNavigate).toHaveBeenCalledWith('/invoice');
      });
    });
});