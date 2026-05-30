import { useState, useEffect, useCallback, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import "./ArrivalsToday.css";

const ArrivalsToday = ({ date }) => {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [guestSearch, setGuestSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("CONFIRMADA");

  const navigate = useNavigate();

  const fetchBookings = useCallback(async () => {
    try {
      setLoading(true);
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
        Array.isArray(data) ? data.filter((b) => b.fechaEntrada === date) : [],
      );
    } catch (error) {
      console.error("Error al cargar las llegadas:", error);
      setBookings([]);
    } finally {
      setLoading(false);
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
    const confirmadas = bookings.filter((b) => b.estado === "CONFIRMADA");
    const sorted = [...confirmadas].sort(
      (a, b) => a.checkInStatus - b.checkInStatus,
    );
    return sorted.filter((b) =>
      b.cliente?.nombre
        ?.toLowerCase()
        .includes(guestSearch.trim().toLowerCase()),
    );
  }, [bookings, guestSearch]);

  const formatDate = (dateStr) => {
    return new Date(dateStr).toLocaleDateString("es-ES", {
      weekday: "long",
      year: "numeric",
      month: "long",
      day: "numeric",
    });
  };

  return (
    <div className="clients-page-wrapper">
      <div className="clients-container">
        <div className="header-actions">
          <div>
            <h2 className="title-list">Listado de Entradas</h2>
            <p type="text" className="date-display" >
              {formatDate(date)}
            </p>
          </div>
        </div>

        <div className="sort-controls">
          <label className="guest-search-label">
            Buscar por Huésped:
            <input
              type="search"
              value={guestSearch}
              onChange={(e) => setGuestSearch(e.target.value)}
              placeholder="Ej: Juan Pérez"
            />
          </label>
          <label>
            Estado:
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
            >
              <option value="CONFIRMADA">Confirmada</option>
              <option value="PENDIENTE">Pendiente</option>
            </select>
          </label>
        </div>

        <div className="results-counter">
          {filteredBookings.length}{" "}
          {filteredBookings.length === 1 ? "llegada" : "llegadas"} encontradas
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
              {loading ? (
                <tr>
                  <td colSpan="4" className="text-center empty-table-message">
                    Cargando...
                  </td>
                </tr>
              ) : filteredBookings.length === 0 ? (
                <tr>
                  <td colSpan="4" className="text-center empty-table-message">
                    No hay llegadas para esta fecha
                  </td>
                </tr>
              ) : (
                filteredBookings.map((b) => (
                  <tr key={b.id}>
                    <td className="bold">#{b.habitacion?.number}</td>
                    <td>{b.cliente?.nombre}</td>
                    <td className="text-center">
                      <span
                        className={`badge ${b.checkInStatus === 0 ? "confirmada" : "pendiente"}`}
                      >
                        {b.checkInStatus === 0 ? "Confirmada" : "Pendiente"}
                      </span>
                    </td>
                    <td className="text-center">
                      <div className="action-group">
                        <button
                          className="btn-edit"
                          onClick={() => navigate(`/bookings/${b.id}`)}
                        >
                          Booking
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

export default ArrivalsToday;
