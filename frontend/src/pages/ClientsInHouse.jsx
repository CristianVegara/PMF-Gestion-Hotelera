import { useState, useEffect, useCallback, useMemo } from "react";
import { Link, useNavigate } from "react-router-dom";
import "./Clients.css";

const formatDate = (value) => {
  if (!value) return "---";
  const [year, month, day] = value.split("-").map(Number);
  return new Date(year, month - 1, day).toLocaleDateString("es-ES");
};

const ClientsInHouse = () => {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");

  const navigate = useNavigate();

  const todayStr = useMemo(() => {
    const local = new Date();
    const offset = local.getTimezoneOffset();
    const adjusted = new Date(local.getTime() - offset * 60 * 1000);
    return adjusted.toISOString().split("T")[0];
  }, []);

  const fetchInHouseBookings = useCallback(async () => {
    setLoading(true);
    try {
      const token = localStorage.getItem("user_token");
      const response = await fetch(
        `http://localhost:8080/api/bookings/date?date=${todayStr}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        },
      );
      const data = await response.json();
      setBookings(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("Error al cargar huéspedes:", err);
      setBookings([]);
    } finally {
      setLoading(false);
    }
  }, [todayStr]);

  useEffect(() => {
    let active = true;

    const loadData = async () => {
      if (active) {
        await fetchInHouseBookings();
      }
    };

    loadData();

    return () => {
      active = false;
    };
  }, [fetchInHouseBookings]);

  const rawRole = localStorage.getItem("role") || "";
  const userRole = rawRole.startsWith("ROLE_")
    ? rawRole.replace("ROLE_", "")
    : rawRole;

  const filteredBookings = useMemo(() => {
    return bookings
      .filter((book) => book.checkInStatus === "DENTRO")
      .filter((book) => {
        const search = searchTerm.toLowerCase();
        return (
          book.cliente?.nombre?.toLowerCase().includes(search) ||
          book.habitacion?.number?.toString().includes(search)
        );
      })
      .sort(
        (a, b) =>
          (parseInt(a.habitacion?.number) || 0) -
          (parseInt(b.habitacion?.number) || 0),
      );
  }, [bookings, searchTerm]);

  const stats = useMemo(() => {
    return {
      total: filteredBookings.length,
      checkIns: bookings.filter((b) => b.fechaEntrada === todayStr).length,
      checkOuts: bookings.filter((b) => b.fechaSalida === todayStr).length,
    };
  }, [bookings, filteredBookings, todayStr]);

  return (
    <div className="clients-page-wrapper">
      <div className="clients-container">
        <div className="header-actions">
          <h2 className="title-list">Listado de Huéspedes</h2>
          <button className="btn-print" onClick={() => window.print()}>
            Imprimir
          </button>
        </div>

        <div className="sort-controls">
          <label className="dni-search-label">
            Buscar por huésped o habitación:
            <input
              type="search"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="Ej: Carlos Mendoza o 102"
            />
          </label>
        </div>

        <div className="compact-stats">
          <span className="stat-chip">
            <strong>{stats.total}</strong> En Casa
          </span>
          <span className="stat-chip">
            <strong>{stats.checkIns}</strong> Entradas Hoy
          </span>
          <span className="stat-chip">
            <strong>{stats.checkOuts}</strong> Salidas Hoy
          </span>
        </div>

        <div className="table-responsive">
          <table className="modern-table">
            <thead>
              <tr>
                <th>Hab.</th>
                <th>Tipo</th>
                <th>Huésped</th>
                <th>Identificación</th>
                <th>Duración de la Estancia</th>
                <th className="text-center">Estado</th>
                <th className="text-center">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan="7" className="text-center">
                    Cargando...
                  </td>
                </tr>
              ) : filteredBookings.length === 0 ? (
                <tr>
                  <td colSpan="7" className="text-center">
                    No hay huéspedes "Dentro" registrados para hoy.
                  </td>
                </tr>
              ) : (
                filteredBookings.map((book) => (
                  <tr
                    key={book.id}
                    onClick={() => navigate(`/bookings/${book.id}`)}
                    className="clickable-row"
                    title="Ver detalles de la reserva"
                  >
                    <td>
                      <strong>#{book.habitacion?.number || "---"}</strong>
                    </td>
                    <td>
                      <span className="tag">
                        {book.habitacion?.type || book.roomType}
                      </span>
                    </td>
                    <td className="bold">{book.cliente?.nombre}</td>
                    <td>{book.cliente?.dni}</td>
                    <td className="small-date">
                      {formatDate(book.fechaEntrada)} -{" "}
                      {formatDate(book.fechaSalida)}
                    </td>
                    <td className="text-center">
                      <span
                        className={`badge ${book.checkInStatus.toLowerCase()}`}
                      >
                        {book.checkInStatus}
                      </span>
                    </td>
                    <td className="text-center">
                      <div className="action-group">
                        {userRole !== "USER" && (
                          <button
                            onClick={(e) => {
                              e.stopPropagation();
                              navigate(`/bookings/${book.id}`);
                            }}
                            className="btn-edit"
                          >
                            Ver
                          </button>
                        )}
                        {userRole === "ADMIN" && (
                          <button
                            onClick={(e) => {
                              e.stopPropagation();
                              navigate(`/bookings/edit/${book.id}`);
                            }}
                            className="btn-edit"
                            style={{
                              backgroundColor: "#17a2b8",
                            }}
                          >
                            Editar
                          </button>
                        )}
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

export default ClientsInHouse;
