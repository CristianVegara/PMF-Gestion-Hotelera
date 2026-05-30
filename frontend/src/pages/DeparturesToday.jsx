import { useState, useEffect, useCallback, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import "./Clients.css";

const DeparturesToday = ({ date }) => {
  const [bookings, setBookings] = useState([]);
  const [sortBy, setSortBy] = useState("checkInStatus");
  const [direction, setDirection] = useState("asc");
  const [guestSearch, setGuestSearch] = useState("");

  const navigate = useNavigate();

  const fetchBookings = useCallback(async () => {
    try {
      const token = localStorage.getItem("user_token");
      const response = await fetch(
        `http://localhost:8080/api/bookings/date?date=${date}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        },
      );
      const data = await response.json();
      setBookings(
        Array.isArray(data)
          ? data.filter(
              (b) => b.fechaSalida === date && b.checkInStatus === "DENTRO",
            )
          : [],
      );
    } catch (error) {
      console.error("Error al cargar las salidas:", error);
      setBookings([]);
    }
  }, [date]);

  useEffect(() => {
    let active = true;

    const loadData = async () => {
      if (active) {
        await fetchBookings();
      }
    };

    loadData();

    return () => {
      active = false;
    };
  }, [fetchBookings]);

  const rawRole = localStorage.getItem("role") || "";
  const userRole = rawRole.startsWith("ROLE_")
    ? rawRole.replace("ROLE_", "")
    : rawRole;

  const filteredBookings = useMemo(() => {
    const filtered = bookings.filter(
      (b) =>
        b.cliente?.nombre
          ?.toLowerCase()
          .includes(guestSearch.trim().toLowerCase()) ||
        b.cliente?.dni
          ?.toLowerCase()
          .includes(guestSearch.trim().toLowerCase()),
    );
    return [...filtered].sort((a, b) => {
      let aVal, bVal;
      switch (sortBy) {
        case "habitacion":
          aVal = a.habitacion?.number ?? 0;
          bVal = b.habitacion?.number ?? 0;
          break;
        case "nombre":
          aVal = a.cliente?.nombre ?? "";
          bVal = b.cliente?.nombre ?? "";
          break;
        default:
          aVal = a.checkInStatus;
          bVal = b.checkInStatus;
      }
      if (aVal < bVal) return direction === "asc" ? -1 : 1;
      if (aVal > bVal) return direction === "asc" ? 1 : -1;
      return 0;
    });
  }, [bookings, guestSearch, sortBy, direction]);

  return (
    <div className="clients-page-wrapper">
      <div className="clients-container">
        <div className="header-actions">
          <div>
            <h2 className="title-list">Listado de Salidas</h2>
            <p
              style={{
                color: "#6c757d",
                margin: "4px 0 0",
                fontSize: "0.95rem",
              }}
            >
              Fecha:{" "}
              {new Date(date).toLocaleDateString("es-ES", {
                weekday: "long",
                year: "numeric",
                month: "long",
                day: "numeric",
              })}
            </p>
          </div>
        </div>

        <div className="sort-controls">
          <label className="dni-search-label">
            Buscar por Huésped / DNI:
            <input
              type="search"
              value={guestSearch}
              onChange={(e) => setGuestSearch(e.target.value)}
              placeholder="Ej: Juan Pérez / 12345678A"
            />
          </label>
          <label>
            Ordenar por:
            <select value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
              <option value="checkInStatus">Estado</option>
              <option value="habitacion">Habitación</option>
              <option value="nombre">Huésped</option>
            </select>
          </label>
          <label>
            Dirección:
            <select
              value={direction}
              onChange={(e) => setDirection(e.target.value)}
            >
              <option value="asc">Ascendente</option>
              <option value="desc">Descendente</option>
            </select>
          </label>
        </div>

        <div className="table-responsive">
          <table className="modern-table">
            <thead>
              <tr>
                <th>Hab.</th>
                <th>Huésped</th>
                <th className="text-center">Estado</th>
                <th className="text-center">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {filteredBookings.length === 0 ? (
                <tr>
                  <td colSpan="4" className="text-center empty-table-message">
                    No hay salidas para esta fecha.
                  </td>
                </tr>
              ) : (
                filteredBookings.map((b) => (
                  <tr
                    key={b.id}
                    onClick={() => navigate(`/bookings/${b.id}`)}
                    className="clickable-row"
                    title="Ver detalles de la reserva"
                  >
                    <td className="bold">#{b.habitacion?.number}</td>
                    <td>{b.cliente?.nombre}</td>
                    <td className="text-center">
                      <span
                        className={`badge ${b.checkInStatus === 2 ? "status-cancelada" : "status-dentro"}`}
                      >
                        {b.checkInStatus}
                      </span>
                    </td>
                    <td className="text-center">
                      <div className="action-group">
                        <button
                          onClick={(e) => {
                            e.stopPropagation();
                            navigate(`/bookings/${b.id}`);
                          }}
                          className="btn-edit"
                        >
                          Ver
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default DeparturesToday;
