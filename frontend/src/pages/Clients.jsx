import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import './Clients.css';

const Clients = () => {
  const [clients, setClients] = useState([]);
  const [sortBy, setSortBy] = useState('id');
  const [direction, setDirection] = useState('asc');
  
  // Estados para el Modal de Reservas
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

  const deleteClient = (id) => {
    if (window.confirm('¿Estás seguro de eliminar este cliente?')) {
      fetch(`http://localhost:8080/api/clients/${id}`, { method: 'DELETE' })
        .then(res => {
          if (res.ok) fetchClients();
        })
        .catch(err => console.error(err));
    }
  };

  // Abrir ventana de historial de reservas
  const openBookingHistory = (client) => {
    setSelectedClient(client);
    setShowModal(true);
  };

  // Colores para los estados (usando el campo 'estado' de tu JSON)
  const getStatusStyle = (estado) => {
    switch (estado) {
      case 'TERMINADA': return { color: '#6c757d', fontWeight: 'bold' }; // Gris
      case 'CONFIRMADA': return { color: '#28a745', fontWeight: 'bold' }; // Verde
      case 'PRÓXIMA': return { color: '#007bff', fontWeight: 'bold' };    // Azul
      default: return { color: '#333', fontWeight: 'bold' };
    }
  };

  return (
    <div className="clients-page-wrapper">
      <div className="clients-container">
        <div className="header-actions">
          <h2 className="title-list">Listado de Clientes</h2>
          <Link to="/clients/form" className="btn-new"> Nuevo Cliente</Link>
        </div>
        
        <div className="sort-controls">
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
              {clients.map(client => (
                <tr key={client.id}>
                  <td><strong>{client.id}</strong></td>
                  <td>{client.dni}</td>
                  <td>{client.nombre}</td>
                  <td>{client.telefono}</td>
                  <td>{client.correo}</td>
                  <td className="text-center">
                    <Link to={`/clients/edit/${client.id}`} className="btn-edit">Editar</Link>
                    
                    <button 
                      onClick={() => openBookingHistory(client)} 
                      className="btn-history"
                      style={{ 
                        margin: '0 5px', 
                        backgroundColor: '#6f42c1', 
                        color: 'white', 
                        border: 'none', 
                        padding: '5px 10px', 
                        borderRadius: '4px', 
                        cursor: 'pointer' 
                      }}
                    >
                      Historial
                    </button>

                    <button onClick={() => deleteClient(client.id)} className="btn-delete">Eliminar</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* VENTANA MODAL DE RESERVAS ACTUALIZADA */}
      {showModal && selectedClient && (
        <div className="modal-overlay" style={{ position: 'fixed', top: 0, left: 0, width: '100%', height: '100%', backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000 }}>
          <div className="modal-content" style={{ backgroundColor: 'white', padding: '25px', borderRadius: '12px', width: '95%', maxWidth: '850px', maxHeight: '80vh', overflowY: 'auto' }}>
            
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '2px solid #eee', marginBottom: '15px', paddingBottom: '10px' }}>
              <h3>Historial de Reservas: {selectedClient.nombre}</h3>
              <button onClick={() => setShowModal(false)} style={{ background: 'none', border: 'none', fontSize: '24px', cursor: 'pointer' }}>&times;</button>
            </div>
            
            <div className="history-body">
              {selectedClient.bookings && selectedClient.bookings.length > 0 ? (
                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                  <thead>
                    <tr style={{ backgroundColor: '#f8f9fa', textAlign: 'left' }}>
                      <th style={{ padding: '12px', borderBottom: '1px solid #ddd' }}>Nº Reserva</th>
                      <th style={{ padding: '12px', borderBottom: '1px solid #ddd' }}>Habitación</th>
                      <th style={{ padding: '12px', borderBottom: '1px solid #ddd' }}>Precio</th>
                      <th style={{ padding: '12px', borderBottom: '1px solid #ddd' }}>Entrada</th>
                      <th style={{ padding: '12px', borderBottom: '1px solid #ddd' }}>Salida</th>
                      <th style={{ padding: '12px', borderBottom: '1px solid #ddd' }}>Estado</th>
                    </tr>
                  </thead>
                  <tbody>
                    {selectedClient.bookings.map(book => (
                      <tr key={book.id}>
                        {/* Nº Reserva */}
                        <td style={{ padding: '12px', borderBottom: '1px solid #eee' }}>#{book.id}</td>
                        
                        {/* Habitación: Sacamos 'number' del objeto 'habitacion' */}
                        <td style={{ padding: '12px', borderBottom: '1px solid #eee' }}>
                          {book.habitacion ? book.habitacion.number : '---'}
                        </td>

                        {/* Precio: Sacamos 'price' del objeto 'habitacion' */}
                        <td style={{ padding: '12px', borderBottom: '1px solid #eee' }}>
                          {book.habitacion ? `${book.habitacion.price}€` : '0€'}
                        </td>
                        
                        <td style={{ padding: '12px', borderBottom: '1px solid #eee' }}>{new Date(book.fechaEntrada).toLocaleDateString()}</td>
                        <td style={{ padding: '12px', borderBottom: '1px solid #eee' }}>{new Date(book.fechaSalida).toLocaleDateString()}</td>
                        
                        {/* Estado: Usamos 'book.estado' que es como viene en tu JSON */}
                        <td style={{ padding: '12px', borderBottom: '1px solid #eee' }}>
                          <span style={getStatusStyle(book.estado)}>
                            {book.estado}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              ) : (
                <p style={{ textAlign: 'center', color: '#666', padding: '20px' }}>Este cliente no tiene estancias registradas.</p>
              )}
            </div>

            <div style={{ marginTop: '20px', textAlign: 'right' }}>
              <button onClick={() => setShowModal(false)} className="btn-delete" style={{ backgroundColor: '#6c757d', color: 'white', border: 'none', padding: '10px 20px', borderRadius: '4px', cursor: 'pointer' }}>Cerrar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Clients;