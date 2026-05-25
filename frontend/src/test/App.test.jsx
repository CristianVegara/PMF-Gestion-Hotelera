import { render, screen, act } from '@testing-library/react';
import { beforeEach, describe, expect, test, vi } from 'vitest';
import App from '../App';

vi.mock('../components/Navbar', () => ({ default: () => <div data-testid="navbar">Navbar</div> }));
vi.mock('../components/ProtectedLayout', () => ({ default: () => <div data-testid="protected">Protected</div> }));
vi.mock('../pages/Login', () => ({ default: () => <div data-testid="login">Login Page</div> }));
vi.mock('../pages/Home', () => ({ default: () => <div>Home</div> }));
vi.mock('../pages/Bookings', () => ({ default: () => <div>Bookings</div> }));
vi.mock('../pages/BookingDetails', () => ({ default: () => <div>Details</div> }));
vi.mock('../pages/BookingForm', () => ({ default: () => <div>Form</div> }));
vi.mock('../pages/ClientsManager', () => ({ default: () => <div>Clients</div> }));
vi.mock('../pages/ClientForm', () => ({ default: () => <div>ClientForm</div> }));
vi.mock('../pages/ClientDetails', () => ({ default: () => <div>ClientDetails</div> }));
vi.mock('../pages/Invoice', () => ({ default: () => <div>Invoice</div> }));
vi.mock('../pages/InvoiceForm', () => ({ default: () => <div>InvoiceForm</div> }));
vi.mock('../pages/NotFound', () => ({ default: () => <div>NotFound</div> }));
vi.mock('../pages/Rooms', () => ({ default: () => <div>Rooms</div> }));
vi.mock('../pages/Activities', () => ({ default: () => <div>Activities</div> }));
vi.mock('../pages/Activity', () => ({ default: () => <div>Activity</div> }));
vi.mock('../pages/Shifts', () => ({ default: () => <div>Shifts</div> }));
vi.mock('../pages/RoomPriceType', () => ({ default: () => <div>PriceType</div> }));
vi.mock('../pages/RoomPriceAll', () => ({ default: () => <div>PriceAll</div> }));
vi.mock('../pages/RoomDetails', () => ({ default: () => <div>RoomDetails</div> }));

describe('Pruebas en App', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  test('Renderizar la aplicacion y mostrar login por defecto', async () => {
    await act(async () => {
      render(<App />);
    });

    const loginPage = await screen.findByTestId('login');
    expect(loginPage).toBeInTheDocument();
  });
});