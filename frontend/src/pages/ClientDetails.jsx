import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import './ClientDetails.css';

const formatDate = (value) => {
  if (!value) return '---';
  const datePart = value.split('T')[0];
  const [year, month, day] = datePart.split('-').map(Number);
  return new Date(year, month - 1, day).toLocaleDateString('es-ES');
};

const formatCurrency = (value) => `${Number(value || 0).toFixed(2)}€`;

const getStatusStyle = (estado) => {
  switch (estado) {
    case 'TERMINADA': return { color: '#6c757d', fontWeight: 'bold' };
    case 'CONFIRMADA': return { color: '#28a745', fontWeight: 'bold' };
    case 'PRÓXIMA': return { color: '#007bff', fontWeight: 'bold' };
    default: return { color: '#333', fontWeight: 'bold' };
  }
};

const ClientDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  
  const [client, setClient] = useState(null);
  const [loading, setLoading] = useState(true);
  
  const [hoverRange, setHoverRange] = useState({ start: null, end: null });
  const [selectedRange, setSelectedRange] = useState({ start: null, end: null });

  useEffect(() => {
    fetch(`http://localhost:8080/api/clients/${id}`)
      .then(res => res.json())
      .then(data => {
        setClient(data);
        
        const queryParams = new URLSearchParams(location.search);
        const highlightedId = queryParams.get('highlight');
        
        if (highlightedId && data.bookings) {
          const targetBooking = data.bookings.find(b => String(b.id) === highlightedId);
          if (targetBooking) {
            setSelectedRange({ 
                start: targetBooking.fechaEntrada, 
                end: targetBooking.fechaSalida 
            });
          }
        }
        
        setLoading(false);
      })
      .catch(err => {
        console.error("Error:", err);
        setLoading(false);
      });
  }, [id, location.search]);

  const activeRange = (selectedRange.start) ? selectedRange : hoverRange;

  const isDateInRange = (dateStr, range) => {
    if (!dateStr || !range.start || !range.end) return false;
    const d = new Date(dateStr.split('T')[0]).getTime();
    const start = new Date(range.start).getTime();
    const end = new Date(range.end).getTime();
    return d >= start && d <= end;
  };

  const handleBookingClick = (book) => {
    if (selectedRange.start === book.fechaEntrada && selectedRange.end === book.fechaSalida) {
      setSelectedRange({ start: null, end: null });
    } else {
      setSelectedRange({ start: book.fechaEntrada, end: book.fechaSalida });
    }
  };

  if (loading) return <div className="details-loading">Cargando...</div>;
  if (!client) return <div className="details-error">Cliente no encontrado.</div>;

  const activities = client.activities || [];
  const bookings = client.bookings || [];

  return (
    <div className="client-details-page">
      <div className="details-header">
        <button onClick={() => navigate('/bookings')} className="btn-back-link">
          ❮ Volver al listado
        </button>
      </div>

      <div className="details-section-card profile-main-card">
        <div className="profile-icon">👤</div>
        <div className="profile-data">
          <h1>{client.nombre}</h1>
          <div className="data-grid">
            <p><strong>DNI:</strong> {client.dni}</p>
            <p><strong>Teléfono:</strong> {client.telefono}</p>
            <p><strong>Email:</strong> {client.correo}</p>
          </div>
        </div>
      </div>

      <div className="details-stats-container">
        <div className="stat-box">
          <span className="stat-number">{bookings.length}</span>
          <span className="stat-label">Reservas</span>
        </div>
        <div className="stat-box active">
          <span className="stat-number">{bookings.filter(b => b.estado !== 'TERMINADA').length}</span>
          <span className="stat-label">Activas</span>
        </div>
        <div className="stat-box finished">
          <span className="stat-number">{bookings.filter(b => b.estado === 'TERMINADA').length}</span>
          <span className="stat-label">Terminadas</span>
        </div>
      </div>

      <div className="details-histories-grid">
        <div className="details-section-card history-panel">
          <h3>Historial de Reservas</h3>
          <div className="details-table-wrapper">
            <table className="details-mini-table">
              <thead>
                <tr>
                  <th>Hab.</th>
                  <th>Entrada</th>
                  <th>Salida</th>
                  <th>Estado</th>
                </tr>
              </thead>
              <tbody>
                {bookings.map(book => {
                  const isHighlighted = activeRange.start === book.fechaEntrada && activeRange.end === book.fechaSalida;
                  const isSelectedByClick = selectedRange.start === book.fechaEntrada && selectedRange.end === book.fechaSalida;
                  
                  return (
                    <tr 
                      key={book.id} 
                      className={`${isHighlighted ? 'row-highlighted' : ''} ${isSelectedByClick ? 'row-selected-persist' : ''} clickable-row`}
                      onMouseEnter={() => setHoverRange({ start: book.fechaEntrada, end: book.fechaSalida })}
                      onMouseLeave={() => setHoverRange({ start: null, end: null })}
                      onClick={() => handleBookingClick(book)}
                    >
                      <td>{book.habitacion?.number}</td>
                      <td>{formatDate(book.fechaEntrada)}</td>
                      <td>{formatDate(book.fechaSalida)}</td>
                      <td><span style={getStatusStyle(book.estado)}>{book.estado}</span></td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>

        <div className="details-section-card history-panel">
          <h3>Historial de Actividades</h3>
          <div className="details-table-wrapper">
            <table className="details-mini-table">
              <thead>
                <tr>
                  <th>Actividad</th>
                  <th>Comienzo</th>
                  <th>Fin</th>
                  <th>Precio</th>
                </tr>
              </thead>
              <tbody>
                {activities.map((act) => {
                  const actDate = act.fechaComienzo;
                  const isHighlighted = isDateInRange(actDate, activeRange);

                  return (
                    <tr 
                      key={act.id}
                      className={`clickable-row ${isHighlighted ? 'row-highlighted' : ''}`}
                      onMouseEnter={() => {
                        const parentBooking = bookings.find(b => isDateInRange(actDate, { start: b.fechaEntrada, end: b.fechaSalida }));
                        if (parentBooking) {
                          setHoverRange({ start: parentBooking.fechaEntrada, end: parentBooking.fechaSalida });
                        }
                      }}
                      onMouseLeave={() => setHoverRange({ start: null, end: null })}
                      onClick={() => navigate(`/activities/${act.id}`)}
                    >
                      <td>{act.descripcion}</td>
                      <td>{formatDate(act.fechaComienzo)}</td>
                      <td>{formatDate(act.fechaFin)}</td>
                      <td>{formatCurrency(act.precio)}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ClientDetails;