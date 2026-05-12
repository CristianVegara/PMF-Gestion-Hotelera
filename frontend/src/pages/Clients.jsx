import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Clients.css';

const Clients = () => {
  const [clients, setClients] = useState([]);
  const [sortBy, setSortBy] = useState('id');
  const [direction, setDirection] = useState('asc');
  const [dniSearch, setDniSearch] = useState('');
  const navigate = useNavigate();

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
                <tr 
                  key={client.id} 
                  onClick={() => navigate(`/clients/${client.id}`)}
                  style={{ cursor: 'pointer' }}
                  className="client-row-hover"
                >
                  <td><strong>{client.id}</strong></td>
                  <td>{client.dni}</td>
                  <td>{client.nombre}</td>
                  <td>{client.telefono}</td>
                  <td>{client.correo}</td>
                  <td className="text-center">
                    <div className="action-group">
                      <Link 
                        to={`/clients/edit/${client.id}`} 
                        className="btn-edit"
                        onClick={(e) => e.stopPropagation()} // Evita navegar al perfil al pulsar editar
                      >
                        Editar
                      </Link>
                      <button 
                        onClick={(e) => deleteClient(e, client.id)} 
                        className="btn-delete"
                      >
                        Eliminar
                      </button>
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
    </div>
  );
};

export default Clients;
