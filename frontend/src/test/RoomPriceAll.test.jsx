import { render, screen, fireEvent } from '@testing-library/react';
import { describe, test, expect, vi, beforeEach } from 'vitest';
import RoomPriceAll from '../pages/RoomPriceAll.jsx';

vi.mock('recharts', async () => {
  const original = await vi.importActual('recharts');
  return {
    ...original,
    ResponsiveContainer: ({ children }) => <div>{children}</div>,
    LineChart: ({ data, children }) => (
      <div data-testid="mocked-line-chart">
        <span data-testid="processed-chart-data">{JSON.stringify(data)}</span>
        {children}
      </div>
    ),
    CartesianGrid: () => null,
    XAxis: () => null,
    YAxis: () => null,
    Tooltip: () => null,
    Legend: () => null,
    Line: () => null
  };
});

const mockAllTypesResponse = {
  labels: ['Enero', 'Febrero'],
  datasets: {
    INDIVIDUAL: [60, 70],
    DOBLE: [100, 110],
    SUITE: [200, 220]
  }
};

describe('Pruebas en el Dashboard RoomPriceAll', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    localStorage.clear();
    localStorage.setItem('user_token', 'dashboard_token');

    vi.stubGlobal('fetch', vi.fn().mockImplementation((url) => {
      if (url.includes('/api/rooms/dynamic/chart/all-types')) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve(mockAllTypesResponse)
        });
      }
      return Promise.resolve({ ok: false });
    }));
  });

  test('Procesar las series del backend y calcular los promedios', async () => {
    render(<RoomPriceAll />);

    expect(screen.getByText('Cargando análisis dinámico...')).toBeInTheDocument();

    expect(await screen.findByRole('heading', { name: 'Análisis Comparativo de Precios' })).toBeInTheDocument();

    expect(screen.getByLabelText('INDIVIDUAL')).toBeChecked();
    expect(screen.getByLabelText('DOBLE')).toBeChecked();
    expect(screen.getByLabelText('SUITE')).toBeChecked();
    expect(screen.getByLabelText('PROMEDIO')).toBeChecked();

    const rawDataElement = screen.getByTestId('processed-chart-data');
    const chartData = JSON.parse(rawDataElement.textContent);

    expect(chartData[0]).toEqual({
      mes: 'Enero',
      INDIVIDUAL: 60,
      DOBLE: 100,
      SUITE: 200,
      MEDIA: 120
    });
  });

  test('Recalcular el promedio dinámicamente al desactivar una serie de la gráfica', async () => {
    render(<RoomPriceAll />);
    
    await screen.findByRole('heading', { name: 'Análisis Comparativo de Precios' });

    const checkboxSuite = screen.getByLabelText('SUITE');
    fireEvent.click(checkboxSuite);
    expect(checkboxSuite).not.toBeChecked();

    const rawDataElement = screen.getByTestId('processed-chart-data');
    const updatedChartData = JSON.parse(rawDataElement.textContent);

    expect(updatedChartData[0].MEDIA).toBe(80); 
    expect(updatedChartData[1].MEDIA).toBe(90); 
  });
});