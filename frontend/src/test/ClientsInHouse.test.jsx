import { render, screen, fireEvent } from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";
import { describe, test, expect, vi, beforeEach } from "vitest";
import ClientsInHouse from "../pages/ClientsInHouse";

const mockBookingsData = [
  {
    id: 101,
    checkInStatus: "DENTRO",
    fechaEntrada: new Date().toISOString().split("T")[0],
    fechaSalida: new Date().toISOString().split("T")[0],
    cliente: { nombre: "Carlos Mendoza", dni: "11122233X" },
    habitacion: { number: "102", type: "Doble" },
  },
  {
    id: 102,
    checkInStatus: "PENDIENTE",
    fechaEntrada: new Date().toISOString().split("T")[0],
    fechaSalida: new Date().toISOString().split("T")[0],
    cliente: { nombre: "María Silva", dni: "44455566Y" },
    habitacion: { number: "204", type: "Suite" },
  },
];

describe("Pruebas en el componente ClientsInHouse", () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    localStorage.clear();
  });

  test("Mostrar la lista de huéspedes en casa y las estadísticas correctamente", async () => {
    const mockFetch = vi.fn().mockResolvedValueOnce({
      json: () => Promise.resolve(mockBookingsData),
    });
    vi.stubGlobal("fetch", mockFetch);

    render(
      <BrowserRouter>
        <ClientsInHouse />
      </BrowserRouter>,
    );

    expect(screen.getByText("Cargando...")).toBeInTheDocument();

    expect(await screen.findByText("Carlos Mendoza")).toBeInTheDocument();
    expect(screen.getByText("#102")).toBeInTheDocument();
    expect(screen.getByText("11122233X")).toBeInTheDocument();

    expect(screen.queryByText("María Silva")).not.toBeInTheDocument();

    const statTotal = screen.getByText("En Casa");
    expect(statTotal.textContent).toContain("1");

    const statCheckIns = screen.getByText("Entradas Hoy");
    expect(statCheckIns.textContent).toContain("1");
  });

  test("Filtrar la tabla cuando se busca por nombre de huésped", async () => {
    const mockFetch = vi.fn().mockResolvedValueOnce({
      json: () => Promise.resolve(mockBookingsData),
    });
    vi.stubGlobal("fetch", mockFetch);

    render(
      <BrowserRouter>
        <ClientsInHouse />
      </BrowserRouter>,
    );

    expect(await screen.findByText("Carlos Mendoza")).toBeInTheDocument();

    const searchInput = screen.getByPlaceholderText("Ej: Carlos Mendoza o 102");
    fireEvent.change(searchInput, { target: { value: "Inexistente" } });

    expect(screen.queryByText("Carlos Mendoza")).not.toBeInTheDocument();
    expect(
      screen.getByText('No hay huéspedes "Dentro" registrados para hoy.'),
    ).toBeInTheDocument();
  });

  test("Manejar una lista vacía de huéspedes de manera correcta", async () => {
    const mockFetch = vi.fn().mockResolvedValueOnce({
      json: () => Promise.resolve([]),
    });
    vi.stubGlobal("fetch", mockFetch);

    render(
      <BrowserRouter>
        <ClientsInHouse />
      </BrowserRouter>,
    );

    expect(
      await screen.findByText(
        'No hay huéspedes "Dentro" registrados para hoy.',
      ),
    ).toBeInTheDocument();
  });
});
