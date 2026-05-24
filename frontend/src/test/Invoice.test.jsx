import { render, screen, fireEvent } from '@testing-library/react';
import { describe, test, expect, vi, beforeEach } from 'vitest';
import Invoice from '../pages/Invoice.jsx';

vi.mock('react-router-dom', () => ({
  Link: ({ to, children }) => <a href={to}>{children}</a>
}));

const mockInvoices = [
  {
    id: 1001,
    concepto: 'Estancia Habitación Familiar',
    noches: 3,
    precio: 100,
    discountAmount: 30,
    discountPercentage: 10,
    total: 297,
    cliente: { nombre: 'Lucía Fernández', dni: '12345678A' }
  }
];

const mockReportResponse = {
  totalGross: 550,
  totalNet: 500,
  totalTax: 50,
  breakdown: [
    { type: 'Hospedaje', amount: 440, netAmount: 400, tax: 40 },
    { type: 'Restaurante', amount: 110, netAmount: 100, tax: 10 }
  ]
};

describe('Pruebas en el Sistema de Facturación Invoice', () => {
  let mockOpenWindow;

  beforeEach(() => {
    vi.restoreAllMocks();
    localStorage.clear();
    localStorage.setItem('user_token', 'invoice_token');
    localStorage.setItem('role', 'ROLE_ADMIN'); 

    vi.spyOn(window, 'confirm').mockImplementation(() => true);
    
    mockOpenWindow = {
      document: {
        write: vi.fn(),
        close: vi.fn()
      }
    };
    vi.spyOn(window, 'open').mockImplementation(() => mockOpenWindow);

    vi.stubGlobal('fetch', vi.fn().mockImplementation((url) => {
      if (url.includes('/api/invoice/report')) {
        return Promise.resolve({ json: () => Promise.resolve(mockReportResponse), ok: true });
      }
      return Promise.resolve({ json: () => Promise.resolve(mockInvoices), ok: true });
    }));
  });

  test('Cargar y listar las facturas con sus desgloses en la tabla principal', async () => {
    render(<Invoice />);

    expect(screen.getByRole('heading', { name: 'Listado de Facturas' })).toBeInTheDocument();
    expect(await screen.findByText('Lucía Fernández')).toBeInTheDocument();
    expect(screen.getByText('Estancia Habitación Familiar')).toBeInTheDocument();
    
    expect(screen.getByRole('link', { name: 'Nueva Factura' })).toBeInTheDocument();
  });

  test('Llamar al endpoint de borrado al confirmar la eliminación de una factura', async () => {
      const mockFetch = vi.fn().mockImplementation((url, config) => {
        if (config && config.method === 'DELETE') {
          return Promise.resolve({
            ok: true,
            json: () => Promise.resolve({ mensaje: 'Eliminado correctamente' })
          });
        }
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve(mockInvoices)
        });
      });
      vi.stubGlobal('fetch', mockFetch);

      render(<Invoice />);
      
      const btnEliminar = await screen.findByRole('button', { name: 'Eliminar' });
      fireEvent.click(btnEliminar);

      expect(window.confirm).toHaveBeenCalledWith('¿Eliminar factura?');
      expect(mockFetch).toHaveBeenCalledWith('/api/invoice/1001', expect.objectContaining({
        method: 'DELETE'
      }));
    });

  test('Generar un informe de facturación y permitir su impresión en una ventana emergente', async () => {
    render(<Invoice />);

    fireEvent.change(screen.getAllByLabelText(/Desde/i)[0], { target: { value: '2026-05-01' } });
    fireEvent.change(screen.getAllByLabelText(/Hasta/i)[0], { target: { value: '2026-05-30' } });

    const btnGenerar = screen.getByRole('button', { name: 'Generar informe' });
    fireEvent.click(btnGenerar);

    expect(await screen.findByText('Total bruto:')).toBeInTheDocument();
    expect(screen.getByText('550.00 €')).toBeInTheDocument();
    expect(screen.getByText('500.00 €')).toBeInTheDocument(); 
    expect(screen.getByText('Hospedaje')).toBeInTheDocument();  

    const btnImprimirPDF = screen.getByRole('button', { name: 'Imprimir PDF' });
    fireEvent.click(btnImprimirPDF);

    expect(window.open).toHaveBeenCalled();
    expect(mockOpenWindow.document.write).toHaveBeenCalled(
      expect.stringContaining('Gestión Hotelera - Informes de Ingresos')
    );
  });
});
