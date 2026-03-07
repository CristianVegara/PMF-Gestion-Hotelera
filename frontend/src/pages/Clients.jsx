import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import './Clients.css';

const Clients = () => {
  const [clients, setClients] = useState([]);
  const [sortBy, setSortBy] = useState('id');
  const [direction, setDirection] = useState('asc');

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
                    <button onClick={() => deleteClient(client.id)} className="btn-delete">Eliminar</button>
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