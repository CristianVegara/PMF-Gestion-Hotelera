import { render, screen } from '@testing-library/react';
import { describe, test, expect, vi, beforeEach } from 'vitest';
import RoomPriceByType from '../pages/RoomPriceType.jsx';

vi.mock('react-router-dom', () => ({
  useParams: () => ({ type: 'deluxe' })
}));

vi.mock('recharts', async () => {
  const original = await vi.importActual('recharts');
  return {
    ...original,
    ResponsiveContainer: ({ children }) => <div>{children}</div>,
    AreaChart: ({ data, children }) => (
      <div data-testid="mocked-area-chart">
        <span data-testid="chart-data">{JSON.stringify(data)}</span>
        {children}
      </div>
    ),
    CartesianGrid: () => null,
    XAxis: () => null,
    YAxis: () => null,
    Tooltip: () => null,
    Legend: () => null,
    Area: () => null
  };
});

const mockChartResponse = {
  labels: ['Enero', 'Febrero', 'Marzo'],
  datasets: [100, 150, 130]
};

describe('Pruebas en RoomPriceByType', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    localStorage.clear();
    localStorage.setItem('user_token', 'mock_token');

    vi.stubGlobal('fetch', vi.fn().mockImplementation((url) => {
      if (url.includes('/api/dynamic/chart/type/DELUXE')) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve(mockChartResponse)
        });
      }
      return Promise.resolve({ ok: false });
    }));
  });

  test('Mostrar el estado de carga y luego procesar los datos para la gráfica', async () => {
    render(<RoomPriceByType />);

    expect(screen.getByText('Cargando historial de deluxe...')).toBeInTheDocument();

    expect(await screen.findByRole('heading', { name: 'Evolución de Precios: deluxe' })).toBeInTheDocument();

    expect(screen.queryByText('Cargando historial de deluxe...')).not.toBeInTheDocument();

    const chartContainer = screen.getByTestId('mocked-area-chart');
    expect(chartContainer).toBeInTheDocument();
 
    const dataElement = screen.getByTestId('chart-data');
    const parsedData = JSON.parse(dataElement.textContent);

    expect(parsedData).toEqual([
      { mes: 'Enero', valor: 100 },
      { mes: 'Febrero', valor: 150 },
      { mes: 'Marzo', valor: 130 }
    ]);
  });
});