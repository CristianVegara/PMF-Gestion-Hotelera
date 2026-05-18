import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Rooms.css';

const formatEnum = (text) => {
  if (!text) return "";
  return text.charAt(0) + text.slice(1).toLowerCase().replace(/_/g, ' ');
};

const enumToClass = (text) => {
  return text ? text.toLowerCase().replace(/_/g, '-') : '';
};

const Rooms = () => {
  const [rooms, setRooms] = useState([]);
  const [viewMode, setViewMode] = useState('list');
  const [roomSelected, setRoomSelected] = useState(null);
  const [roomBookings, setRoomBookings] = useState([]);
  const [clients, setClients] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [clientSelected, setClientSelected] = useState(null);
  const [showSearch, setShowSearch] = useState(false);
  const [fechaEntrada, setFechaEntrada] = useState('');
  const [fechaSalida, setFechaSalida] = useState('');
  const [bookingDetail, setBookingDetail] = useState(null);
  const [showHistory, setShowHistory] = useState(false);
  const navigate = useNavigate();

  const fetchRooms = () => {
    fetch('http://localhost:8080/api/rooms')
      .then(res => res.json())
      .then(data => setRooms(data))
      .catch(err => console.error("Error cargando habitaciones:", err));
  };

  const fetchRoomBookings = (roomId) => {
    if (!roomId) return;
    fetch(`http://localhost:8080/api/bookings/room/${roomId}`)
      .then(res => res.json())
      .then(data => {
        const sorted = data.sort((a, b) => new Date(b.fechaEntrada) - new Date(a.fechaEntrada));
        setRoomBookings(sorted);
      })
      .catch(err => console.error("Error cargando agenda de la habitación:", err));
  };

  useEffect(() => {
    fetchRooms();
  }, []);

  useEffect(() => {
    if (roomSelected) {
      fetchRoomBookings(roomSelected.id);
    }
  }, [roomSelected]);

  const handleStartAssignment = () => {
    fetch('/api/clients')
      .then(res => res.json())
      .then(data => {
        setClients(data);
        setShowSearch(true);
      });
  };

  const filteredClients = clients.filter(c =>
    (c.nombre && c.nombre.toLowerCase().includes(searchTerm.toLowerCase())) ||
    (c.dni && c.dni.toLowerCase().includes(searchTerm.toLowerCase()))
  ).slice(0, 10);

  const handleSelectClient = (client) => {
    setClientSelected(client);
    setShowSearch(false);
    setSearchTerm("");
  };

  const handleConfirmRegistro = () => {
    if (!fechaEntrada || !fechaSalida) {
      alert("Por favor, selecciona las fechas.");
      return;
    }
    const bookingData = {
      fechaEntrada, fechaSalida, estado: "CONFIRMADA",
      cliente: { id: clientSelected.id },
      habitacion: { id: roomSelected.id }
    };
    fetch('/api/bookings', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(bookingData)
    }).then(res => {
      if (res.ok) {
        alert("¡Reserva creada!");
        setClientSelected(null);
        setRoomSelected(null);
        fetchRooms();
      } else if (res.status === 409) {
        alert("❌ Conflicto de fechas.");
      }
    });
  };

  const handleUpdateBooking = () => {
    fetch(`/api/bookings/${bookingDetail.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(bookingDetail)
    })
      .then(res => {
        if (res.ok) {
          alert("Reserva actualizada correctamente.");
          setBookingDetail(null);
          fetchRoomBookings(roomSelected.id);
          fetchRooms();
        }
      });
  };

  const agendaFutura = roomBookings.filter(b => new Date(b.fechaSalida) >= new Date().setHours(0, 0, 0, 0));

  return (
    <div className="rooms-page-wrapper">
      <div className="rooms-container">
        <div className="main-content-column">
          <div className="header-actions">
            <h2 className="title-list">Estado de Habitaciones ({rooms.length})</h2>
          </div>

          <div className="table-responsive scrollable-table">
            {viewMode === 'list' ? (
              <table className="rooms-table">
                <thead>
                  <tr><th>ID</th><th>Número</th><th>Tipo</th><th>Estado</th></tr>
                </thead>
                <tbody>
                  {rooms.map(room => (
                    <tr
                      key={room.id}
                      onClick={() => setRoomSelected(room)}
                      className={roomSelected?.id === room.id ? 'row-selected' : ''}
                    >
                      <td>{room.id}</td>
                      <td>{room.number}</td>
                      <td>{formatEnum(room.type)}</td>
                      <td>
                        <span className={`status-pill ${enumToClass(room.status)}`}>
                          {formatEnum(room.status)}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              <div className="rooms-grid">
                {rooms.map(room => (
                  <div
                    key={room.id}
                    className={`room-card ${enumToClass(room.status)} ${roomSelected?.id === room.id ? 'selected' : ''}`}
                    onClick={() => setRoomSelected(room)}
                  >
                    <div className="room-card-number">{room.number}</div>
                    <div className="room-card-type">{formatEnum(room.type)}</div>
                    <div className="room-card-status">{formatEnum(room.status)}</div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        <div className="info-column">
          {roomSelected && (
            <div className="detail-panel sticky-panel">
              <div className="panel-header-with-action">
                <h3 className="panel-title">Habitación {roomSelected.number}</h3>
                <button className="btn-history-trigger" onClick={() => setShowHistory(true)}>📜 Historial</button>
              </div>

              <div className="detail-info-basic">
                <p><strong>Tipo:</strong> {formatEnum(roomSelected.type)} | <strong>Precio:</strong> {roomSelected.price?.toFixed(2)}€</p>
                <p><strong>Estado:</strong> <span className={`status-text ${enumToClass(roomSelected.status)}`}>{formatEnum(roomSelected.status)}</span></p>
              </div>

              <hr className="divider" />

              <div className="room-agenda">
                <p className="agenda-title">📅 Próximas Reservas:</p>
                {agendaFutura.length > 0 ? (
                  <ul className="agenda-list">
                    {agendaFutura.slice(0, 5).map(b => (
                      <li key={b.id} className="agenda-item clickable-booking" onClick={() => setBookingDetail(b)}>
                        <div className="agenda-dates">🗓️ <strong>{b.fechaEntrada}</strong> al <strong>{b.fechaSalida}</strong></div>
                        <div className="agenda-client">👤 <span className="client-name-small">{b.cliente?.nombre}</span></div>
                      </li>
                    ))}
                  </ul>
                ) : <p className="no-bookings">Sin reservas próximas</p>}
              </div>

              <div className="assignment-area">
                {!showSearch && !clientSelected && (
                  <button className="btn-action" onClick={handleStartAssignment}>Asignar / Nueva Reserva</button>
                )}
                {showSearch && (
                  <div className="inline-search-box">
                    <input type="text" placeholder="Buscar cliente..." className="search-input-inline" onChange={(e) => setSearchTerm(e.target.value)} autoFocus />
                    <div className="mini-client-list">
                      {filteredClients.map(c => (
                        <div key={c.id} className="client-option-item" onClick={() => handleSelectClient(c)}>{c.nombre}</div>
                      ))}
                    </div>
                  </div>
                )}
                {clientSelected && (
                  <div className="selected-client-box">
                    <p className="client-name-display">👤 {clientSelected.nombre}</p>
                    <div className="reservation-dates">
                      <div className="date-group"><label>Entrada</label><input type="date" className="date-input-field" onChange={(e) => setFechaEntrada(e.target.value)} /></div>
                      <div className="date-group"><label>Salida</label><input type="date" className="date-input-field" onChange={(e) => setFechaSalida(e.target.value)} /></div>
                    </div>
                    <button className="btn-confirm-final" onClick={handleConfirmRegistro}>Confirmar Registro</button>
                    <button className="btn-change-inline" onClick={() => setShowSearch(true)}>Cambiar Cliente</button>
                  </div>
                )}
              </div>
            </div>
          )}
        </div>
      </div>

      {showHistory && (
        <div className="modal-overlay" onClick={() => setShowHistory(false)}>
          <div className="modal-content modal-large" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Historial de Habitación {roomSelected?.number}</h3>
              <button className="close-btn" onClick={() => setShowHistory(false)}>&times;</button>
            </div>
            <div className="modal-body scrollable-modal-body">
              <table className="history-table">
                <thead>
                  <tr><th>Cliente</th><th>Entrada</th><th>Salida</th><th>Estado</th></tr>
                </thead>
                <tbody>
                  {roomBookings.map(b => (
                    <tr key={b.id}>
                      <td><strong>{b.cliente?.nombre}</strong><br/><small>{b.cliente?.dni}</small></td>
                      <td>{b.fechaEntrada}</td><td>{b.fechaSalida}</td>
                      <td><span className="status-pill">{b.estado}</span></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            <div className="modal-footer">
              <button className="btn-secondary" onClick={() => setShowHistory(false)}>Cerrar</button>
            </div>
          </div>
        </div>
      )}

      {bookingDetail && (
        <div className="modal-overlay" onClick={() => setBookingDetail(null)}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Reserva #{bookingDetail.id} - Detalles y Edición</h3>
              <button className="close-btn" onClick={() => setBookingDetail(null)}>&times;</button>
            </div>
            <div className="modal-body">
              <div className="info-section">
                <h4>🗓️ Modificar Periodo</h4>
                <div className="edit-date-group">
                  <label>Fecha Entrada:</label>
                  <input type="date" className="date-input-field" value={bookingDetail.fechaEntrada} onChange={(e) => setBookingDetail({ ...bookingDetail, fechaEntrada: e.target.value })} />
                </div>
                <div className="edit-date-group">
                  <label>Fecha Salida:</label>
                  <input type="date" className="date-input-field" value={bookingDetail.fechaSalida} onChange={(e) => setBookingDetail({ ...bookingDetail, fechaSalida: e.target.value })} />
                </div>
              </div>
              <hr className="divider" />
              <div className="info-section">
                <h4>👤 Información del Cliente</h4>
                <p><strong>Nombre completo:</strong> {bookingDetail.cliente?.nombre}</p>
                <p><strong>DNI/Documento:</strong> {bookingDetail.cliente?.dni}</p>
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn-confirm-final" onClick={handleUpdateBooking}>Guardar Cambios</button>
              <button className="btn-secondary" onClick={() => setBookingDetail(null)}>Cerrar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Rooms;