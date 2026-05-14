import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Clients.css';

const Clients = () => {
  const [clients, setClients] = useState([]);
  const [sortBy, setSortBy] = useState('id');
  const [direction, setDirection] = useState('asc');
  const [dniSearch, setDniSearch] = useState('');

  const [showModal, setShowModal] = useState(false);
  const [selectedClient, setSelectedClient] = useState(null);

  useEffect(() => {
    fetchClients();
  }, [sortBy, direction]);

  const fetchClients = async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/clients?sortBy=${sortBy}&direction=${direction}`);
      const data = await response.json();
      setClients(data);
    } catch (error) {
      console.error('Error al cargar los clientes:', error);
    }
  };

  const deleteClient = (e, id) => {
    e.stopPropagation(); // Evita que al borrar se abra el perfil del cliente
    if (window.confirm('¿Estás seguro de eliminar este cliente?')) {
      fetch(`http://localhost:8080/api/clients/${id}`, { method: 'DELETE' })
        .then(res => {
          if (res.ok) fetchClients();
        })
        .catch(err => console.error(err));
    }
  };

  const openBookingHistory = (client) => {
    setSelectedClient(client);
    setShowModal(true);
  };

  const getStatusStyle = (estado) => {
    switch (estado) {
      case 'TERMINADA': return { color: '#6c757d', fontWeight: 'bold' }; // Gris
      case 'CONFIRMADA': return { color: '#28a745', fontWeight: 'bold' }; // Verde
      case 'PRÓXIMA': return { color: '#007bff', fontWeight: 'bold' };    // Azul
      default: return { color: '#333', fontWeight: 'bold' };
    }
  };

  const filteredClients = clients.filter(client =>
    client.dni?.toLowerCase().includes(dniSearch.trim().toLowerCase())
  );

  return (
    <div className="clients-page-wrapper">
      <div className="clients-container">
        <div className="header-actions">
          <h2 className="title-list">Listado de Clientes</h2>
          <Link to="/clients/form" className="btn-new"> Nuevo Cliente</Link>
        </div>

        <div className="sort-controls">
          <label className="dni-search-label">
            Buscar por DNI:
            <span className="search-input-group">
              <input
                type="search"
                value={dniSearch}
                onChange={e => setDniSearch(e.target.value)}
                placeholder="Ej: 12345678A"
                aria-label="Buscar cliente por DNI"
              />
              {dniSearch && (
                <button type="button" className="btn-clear-search" onClick={() => setDniSearch('')}>
                  Limpiar
                </button>
              )}
            </span>
          </label>
          <label>
            Ordenar por:
            <select value={sortBy} onChange={e => setSortBy(e.target.value)}>
              <option value="id">ID</option>
              <option value="nombre">Nombre</option>
              <option value="dni">DNI</option>
              <option value="correo">Correo</option>
            </select>
          </label>
          <label>
            Dirección:
            <select value={direction} onChange={e => setDirection(e.target.value)}>
              <option value="asc">Ascendente</option>
              <option value="desc">Descendente</option>
            </select>
          </label>
        </div>

        <p className="results-counter">
          {filteredClients.length} de {clients.length} clientes
        </p>

	    <div className="table-responsive">
    <table className="clients-table">
    <thead>
    <tr>
    <th>ID</th>
    <th>DNI</th>
    <th>Nombre</th>
    <th>Teléfono</th>
    <th>Correo</th>
    <th className="text-center">Acciones</th>
    </tr>
	    </thead>
	    <tbody>
	    {filteredClients.length > 0 ? filteredClients.map(client => (
	      <tr key={client.id}>
      <td><strong>{client.id}</strong></td>
      <td>{client.dni}</td>
      <td>{client.nombre}</td>
      <td>{client.telefono}</td>
      <td>{client.correo}</td>
      <td className="text-center">
      <div className="action-group">
	      <Link to={`/clients/edit/${client.id}`} className="btn-edit">Editar</Link>

	      <button
	      onClick={() => openBookingHistory(client)}
	      className="btn-history"
	      >
	      Historial
      </button>

      <button onClick={() => deleteClient(client.id)} className="btn-delete">Eliminar</button>
      </div>
	      </td>
	      </tr>
	    )) : (
	      <tr>
	      <td colSpan="6" className="empty-table-message">
	      No hay clientes con ese DNI.
	      </td>
	      </tr>
	    )}
	    </tbody>
    </table>
    </div>
    </div>

	    {showModal && selectedClient && (
	      <div className="modal-overlay">
	      <div className="modal-content clients-history-modal">

	      <div className="history-modal-header">
	      <div>
	      <h3>Historial de Reservas: {selectedClient.nombre}</h3>
	      <p>{selectedClient.dni} · {selectedClient.telefono} · {selectedClient.correo}</p>
	      </div>
	      <button onClick={() => setShowModal(false)} className="modal-close-button">&times;</button>
	      </div>

	      <div className="booking-summary">
	      <div><strong>{totalBookings}</strong><span>Reservas</span></div>
	      <div><strong>{activeBookings}</strong><span>Activas / próximas</span></div>
	      <div><strong>{finishedBookings}</strong><span>Terminadas</span></div>
	      </div>

	      <div className="history-body">
	      {bookings.length > 0 ? (
	        <div className="table-responsive history-responsive">
	        <table className="history-table">
	        <thead>
	        <tr>
	        <th>Nº Reserva</th>
	        <th>Habitación</th>
	        <th>Tipo</th>
	        <th>Precio/noche</th>
	        <th>Noches</th>
	        <th>Total estimado</th>
	        <th>Entrada</th>
	        <th>Salida</th>
	        <th>Estado</th>
	        </tr>
	        </thead>
	        <tbody>
	        {bookings.map(book => {
	          const nights = calculateNights(book.fechaEntrada, book.fechaSalida);
	          const price = book.habitacion?.price || 0;
	          return (
	            <tr key={book.id}>
	            <td><strong>#{book.id}</strong></td>
	            <td>{book.habitacion?.number || '---'}</td>
	            <td>{book.habitacion?.type || '---'}</td>
	            <td>{formatCurrency(price)}</td>
	            <td>{nights}</td>
	            <td><strong>{formatCurrency(nights * price)}</strong></td>
	            <td>{formatDate(book.fechaEntrada)}</td>
	            <td>{formatDate(book.fechaSalida)}</td>
	            <td>
	            <span style={getStatusStyle(book.estado)}>
	            {book.estado}
	            </span>
	            </td>
	            </tr>
	          );
	        })}
	        </tbody>
	        </table>
	        </div>
	      ) : (
	        <p className="empty-history-message">Este cliente no tiene estancias registradas.</p>
	      )}
	      </div>

	      <div className="history-modal-footer">
	      <button onClick={() => setShowModal(false)} className="btn-modal-close">Cerrar</button>
	      </div>
      </div>
      </div>
    )}
    </div>
  );
};

export default Clients;
