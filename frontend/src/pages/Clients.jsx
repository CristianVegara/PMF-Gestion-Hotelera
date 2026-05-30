import { useState, useEffect, useCallback } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Clients.css';

const Clients = () => {
  const [clients, setClients] = useState([]);
  const [sortBy, setSortBy] = useState('id');
  const [direction, setDirection] = useState('asc');
  const [dniSearch, setDniSearch] = useState('');
  
  const navigate = useNavigate();
  
  const fetchClients = useCallback(async () => {
     try {
       const token = localStorage.getItem('user_token');
       const response = await fetch(`http://localhost:8080/api/clients?sortBy=${sortBy}&direction=${direction}`, {
         headers: {
           'Authorization': `Bearer ${token}`
         }
       });
       const data = await response.json();
       setClients(Array.isArray(data) ? data : []);
     } catch (error) {
       console.error('Error al cargar los clientes:', error);
     }
  }, [sortBy, direction]);
  
  useEffect(() => {
    let active = true;
    
    const loadData = async () => {
      if (active) {
        await fetchClients();
      }
    };
    
    loadData();
    
    return () => {
      active = false;
    };
  }, [fetchClients]);

  const rawRole = localStorage.getItem('role') || '';
  const userRole = rawRole.startsWith("ROLE_") ? rawRole.replace("ROLE_", "") : rawRole;
  
  const deleteClient = async (e, id) => {
    e.stopPropagation(); 
    if (window.confirm('¿Estás seguro de eliminar este cliente?')) {
      try {
        const token = localStorage.getItem('user_token');
        const res = await fetch(`http://localhost:8080/api/clients/${id}`, { 
          method: 'DELETE',
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        if (res.ok) {
          fetchClients();
        } else {
          alert("No se pudo eliminar: asegúrate de que el cliente no tenga reservas activas.");
        }
      } catch (err) {
        console.error(err);
      }
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
          {userRole !== 'USER' && (
            <Link to="/clients/form" className="btn-new">Nuevo Cliente</Link>
          )}
        </div>
        
        <div className="sort-controls">
          <label className="dni-search-label">
            Buscar por DNI:
            <input
              type="search"
              value={dniSearch}
              onChange={e => setDniSearch(e.target.value)}
              placeholder="Ej: 12345678A"
            />
          </label>
          <label>
            Ordenar por:
            <select value={sortBy} onChange={e => setSortBy(e.target.value)}>
              <option value="id">ID</option>
              <option value="nombre">Nombre</option>
              <option value="dni">DNI</option>
            </select>
          </label>
        </div>
        
        <div className="table-responsive">
          <table className="modern-table">
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
              {filteredClients.map(client => (
                <tr 
                  key={client.id} 
                  onClick={() => navigate(`/clients/${client.id}`)} 
                  className="clickable-row"
                  title="Ver detalles del cliente"
                >
                  <td><strong>{client.id}</strong></td>
                  <td>{client.dni}</td>
                  <td>{client.nombre}</td>
                  <td>{client.telefono}</td>
                  <td>{client.correo}</td>
                  <td className="text-center">
                    <div className="action-group">
                      {userRole !== 'USER' && (
                        <button 
                          onClick={(e) => {
                            e.stopPropagation();
                            navigate(`/clients/edit/${client.id}`);
                          }}
                          className="btn-edit">
                          Editar
                        </button>
                      )}
                      {(userRole === 'ADMIN' || userRole === 'SUPERVISOR') && (
                        <button 
                          onClick={(e) => deleteClient(e, client.id)} 
                          className="btn-delete"
                        >
                          Eliminar
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Clients;