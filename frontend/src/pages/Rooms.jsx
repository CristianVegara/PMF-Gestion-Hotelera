import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import './Rooms.css';

const Rooms = () => {
  const [rooms, setRooms] = useState([]);

  useEffect(() => {
    fetchRooms();
  }, []);

  const fetchRooms = () => {
    fetch('/api/rooms')
      .then(res => res.json())
      .then(data => setRooms(data))
      .catch(err => console.error(err));
  };

  const deleteRoom = (id) => {
    if (window.confirm('¿Estás seguro de eliminar esta habitación?')) {
      fetch(`/api/rooms/${id}`, { method: 'DELETE' })
        .then(res => {
          if (res.ok) fetchRooms();
        })
        .catch(err => console.error(err));
    }
  };

  return (
    <div className="rooms-page-wrapper">
      <div className="rooms-container">
        <div className="header-actions">
          <h2 className="title-list">Listado de Habitaciones</h2>
          <Link to="/rooms/form" className="btn-new"> Nueva Habitación</Link>
        </div>

        <div className="table-responsive">
          <table className="rooms-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Número</th>
                <th>Tipo</th>
                <th>Precio</th>
                <th>Disponible</th>
                <th className="text-center">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {rooms.map(room => (
                <tr key={room.id}>
                  <td><strong>{room.id}</strong></td>
                  <td>{room.number}</td>
                  <td>{room.type}</td>
                  <td>{room.price.toFixed(2)}</td>
                  <td>{room.available ? 'Sí' : 'No'}</td>
                  <td className="text-center">
                    <Link to={`/rooms/edit/${room.id}`} className="btn-edit">Editar</Link>
                    <button onClick={() => deleteRoom(room.id)} className="btn-delete">Eliminar</button>
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

export default Rooms;