import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Rooms.css';

const Rooms = () => {
  const [rooms, setRooms] = useState([]);
  const navigate = useNavigate();

  const fetchRooms = () => {
    const token = localStorage.getItem('user_token');

    fetch('http://localhost:8080/api/rooms', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    })
      .then(res => {
        if (!res.ok) throw new Error('No autorizado');
        return res.json();
      })
      .then(data => setRooms(data))
      .catch(err => console.error("Error cargando habitaciones:", err));
  };

  const fetchRoomBookings = (roomId) => {
    if (!roomId) return;
    const token = localStorage.getItem('user_token');

    fetch(`http://localhost:8080/api/bookings/room/${roomId}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    })
      .then(res => {
        if (!res.ok) throw new Error('No autorizado');
        return res.json();
      })
      .then(data => setRooms(data))
      .catch(err => console.error("Error cargando reservas:", err));
  };

  useEffect(() => {
    fetchRooms();
  }, []);

  return (
    <div className="rooms-page-wrapper">
      <div className="rooms-container">
        <div className="header-actions">
          <h2 className="title-list">Estado de Habitaciones ({rooms.length})</h2>
        </div>

        <div className="rooms-grid">
          {rooms.map(room => (
            <div 
              key={room.id} 
              className={`room-card ${room.status ? room.status.toLowerCase() : ''}`}
              onClick={() => navigate(`/rooms/${room.id}`)}
            >
              <div className="room-card-number">{room.number}</div>
              <div className="room-card-type">{room.type}</div>
              <div className="room-card-status">{room.status}</div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Rooms;