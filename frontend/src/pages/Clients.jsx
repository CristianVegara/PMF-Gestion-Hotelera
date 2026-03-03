import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import './Clients.css';

const Clients = () => {
  const [clients, setClients] = useState([]);

  useEffect(() => {
    fetchClients();
  }, []);

  const fetchClients = () => {
    fetch('/api/clients')
      .then(res => res.json())
      .then(data => setClients(data))
      .catch(err => console.error(err));
  };

  const deleteClient = (id) => {
    if (window.confirm('¿Estás seguro de eliminar este cliente?')) {
      fetch(`/api/clients/${id}`, { method: 'DELETE' })
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